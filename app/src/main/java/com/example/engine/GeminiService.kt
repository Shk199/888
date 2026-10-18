package com.example.engine

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class GrammarWord(
    val word: String,
    val role: String,
    val explanation: String
)

data class GrammarAnalysisResult(
    val words: List<GrammarWord>,
    val summary: String,
    val hasError: Boolean,
    val poet: String?,
    val era: String?,
    val rhyme: String?
)

data class VocalizationResult(
    val sadr: String,
    val ajuz: String
)

object GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            (field.get(null) as? String) ?: ""
        } catch (_: Throwable) {
            ""
        }
    }

    private fun verifyTashkeelIntegrity(original: String, vocalized: String): Boolean {
        val strip = { s: String -> s.replace(Regex("[\\u064B-\\u0652\\u0670\\s\\p{P}]"), "") }
        return strip(original) == strip(vocalized)
    }

    suspend fun vocalizeVerse(sadr: String, ajuz: String): VocalizationResult? = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) return@withContext null

        try {
            val prompt = """
أنت عالم لغوي وعروضي خبير في الشعر العربي.
شكّل النص الشعري العربي التالي تشكيلاً كاملاً دقيقاً حسب قواعد النطق وأعراف العروض للشعر العربي.
ممنوع منعاً باتاً تغيير أو تبديل أو حذف أي حرف من حروف النص الأصلي، وممنوع إضافة أي شرح أو مقدمات.
أعد الناتج ككائن JSON يحوي صدر البيت وعجزه.
النص:
الصدر: $sadr
العجز: ${ajuz.ifBlank { "لا يوجد" }}
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                }))
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.1)
                    put("responseMimeType", "application/json")
                })
            }

            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(endpoint)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val responseBody = response.body?.string() ?: return@withContext null
            val respJson = JSONObject(responseBody)
            val candidates = respJson.optJSONArray("candidates") ?: return@withContext null
            if (candidates.length() == 0) return@withContext null
            val rawText = candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")
            val cleanJson = rawText.replace("```json", "").replace("```", "").trim()
            val parsed = JSONObject(cleanJson)

            val vocSadr = parsed.optString("sadr", sadr)
            val vocAjuz = parsed.optString("ajuz", ajuz)

            val validSadr = if (verifyTashkeelIntegrity(sadr, vocSadr)) vocSadr else sadr
            val validAjuz = if (ajuz.isNotBlank() && verifyTashkeelIntegrity(ajuz, vocAjuz)) vocAjuz else ajuz

            VocalizationResult(validSadr, validAjuz)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun analyzeGrammar(fullVerse: String): GrammarAnalysisResult? = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) return@withContext null

        try {
            val prompt = """
أعرب كلمات البيت الشعري التالي إعراباً وافياً ومختصراً حتى 6 كلمات رئيسية، مع تحديد الدور النحوي، ولخص السلامة النحوية (سليم أو فيه لحن).
كذلك وثق نسبة البيت (الشاعر، العصر، القافية) إن كان مشهوراً، وضع null لأي حقل غير مؤكد ولا تختلق نسبة أبداً.
البيت:
$fullVerse
            """.trimIndent()

            val schema = JSONObject().apply {
                put("type", "OBJECT")
                put("properties", JSONObject().apply {
                    put("words", JSONObject().apply {
                        put("type", "ARRAY")
                        put("items", JSONObject().apply {
                            put("type", "OBJECT")
                            put("properties", JSONObject().apply {
                                put("word", JSONObject().put("type", "STRING"))
                                put("role", JSONObject().put("type", "STRING"))
                                put("explanation", JSONObject().put("type", "STRING"))
                            })
                            put("required", JSONArray(listOf("word", "role", "explanation")))
                        })
                    })
                    put("summary", JSONObject().put("type", "STRING"))
                    put("hasError", JSONObject().put("type", "BOOLEAN"))
                    put("poet", JSONObject().put("type", "STRING"))
                    put("era", JSONObject().put("type", "STRING"))
                    put("rhyme", JSONObject().put("type", "STRING"))
                })
                put("required", JSONArray(listOf("words", "summary", "hasError")))
            }

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().put(JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                }))
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.1)
                    put("responseMimeType", "application/json")
                    put("responseSchema", schema)
                })
            }

            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(endpoint)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val responseBody = response.body?.string() ?: return@withContext null
            val respJson = JSONObject(responseBody)
            val candidates = respJson.optJSONArray("candidates") ?: return@withContext null
            if (candidates.length() == 0) return@withContext null
            val rawText = candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")
            val cleanJson = rawText.replace("```json", "").replace("```", "").trim()
            val parsed = JSONObject(cleanJson)

            val wordsList = mutableListOf<GrammarWord>()
            val wordsArray = parsed.optJSONArray("words")
            if (wordsArray != null) {
                for (i in 0 until minOf(6, wordsArray.length())) {
                    val wObj = wordsArray.getJSONObject(i)
                    wordsList.add(
                        GrammarWord(
                            word = wObj.optString("word", ""),
                            role = wObj.optString("role", ""),
                            explanation = wObj.optString("explanation", "")
                        )
                    )
                }
            }

            GrammarAnalysisResult(
                words = wordsList,
                summary = parsed.optString("summary", "البيت خالٍ من اللحن ومستقيم التراكيب."),
                hasError = parsed.optBoolean("hasError", false),
                poet = parsed.optString("poet").takeIf { it.isNotBlank() && it != "null" },
                era = parsed.optString("era").takeIf { it.isNotBlank() && it != "null" },
                rhyme = parsed.optString("rhyme").takeIf { it.isNotBlank() && it != "null" }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
