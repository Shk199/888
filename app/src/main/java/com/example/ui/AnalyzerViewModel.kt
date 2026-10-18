package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.SavedVerse
import com.example.engine.ArudiEngine
import com.example.engine.GeminiService
import com.example.engine.GrammarAnalysisResult
import com.example.engine.MeterDetector
import com.example.engine.MeterMatchResult
import com.example.engine.ShatrAnalysis
import com.example.engine.InputValidator
import com.example.engine.ValidationResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FullAnalysisData(
    val inputRaw: String,
    val vocalizedSadr: String,
    val vocalizedAjuz: String,
    val meterResult: MeterMatchResult,
    val sadrAnalysis: ShatrAnalysis,
    val ajuzAnalysis: ShatrAnalysis?,
    val grammarData: GrammarAnalysisResult?,
    val timestamp: Long = System.currentTimeMillis()
)

class AnalyzerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val savedVerseDao = db.savedVerseDao()

    val savedVerses = savedVerseDao.getAllVerses()

    val sampleVerse = "عَلى قَدْرِ أَهْلِ العَزْمِ تَأْتِي العَزَائِمُ ... وَتَأْتِي عَلَى قَدْرِ الكِرَامِ المَكَارِمُ"

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _optMeter = MutableStateFlow(true)
    val optMeter: StateFlow<Boolean> = _optMeter.asStateFlow()

    private val _optDiacritics = MutableStateFlow(true)
    val optDiacritics: StateFlow<Boolean> = _optDiacritics.asStateFlow()

    private val _optGrammar = MutableStateFlow(true)
    val optGrammar: StateFlow<Boolean> = _optGrammar.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisData = MutableStateFlow<FullAnalysisData?>(null)
    val analysisData: StateFlow<FullAnalysisData?> = _analysisData.asStateFlow()

    private val _symbolsMode = MutableStateFlow("// o") // "// o" or "1 0"
    val symbolsMode: StateFlow<String> = _symbolsMode.asStateFlow()

    private val _isEditingTashkeel = MutableStateFlow(false)
    val isEditingTashkeel: StateFlow<Boolean> = _isEditingTashkeel.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val _inputError = MutableStateFlow<String?>(null)
    val inputError: StateFlow<String?> = _inputError.asStateFlow()

    init {
        MeterDetector.initialize(application)
    }

    fun validateInput(text: String): ValidationResult {
        return InputValidator.validateInput(text)
    }

    fun setInputText(text: String) {
        _inputText.value = text
        if (_inputError.value != null) {
            _inputError.value = null
        }
    }

    fun setOptMeter(value: Boolean) {
        _optMeter.value = value
    }

    fun setOptDiacritics(value: Boolean) {
        _optDiacritics.value = value
    }

    fun setOptGrammar(value: Boolean) {
        _optGrammar.value = value
    }

    fun insertSample() {
        _inputText.value = sampleVerse
        _inputError.value = null
        showToast("تم إدراج بيت المتنبي الشهير")
    }

    fun clearInput() {
        _inputText.value = ""
        _analysisData.value = null
        _isEditingTashkeel.value = false
        _inputError.value = null
        showToast("تم مسح الحقل والنتائج")
    }

    fun toggleSymbolsMode() {
        _symbolsMode.value = if (_symbolsMode.value == "// o") "1 0" else "// o"
    }

    fun setEditingTashkeel(editing: Boolean) {
        _isEditingTashkeel.value = editing
    }


    private fun showToast(msg: String) {
        viewModelScope.launch {
            _toastEvent.emit(msg)
        }
    }

    private fun splitVerse(raw: String): Pair<String, String> {
        val text = raw.trim()
        val separators = listOf("...", "…", "/", "–", "-")
        for (sep in separators) {
            if (text.contains(sep)) {
                val parts = text.split(sep, limit = 2)
                return Pair(parts[0].trim(), parts.getOrElse(1) { "" }.trim())
            }
        }
        if (text.contains("\n")) {
            val lines = text.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
            return Pair(lines.getOrElse(0) { "" }, lines.getOrElse(1) { "" })
        }
        val words = text.split(Regex("\\s+"))
        return if (words.size >= 6) {
            val mid = words.size / 2
            Pair(words.take(mid).joinToString(" "), words.drop(mid).joinToString(" "))
        } else {
            Pair(text, "")
        }
    }

    fun runAnalysis(overrideText: String? = null) {
        val rawInput = overrideText ?: _inputText.value
        val validation = validateInput(rawInput)

        if (validation is ValidationResult.Error) {
            _analysisData.value = null
            _isAnalyzing.value = false
            _isEditingTashkeel.value = false
            _inputError.value = validation.message
            if (validation.hadLinks) {
                showToast("أزلت الروابط من النص.")
            }
            showToast(validation.message)
            return
        }

        val valid = validation as ValidationResult.Valid
        _inputError.value = null
        if (valid.hadLinks) {
            showToast("أزلت الروابط من النص.")
        }
        val targetText = valid.cleanedText

        viewModelScope.launch {
            _isAnalyzing.value = true
            _analysisData.value = null
            _isEditingTashkeel.value = false

            try {
                val (rawSadr, rawAjuz) = splitVerse(targetText)

                var vocalizedSadr = rawSadr
                var vocalizedAjuz = rawAjuz

                val ratioSadr = ArudiEngine.calculateVocalizationRatio(rawSadr)
                val ratioAjuz = if (rawAjuz.isNotEmpty()) ArudiEngine.calculateVocalizationRatio(rawAjuz) else 1f
                val avgRatio = if (rawAjuz.isNotEmpty()) (ratioSadr + ratioAjuz) / 2f else ratioSadr

                // 1. التشكيل عبر الذكاء الاصطناعي إن لم تكن النسبة كافية
                if (avgRatio < 0.85f && _optDiacritics.value) {
                    val vocRes = GeminiService.vocalizeVerse(rawSadr, rawAjuz)
                    if (vocRes != null) {
                        vocalizedSadr = vocRes.sadr
                        if (rawAjuz.isNotEmpty() && vocRes.ajuz.isNotEmpty()) {
                            vocalizedAjuz = vocRes.ajuz
                        }
                    }
                }

                // 2. المحرك العروضي الحتمي
                val sadrAnalysis = ArudiEngine.toArudi(vocalizedSadr, false)
                val ajuzAnalysis = if (vocalizedAjuz.isNotEmpty()) ArudiEngine.toArudi(vocalizedAjuz, true) else null
                val meterResult = MeterDetector.detectMeter(sadrAnalysis, ajuzAnalysis)

                // 3. الإعراب والنحو
                var grammarData: GrammarAnalysisResult? = null
                if (_optGrammar.value) {
                    val fullVerse = if (vocalizedAjuz.isNotEmpty()) "$vocalizedSadr ... $vocalizedAjuz" else vocalizedSadr
                    grammarData = GeminiService.analyzeGrammar(fullVerse)
                }

                val resultData = FullAnalysisData(
                    inputRaw = targetText,
                    vocalizedSadr = vocalizedSadr,
                    vocalizedAjuz = vocalizedAjuz,
                    meterResult = meterResult,
                    sadrAnalysis = sadrAnalysis,
                    ajuzAnalysis = ajuzAnalysis,
                    grammarData = grammarData
                )
                _analysisData.value = resultData

                if (meterResult.isSound) {
                    showToast("اكتمل ميزان البيت بنجاح وسلامة عروضية ✓")
                } else {
                    showToast("الوزن به انكسار عروضي، تم استخراج أقرب البحور")
                }


            } catch (e: Exception) {
                e.printStackTrace()
                showToast("حدث خطأ أثناء معالجة البيت، يرجى المحاولة ثانية")
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun saveCurrentVerse() {
        val current = _analysisData.value ?: return
        viewModelScope.launch {
            val verseText = if (current.vocalizedAjuz.isNotEmpty()) {
                "${current.vocalizedSadr} ... ${current.vocalizedAjuz}"
            } else {
                current.vocalizedSadr
            }
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateStr = formatter.format(Date(current.timestamp))

            savedVerseDao.insertVerse(
                SavedVerse(
                    verse = verseText,
                    meterName = current.meterResult.fullMeterTitle,
                    date = dateStr,
                    timestamp = current.timestamp
                )
            )
            showToast("تم حفظ البيت في المحفوظات بنجاح ✓")
        }
    }

    fun deleteSavedVerse(id: Long) {
        viewModelScope.launch {
            savedVerseDao.deleteVerse(id)
            showToast("تم حذف البيت من المحفوظات")
        }
    }

    fun clearAllSaved() {
        viewModelScope.launch {
            savedVerseDao.clearAll()
            showToast("تم إفراغ قائمة المحفوظات بالكامل")
        }
    }
}
