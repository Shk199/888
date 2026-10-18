package com.example

import com.example.engine.ArudiEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testArudiConversion() {
        val analysis = ArudiEngine.toArudi("عَلى قَدْرِ أَهْلِ العَزْمِ تَأْتِي العَزَائِمُ", false)
        assertTrue(analysis.binaryPattern.isNotEmpty())
        val symbols = ArudiEngine.binaryToTraditionalSymbols(analysis.binaryPattern)
        assertTrue(symbols.contains("/") || symbols.contains("o"))
    }

    @Test
    fun testValidateInput() {
        // 1. فارغ
        val emptyRes = com.example.engine.InputValidator.validateInput("   ")
        assertTrue(emptyRes is com.example.engine.ValidationResult.Error)
        assertEquals("اكتب بيتًا شعريًا أولًا.", (emptyRes as com.example.engine.ValidationResult.Error).message)

        // 2. إنجليزي / لاتيني بالكامل
        val englishRes = com.example.engine.InputValidator.validateInput("Hello world this is an English text")
        assertTrue(englishRes is com.example.engine.ValidationResult.Error)
        assertEquals("التطبيق يحلل الشعر العربي فقط. أدخل بيتًا بالعربية.", (englishRes as com.example.engine.ValidationResult.Error).message)

        // 3. حروف عربية أقل من 8
        val shortArabicRes = com.example.engine.InputValidator.validateInput("سلام")
        assertTrue(shortArabicRes is com.example.engine.ValidationResult.Error)
        assertEquals("أدخل بيتًا كاملًا بالعربية: لم أجد حروفًا عربية كافية.", (shortArabicRes as com.example.engine.ValidationResult.Error).message)

        // 4. كلمات لاتينية 3 أو أكثر في نص عربي
        val mixedRes = com.example.engine.InputValidator.validateInput("هذا البيت الشعري الجميل test one two")
        assertTrue(mixedRes is com.example.engine.ValidationResult.Error)
        assertEquals("النص يحتوي كلمات غير عربية. أدخل بيتًا بالعربية فقط.", (mixedRes as com.example.engine.ValidationResult.Error).message)

        // 5. روابط مع نص عربي صحيح
        val linkRes = com.example.engine.InputValidator.validateInput("عَلى قَدْرِ أَهْلِ العَزْمِ تَأْتِي العَزَائِمُ ... وَتَأْتِي عَلَى قَدْرِ الكِرَامِ المَكَارِمُ https://example.com")
        assertTrue(linkRes is com.example.engine.ValidationResult.Valid)
        val validLink = linkRes as com.example.engine.ValidationResult.Valid
        assertTrue(validLink.hadLinks)
        assertFalse(validLink.cleanedText.contains("http"))

        // 6. بيت شعري صحيح
        val validVerseRes = com.example.engine.InputValidator.validateInput("عَلى قَدْرِ أَهْلِ العَزْمِ تَأْتِي العَزَائِمُ ... وَتَأْتِي عَلَى قَدْرِ الكِرَامِ المَكَارِمُ")
        assertTrue(validVerseRes is com.example.engine.ValidationResult.Valid)
        val validVerse = validVerseRes as com.example.engine.ValidationResult.Valid
        assertTrue(validVerse.arabicCount >= 8)
        assertTrue(validVerse.vocalizationRatio > 0f)
    }
}

