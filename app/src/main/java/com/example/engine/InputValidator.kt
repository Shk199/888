package com.example.engine

sealed class ValidationResult {
    data class Valid(
        val cleanedText: String,
        val hadLinks: Boolean,
        val arabicCount: Int,
        val vocalizationRatio: Float
    ) : ValidationResult()

    data class Error(
        val message: String,
        val hadLinks: Boolean = false
    ) : ValidationResult()
}

object InputValidator {

    private val URL_REGEX = Regex("""(?i)\b(?:https?://|www\.)\S+""")
    private val LATIN_WORD_REGEX = Regex("""[a-zA-Z]""")

    fun validateInput(rawText: String): ValidationResult {
        val trimmedRaw = rawText.trim()

        // 1. فحص الروابط وحذفها إن وجدت
        val hadLinks = URL_REGEX.containsMatchIn(rawText)
        val textWithoutLinks = if (hadLinks) {
            rawText.replace(URL_REGEX, " ").trim()
        } else {
            trimmedRaw
        }

        // 1) فارغ (بعد حذف المسافات)
        if (textWithoutLinks.isEmpty()) {
            return ValidationResult.Error("اكتب بيتًا شعريًا أولًا.", hadLinks = hadLinks)
        }

        // إزالة الرموز التعبيرية (Emojis)
        val textWithoutEmojis = removeEmojis(textWithoutLinks)

        // عدّ الحروف العربية الأساسية فقط (من ا إلى ي في نطاق Unicode U+0621 إلى U+064A، دون التشكيل والأرقام والرموز)
        var arabicLettersCount = 0
        var harakatCount = 0
        var otherAlphabetLettersCount = 0

        for (ch in textWithoutEmojis) {
            if (ch in '\u0621'..'\u064A') {
                arabicLettersCount++
            } else if (ch in '\u064B'..'\u0652' || ch == '\u0670') {
                harakatCount++
            } else if (Character.isLetter(ch)) {
                otherAlphabetLettersCount++
            }
        }

        val totalAlphabetLetters = arabicLettersCount + otherAlphabetLettersCount

        // 4) إن كان النص كله لاتينيًا أو لغة أخرى بلا أي حرف عربي
        if (arabicLettersCount == 0) {
            return ValidationResult.Error("التطبيق يحلل الشعر العربي فقط. أدخل بيتًا بالعربية.", hadLinks = hadLinks)
        }

        // 2) إن كان عدد الحروف العربية الأساسية أقل من 8 حروف
        if (arabicLettersCount < 8) {
            return ValidationResult.Error("أدخل بيتًا كاملًا بالعربية: لم أجد حروفًا عربية كافية.", hadLinks = hadLinks)
        }

        // 3) نسبة الحروف العربية إلى مجموع الحروف الهجائية، وفحص الكلمات اللاتينية
        val words = textWithoutEmojis.split(Regex("\\s+")).filter { it.isNotBlank() }
        val latinWordsCount = words.count { LATIN_WORD_REGEX.containsMatchIn(it) }

        val arabicRatio = if (totalAlphabetLetters > 0) {
            arabicLettersCount.toDouble() / totalAlphabetLetters
        } else {
            1.0
        }

        if (arabicRatio < 0.80 || latinWordsCount >= 3) {
            return ValidationResult.Error("النص يحتوي كلمات غير عربية. أدخل بيتًا بالعربية فقط.", hadLinks = hadLinks)
        }

        // 6) التشكيل: حساب نسبة الحروف العربية التي عليها حركة
        val vocalizationRatio = if (arabicLettersCount > 0) {
            harakatCount.toFloat() / arabicLettersCount
        } else {
            0f
        }

        // تجهيز النص المحلَّل بحذف الأرقام والرموز التعبيرية والروابط وعلامات الترقيم غير الفاصلة
        val cleanedForAnalysis = cleanTextForAnalysis(textWithoutEmojis)

        return ValidationResult.Valid(
            cleanedText = cleanedForAnalysis,
            hadLinks = hadLinks,
            arabicCount = arabicLettersCount,
            vocalizationRatio = vocalizationRatio
        )
    }

    private fun cleanTextForAnalysis(text: String): String {
        val sb = StringBuilder()
        // الحفاظ على فواصل الشطرين: ... أو … أو / أو - أو – أو السطر الجديد، والحروف العربية والحركات
        for (ch in text) {
            when {
                ch in '\u0621'..'\u064A' -> sb.append(ch)
                ch in '\u064B'..'\u0652' || ch == '\u0670' -> sb.append(ch)
                ch == ' ' || ch == '\n' || ch == '\t' -> sb.append(ch)
                ch in listOf('.', '…', '/', '–', '-') -> sb.append(ch)
                Character.isDigit(ch) -> { /* تجاهل الأرقام */ }
                ch in '\u0660'..'\u0669' || ch in '\u06F0'..'\u06F9' -> { /* تجاهل الأرقام العربية والفارسية */ }
                else -> { /* تجاهل علامات الترقيم الأخرى */ }
            }
        }
        return sb.toString().replace(Regex("[ \\t]+"), " ").trim()
    }

    private fun removeEmojis(input: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < input.length) {
            val codePoint = input.codePointAt(i)
            val charCount = Character.charCount(codePoint)
            if (!isEmojiCodePoint(codePoint)) {
                sb.appendCodePoint(codePoint)
            }
            i += charCount
        }
        return sb.toString()
    }

    private fun isEmojiCodePoint(codePoint: Int): Boolean {
        return (codePoint in 0x1F600..0x1F64F) || // Emoticons
               (codePoint in 0x1F300..0x1F5FF) || // Misc Symbols and Pictographs
               (codePoint in 0x1F680..0x1F6FF) || // Transport and Map
               (codePoint in 0x1F700..0x1F77F) || // Alchemical Symbols
               (codePoint in 0x1F780..0x1F7FF) || // Geometric Shapes Extended
               (codePoint in 0x1F800..0x1F8FF) || // Supplemental Arrows-C
               (codePoint in 0x1F900..0x1F9FF) || // Supplemental Symbols and Pictographs
               (codePoint in 0x1FA00..0x1FA6F) || // Chess Symbols
               (codePoint in 0x1FA70..0x1FAFF) || // Symbols and Pictographs Extended-A
               (codePoint in 0x2600..0x26FF)   || // Misc symbols
               (codePoint in 0x2700..0x27BF)   || // Dingbats
               (codePoint in 0xFE00..0xFE0F)   || // Variation Selectors
               (codePoint in 0x1F1E6..0x1F1FF)    // Flags
    }
}
