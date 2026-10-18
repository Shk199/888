package com.example.engine

import kotlin.math.min

object MeterCorrector {

    private data class LevResult(
        val distance: Int,
        val operations: List<LevOp>
    )

    private data class LevOp(
        val op: String, // "replace", "insert", "delete"
        val indexA: Int,
        val indexB: Int
    )

    private fun levenshteinDistance(a: String, b: String): LevResult {
        val m = a.length
        val n = b.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j

        for (i in 1..m) {
            for (j in 1..n) {
                if (a[i - 1] == b[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1]
                } else {
                    dp[i][j] = 1 + min(
                        dp[i - 1][j], // delete
                        min(
                            dp[i][j - 1], // insert
                            dp[i - 1][j - 1] // replace
                        )
                    )
                }
            }
        }

        var i = m
        var j = n
        val ops = mutableListOf<LevOp>()

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && a[i - 1] == b[j - 1]) {
                i--
                j--
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                ops.add(0, LevOp("replace", i - 1, j - 1))
                i--
                j--
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                ops.add(0, LevOp("delete", i - 1, j))
                i--
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                ops.add(0, LevOp("insert", i, j - 1))
                j--
            } else {
                break
            }
        }

        return LevResult(dp[m][n], ops)
    }

    private fun generateRepresentativePattern(form: MeterForm): String {
        val hashwBits = form.hashw.map { opts -> opts.firstOrNull()?.split(":")?.get(0) ?: "" }
        val arudBit = form.pairs.firstOrNull()?.firstOrNull()?.split(":")?.get(0) ?: ""
        return hashwBits.joinToString("") + arudBit
    }

    fun findClosestMeters(
        sadrAnalysis: ShatrAnalysis,
        ajuzAnalysis: ShatrAnalysis?,
        allMeters: List<MeterDefinition>
    ): BrokenMeterCorrection {
        val combinedCurrent = sadrAnalysis.binaryPattern + (ajuzAnalysis?.binaryPattern ?: "")
        val candidates = mutableListOf<CandidateResult>()

        for (meter in allMeters) {
            for (form in meter.forms) {
                val repShatr = generateRepresentativePattern(form)
                val repFull = if (ajuzAnalysis != null) repShatr + repShatr else repShatr

                val lev = levenshteinDistance(combinedCurrent, repFull)
                candidates.add(
                    CandidateResult(
                        meter = meter,
                        form = form,
                        distance = lev.distance,
                        expectedPattern = repFull,
                        operations = lev.operations
                    )
                )
            }
        }

        candidates.sortBy { it.distance }

        val top3 = candidates.take(3).map { c ->
            ClosestMeterInfo(
                id = c.meter.id,
                name = c.meter.name,
                type = c.form.type,
                distance = c.distance,
                expectedPattern = c.expectedPattern
            )
        }

        val best = candidates.firstOrNull() ?: CandidateResult(
            meter = MeterDefinition("unknown", "غير محدد", "", "", "", "", emptyList()),
            form = MeterForm("تام", 4, emptyList(), emptyList(), emptyList()),
            distance = 0,
            expectedPattern = "",
            operations = emptyList()
        )

        val brokenPositions = mutableListOf<BrokenPosition>()
        val sadrLen = sadrAnalysis.binaryPattern.length

        for (op in best.operations) {
            val isSadr = op.indexA < sadrLen
            val targetAnalysis = if (isSadr) sadrAnalysis else ajuzAnalysis ?: continue
            val localBitIndex = if (isSadr) op.indexA else (op.indexA - sadrLen)

            var runningBits = 0
            var foundWordIndex = 0
            var foundChar = "موضع صوتي"
            var wordText = ""

            for (w in targetAnalysis.words.indices) {
                val wordObj = targetAnalysis.words[w]
                val wordBits = wordObj.binaryPattern.length
                if (runningBits + wordBits > localBitIndex) {
                    foundWordIndex = w
                    wordText = wordObj.originalText
                    val sylIndex = localBitIndex - runningBits
                    if (sylIndex in wordObj.syllables.indices) {
                        foundChar = wordObj.syllables[sylIndex].letter
                    }
                    break
                }
                runningBits += wordBits
            }

            val detail = when (op.op) {
                "replace" -> "يلزم تحويل هذا الحرف من " + if (op.indexA < combinedCurrent.length && combinedCurrent[op.indexA] == '1') "متحرك إلى ساكن" else "ساكن إلى متحرك"
                "delete" -> "هذا المقطع زائد عروضياً ويلزم حذفه أو تخفيفه"
                else -> "ينقص هذا الموضع حركة أو حرف مد لتحقيق استقامة الوزن"
            }

            brokenPositions.add(
                BrokenPosition(
                    shatr = if (isSadr) "sadr" else "ajuz",
                    wordIndex = foundWordIndex,
                    word = wordText,
                    charIndex = localBitIndex,
                    letter = foundChar,
                    operation = op.op,
                    detail = detail
                )
            )
        }

        val currentSymbols = ArudiEngine.binaryToTraditionalSymbols(combinedCurrent)
        val expectedSymbols = ArudiEngine.binaryToTraditionalSymbols(best.expectedPattern)

        val keyWords = brokenPositions.map { it.word.ifEmpty { it.letter } }.filter { it.isNotEmpty() }.take(3).joinToString(" و ")
        val explanation = "الوزن به انكسار عروضي لا يطابق أي بحر تماماً. أقرب وزن هو بحر ${best.meter.name} (${best.form.type}) بفارق ${best.distance} تعديلات صوتية، وتتركز أغلب مواضع الكسر في تفعيلات ${if (keyWords.isNotEmpty()) "($keyWords)" else "المقاطع الأخيرة"}."

        return BrokenMeterCorrection(
            closestMeters = top3,
            brokenPositions = brokenPositions,
            currentBinary = combinedCurrent,
            expectedBinary = best.expectedPattern,
            currentSymbols = currentSymbols,
            expectedSymbols = expectedSymbols,
            explanation = explanation
        )
    }

    private data class CandidateResult(
        val meter: MeterDefinition,
        val form: MeterForm,
        val distance: Int,
        val expectedPattern: String,
        val operations: List<LevOp>
    )
}
