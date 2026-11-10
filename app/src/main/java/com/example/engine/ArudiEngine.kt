package com.example.engine

object ArudiEngine {

    val LONG_VOWELS = setOf('ا', 'و', 'ي', 'ى', 'آ')
    val SOLAR_LETTERS = setOf('ت', 'ث', 'د', 'ذ', 'ر', 'ز', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ل', 'ن')

    const val FATHA = "\u064E"
    const val DAMMA = "\u064F"
    const val KASRA = "\u0650"
    const val SUKUN = "\u0652"
    const val SHADDA = "\u0651"
    const val TANWEEN_FATH = "\u064B"
    const val TANWEEN_DAMM = "\u064C"
    const val TANWEEN_KASR = "\u064D"
    const val DAGGER_ALIF = "\u0670"

    private val PRONUNCIATION_SUBSTITUTIONS = mapOf(
        "هذا" to "هَاذَا",
        "هَذَا" to "هَاذَا",
        "هذه" to "هَاذِهِي",
        "هَذِهِ" to "هَاذِهِي",
        "هذان" to "هَاذَانِ",
        "هَذَانِ" to "هَاذَانِ",
        "هذين" to "هَاذَيْنِ",
        "هَذَيْنِ" to "هَاذَيْنِ",
        "هؤلاء" to "هَاؤُلَاءِ",
        "هَؤُلَاءِ" to "هَاؤُلَاءِ",
        "ذلك" to "ذَالِكَ",
        "ذَلِكَ" to "ذَالِكَ",
        "ذلكما" to "ذَالِكُمَا",
        "ذلكم" to "ذَالِكُمْ",
        "ذَلِكُمْ" to "ذَالِكُمْ",
        "أولئك" to "أُلَائِكَ",
        "أُولَئِكَ" to "أُلَائِكَ",
        "لكن" to "لَاكِنْ",
        "لَكِنْ" to "لَاكِنْ",
        "لكنّ" to "لَاكِنْنَ",
        "لَكِنَّ" to "لَاكِنْنَ",
        "الله" to "اللَّاهِ",
        "اللّه" to "اللَّاهِ",
        "اللَّه" to "اللَّاهِ",
        "اللَّهِ" to "اللَّاهِ",
        "الرحمن" to "الرَّحْمَان",
        "الرَّحْمَن" to "الرَّحْمَان",
        "طه" to "طَاهَا",
        "إله" to "إِلَاه",
        "إِلَه" to "إِلَاه",
        "داود" to "دَاوُود",
        "دَاوُد" to "دَاوُود",
        "السماوات" to "السَّمَاوَات",
        "السَّمَوَات" to "السَّمَاوَات",
        "عمرو" to "عَمْر",
        "عَمْرٌو" to "عَمْرُنْ",
        "مائة" to "مِئَة",
        "مِائَة" to "مِئَة"
    )

    fun normalizeVerseText(text: String): String {
        return text
            .replace(Regex("[ـ\\u0640]"), "") // Tatweel / Kashida
            .replace(Regex("[\\u200B-\\u200D\\uFEFF]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun calculateVocalizationRatio(text: String): Float {
        var charCount = 0
        var vowelCount = 0
        for (ch in text) {
            if (ch in '\u0621'..'\u064A') {
                charCount++
            } else if (ch in '\u064B'..'\u0652' || ch == '\u0670') {
                vowelCount++
            }
        }
        return if (charCount > 0) vowelCount.toFloat() / charCount else 0f
    }

    fun toArudi(shatrRaw: String, isLastShatr: Boolean = true): ShatrAnalysis {
        val cleaned = normalizeVerseText(shatrRaw)
        if (cleaned.isEmpty()) {
            return ShatrAnalysis(
                originalText = "",
                words = emptyList(),
                phoneticLine = "",
                binaryPattern = "",
                hasOptionalIshba = false,
                alternativePatterns = emptyList()
            )
        }

        val rawWords = cleaned.split(Regex("\\s+")).filter { it.isNotEmpty() }
        val processedWords = mutableListOf<ArudiWord>()

        for (wIndex in rawWords.indices) {
            var word = rawWords[wIndex]
            val isFirstWordInShatr = (wIndex == 0)
            val isLastWordInShatr = (wIndex == rawWords.size - 1)
            var prevWord = if (wIndex > 0) processedWords[wIndex - 1] else null

            // 1. استبدال الكلمات الاستثنائية
            val cleanNoDiacritics = word.replace(Regex("[\\u064B-\\u0652\\u0670]"), "")
            var isSubstituted = false
            if (PRONUNCIATION_SUBSTITUTIONS.containsKey(word)) {
                word = PRONUNCIATION_SUBSTITUTIONS[word]!!
                isSubstituted = true
            } else if (PRONUNCIATION_SUBSTITUTIONS.containsKey(cleanNoDiacritics)) {
                word = PRONUNCIATION_SUBSTITUTIONS[cleanNoDiacritics]!!
                isSubstituted = true
            }

            // 2. ألف واو الجماعة
            word = word.replace(Regex("ُوَا?(\\b|$)"), "ُو$1").replace("ُوا", "ُو")

            // 3. أل التعريف والهمزات وحروف العطف والجر المتصلة
            var solarConsonantToAttach: Char? = null
            var lunarLamToAttach = false

            if (!isSubstituted) {
                val isLil = cleanNoDiacritics.startsWith("لل")
                val particleMatch = if (isLil) null else Regex("^([وفبك])([\\u064B-\\u0652\\u0670])?ال").find(word)
                if (isLil) {
                    val cleanAfterAl = cleanNoDiacritics.substring(2)
                    val firstCharAfterAl = cleanAfterAl.firstOrNull() ?: ' '
                    val isSolar = SOLAR_LETTERS.contains(firstCharAfterAl)
                    val afterAlText = word.replace(Regex("^ل[\\u064B-\\u0652\\u0670]?ل[\\u064B-\\u0652\\u0670]?"), "")
                    word = if (isSolar) {
                        val solarVowel = afterAlText.firstOrNull { it in listOf('\u064E', '\u064F', '\u0650') }?.toString() ?: FATHA
                        val cleanChar = afterAlText.replace(Regex("^[\\u064B-\\u0652\\u0670\\u0651]"), "")
                        val charBase = cleanChar.firstOrNull() ?: firstCharAfterAl
                        val restOfWord = afterAlText.replace(Regex("^[أ-ي][\\u064B-\\u0652\\u0670\\u0651]*"), "")
                        "لِ${charBase}ْ$charBase$solarVowel$restOfWord"
                    } else {
                        "لِلْ" + afterAlText.replace(Regex("^[\\u064B-\\u0652\\u0670]"), "")
                    }
                } else if (particleMatch != null) {
                        val prefixLetter = particleMatch.groupValues[1]
                        val prefixHarakah = particleMatch.groupValues.getOrNull(2)?.ifEmpty { null }
                            ?: if (prefixLetter == "ب") KASRA else FATHA
                        val afterAlText = word.replace(Regex("^[وفبك][\\u064B-\\u0652\\u0670]?ال"), "")
                        val cleanAfterAl = cleanNoDiacritics.replace(Regex("^[وفبك]ال"), "")
                        val firstCharAfterAl = cleanAfterAl.firstOrNull() ?: ' '
                        val isSolar = SOLAR_LETTERS.contains(firstCharAfterAl)

                        word = if (isSolar) {
                            val solarVowel = afterAlText.firstOrNull { it in listOf('\u064E', '\u064F', '\u0650') }?.toString() ?: FATHA
                            val cleanChar = afterAlText.replace(Regex("^[\\u064B-\\u0652\\u0670\\u0651]"), "")
                            val charBase = cleanChar.firstOrNull() ?: firstCharAfterAl
                            val restOfWord = cleanChar.replace(Regex("^[أ-ي][\\u064B-\\u0652\\u0670\\u0651]*"), "")
                            "$prefixLetter$prefixHarakah${charBase}ْ$charBase$solarVowel$restOfWord"
                        } else {
                            "$prefixLetter$prefixHarakah" + "لْ" + afterAlText
                        }
                } else if (Regex("^[وف]([\\u064E\\u064F\\u0650])?ا([سشصضطظتثدذرزمخغفقلنهب])").containsMatchIn(word)) {
                    // حروف العطف (فـ / وـ) المتصلة بهمزة وصل غير "ال" (مثل: فَاسْتَرَاحَ، وَاسْتَغْفَرَ)
                    val conj = word.first().toString()
                    val afterConj = word.substring(1).replace(Regex("^[\\u064E\\u064F\\u0650]"), "")
                    if (afterConj.startsWith("ا") && !afterConj.startsWith("ال")) {
                        word = "$conj$FATHA" + afterConj.substring(1)
                    }
                } else if (cleanNoDiacritics.startsWith("ال")) {
                    val isRelativePronoun = cleanNoDiacritics in setOf("التي", "الذي", "الذين", "اللاتي", "اللائي", "اللواتي", "اللذان", "اللتان")
                    if (isRelativePronoun) {
                        val baseAfterDoubleLam = cleanNoDiacritics.substring(2)
                        if (!isFirstWordInShatr && prevWord != null) {
                            lunarLamToAttach = true
                            word = "لَ$baseAfterDoubleLam"
                        } else {
                            word = "أَلْلَ$baseAfterDoubleLam"
                        }
                    } else {
                        val nextLetterMatch = cleanNoDiacritics.replace(Regex("^ال"), "").firstOrNull()
                        val isSolar = nextLetterMatch != null && SOLAR_LETTERS.contains(nextLetterMatch)

                        if (isSolar) {
                            val afterAl = word.replace(Regex("^ال[\\u064B-\\u0652\\u0670]?"), "")
                            val solarVowel = afterAl.firstOrNull { it in listOf('\u064E', '\u064F', '\u0650') }?.toString() ?: FATHA
                            val cleanChar = afterAl.replace(Regex("^[\\u064B-\\u0652\\u0670]"), "")
                            val firstLetter = cleanChar.firstOrNull() ?: nextLetterMatch!!
                            val restOfWord = if (cleanChar.length > 1) cleanChar.substring(1) else ""

                            if (!isFirstWordInShatr && prevWord != null) {
                                solarConsonantToAttach = firstLetter
                                word = "$firstLetter$solarVowel$restOfWord"
                            } else {
                                word = "أَ$firstLetter$SHADDA$restOfWord"
                            }
                        } else {
                            val afterAl = word.replace(Regex("^ال[\\u064B-\\u0652\\u0670]?"), "")
                            if (!isFirstWordInShatr && prevWord != null) {
                                lunarLamToAttach = true
                                word = afterAl
                            } else {
                                word = "أَلْ$afterAl"
                            }
                        }
                    }
                } else if (Regex("^ا[^ل\\u064E\\u064F\\u0650\\u0652]").containsMatchIn(word)) {
                    word = if (isFirstWordInShatr) {
                        "إِ" + word.substring(1)
                    } else {
                        word.substring(1)
                    }
                }
            }

            // 4. التقاء الساكنين وإدغام اللام الشمسية / القمرية في الكلمة السابقة
            if (prevWord != null && prevWord.syllables.isNotEmpty()) {
                val lastSyl = prevWord.syllables.last()
                val isPrevMadd = lastSyl.bit == '0' && LONG_VOWELS.contains(lastSyl.letter.firstOrNull())
                val needDropMadd = isPrevMadd && (solarConsonantToAttach != null || lunarLamToAttach || word.startsWith("لْ") || Regex("^[أ-ي]ْ").containsMatchIn(word))

                var updatedSyls = if (needDropMadd) prevWord.syllables.dropLast(1).toMutableList() else prevWord.syllables.toMutableList()
                var updatedPhonetic = if (needDropMadd) prevWord.phoneticText.replace(Regex("[اويةى]ْ?$"), "") else prevWord.phoneticText

                if (solarConsonantToAttach != null) {
                    val sc = solarConsonantToAttach.toString()
                    updatedSyls.add(ArudiSyllable(sc, SUKUN, '0', prevWord.originalText.length))
                    updatedPhonetic += "${sc}ْ"
                } else if (lunarLamToAttach) {
                    updatedSyls.add(ArudiSyllable("ل", SUKUN, '0', prevWord.originalText.length))
                    updatedPhonetic += "لْ"
                }

                val updatedBinary = updatedSyls.map { it.bit }.joinToString("")
                prevWord = prevWord.copy(
                    syllables = updatedSyls,
                    binaryPattern = updatedBinary,
                    phoneticText = updatedPhonetic
                )
                processedWords[wIndex - 1] = prevWord
            }

            // 5. فك الشدة والتنوين وبناء المقاطع
            val syllables = mutableListOf<ArudiSyllable>()
            var phoneticWord = ""

            var i = 0
            while (i < word.length) {
                val char = word[i]
                val next = if (i + 1 < word.length) word[i + 1] else ' '
                val nextNext = if (i + 2 < word.length) word[i + 2] else ' '

                if (char.toString().matches(Regex("[\\u064B-\\u0652\\u0670]"))) {
                    i++
                    continue
                }

                var harakah = ""
                var isShaddah = false
                var shaddahVowel = FATHA

                if (next.toString() == SHADDA) {
                    isShaddah = true
                    shaddahVowel = if (nextNext.toString().matches(Regex("[\\u064B-\\u0650\\u0670]"))) nextNext.toString() else FATHA
                } else if (nextNext.toString() == SHADDA && next.toString().matches(Regex("[\\u064B-\\u0650\\u0670]"))) {
                    isShaddah = true
                    shaddahVowel = next.toString()
                } else if (next.toString().matches(Regex("[\\u064B-\\u0652\\u0670]"))) {
                    harakah = next.toString()
                }

                if (isShaddah) {
                    syllables.add(ArudiSyllable(char.toString(), SUKUN, '0', i))
                    syllables.add(ArudiSyllable(char.toString(), shaddahVowel, '1', i))
                    phoneticWord += "${char}ْ$char$shaddahVowel"
                    i++
                    continue
                }

                if (harakah == TANWEEN_DAMM) {
                    syllables.add(ArudiSyllable(char.toString(), DAMMA, '1', i))
                    syllables.add(ArudiSyllable("ن", SUKUN, '0', i))
                    phoneticWord += "$char${DAMMA}نْ"
                    i++
                    continue
                } else if (harakah == TANWEEN_KASR) {
                    syllables.add(ArudiSyllable(char.toString(), KASRA, '1', i))
                    syllables.add(ArudiSyllable("ن", SUKUN, '0', i))
                    phoneticWord += "$char${KASRA}نْ"
                    i++
                    continue
                } else if (harakah == TANWEEN_FATH) {
                    syllables.add(ArudiSyllable(char.toString(), FATHA, '1', i))
                    syllables.add(ArudiSyllable("ن", SUKUN, '0', i))
                    phoneticWord += "$char${FATHA}نْ"
                    i++
                    while (i < word.length && word[i].toString().matches(Regex("[\\u064B-\\u0652\\u0670]"))) {
                        i++
                    }
                    if (i < word.length && (word[i] == 'ا' || word[i] == 'ى')) {
                        val afterAlif = if (i + 1 < word.length) word[i + 1] else ' '
                        if (!afterAlif.toString().matches(Regex("[\\u064E\\u064F\\u0650\\u0651]"))) {
                            i++
                        }
                    }
                    continue
                }

                if (LONG_VOWELS.contains(char)) {
                    if (harakah.isEmpty() || harakah == SUKUN) {
                        syllables.add(ArudiSyllable(char.toString(), SUKUN, '0', i))
                        phoneticWord += "${char}ْ"
                        i++
                        continue
                    }
                }

                if (harakah == SUKUN) {
                    syllables.add(ArudiSyllable(char.toString(), SUKUN, '0', i))
                    phoneticWord += "${char}ْ"
                    i++
                    continue
                }

                val h = harakah.ifEmpty { FATHA }
                syllables.add(ArudiSyllable(char.toString(), h, '1', i))
                phoneticWord += "$char$h"
                i++
            }

            // 6. إشباع آخر الشطر
            if (isLastWordInShatr && syllables.isNotEmpty()) {
                val lastSyl = syllables.last()
                if (lastSyl.bit == '1') {
                    when (lastSyl.harakah) {
                        DAMMA -> {
                            syllables.add(ArudiSyllable("و", SUKUN, '0', word.length))
                            phoneticWord += "و"
                        }
                        KASRA -> {
                            syllables.add(ArudiSyllable("ي", SUKUN, '0', word.length))
                            phoneticWord += "ي"
                        }
                        FATHA -> {
                            syllables.add(ArudiSyllable("ا", SUKUN, '0', word.length))
                            phoneticWord += "ا"
                        }
                    }
                }
            }

            val binaryPattern = syllables.map { it.bit }.joinToString("")
            processedWords.add(
                ArudiWord(
                    originalText = rawWords[wIndex],
                    phoneticText = phoneticWord,
                    syllables = syllables,
                    binaryPattern = binaryPattern
                )
            )
        }

        val fullBinary = processedWords.map { it.binaryPattern }.joinToString("")
        val fullPhonetic = processedWords.map { it.phoneticText }.joinToString(" ")

        val altPatterns = mutableListOf<String>()
        altPatterns.add(fullBinary)
        if (!fullBinary.endsWith("0") && fullBinary.length > 3) {
            altPatterns.add(fullBinary + "0")
        } else if (fullBinary.endsWith("0") && fullBinary.length > 3) {
            altPatterns.add(fullBinary.dropLast(1))
        }

        return ShatrAnalysis(
            originalText = cleaned,
            words = processedWords,
            phoneticLine = fullPhonetic,
            binaryPattern = fullBinary,
            hasOptionalIshba = altPatterns.size > 1,
            alternativePatterns = altPatterns
        )
    }

    fun binaryToTraditionalSymbols(binary: String): String {
        return binary.replace('1', '/').replace('0', 'o')
    }

    fun symbolsToBinary(symbols: String): String {
        return symbols.replace('/', '1').replace('o', '0')
    }
}
