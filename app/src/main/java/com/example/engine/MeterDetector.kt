package com.example.engine

import android.content.Context
import org.json.JSONObject

object MeterDetector {

    var METERS: List<MeterDefinition> = emptyList()
    var ZIHAF_DICT: Map<String, Map<String, String>> = emptyMap()

    fun initialize(context: Context) {
        if (METERS.isNotEmpty()) return
        try {
            val jsonString = context.assets.open("meters.json").bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)

            val metersJson = root.getJSONArray("meters")
            val metersList = mutableListOf<MeterDefinition>()

            for (i in 0 until metersJson.length()) {
                val mObj = metersJson.getJSONObject(i)
                val formsJson = mObj.getJSONArray("forms")
                val formsList = mutableListOf<MeterForm>()

                for (j in 0 until formsJson.length()) {
                    val fObj = formsJson.getJSONObject(j)
                    val basesJson = fObj.getJSONArray("bases")
                    val basesList = mutableListOf<String>()
                    for (b in 0 until basesJson.length()) basesList.add(basesJson.getString(b))

                    val hashwJson = fObj.getJSONArray("hashw")
                    val hashwList = mutableListOf<List<String>>()
                    for (h in 0 until hashwJson.length()) {
                        val subArr = hashwJson.getJSONArray(h)
                        val subList = mutableListOf<String>()
                        for (s in 0 until subArr.length()) subList.add(subArr.getString(s))
                        hashwList.add(subList)
                    }

                    val pairsJson = fObj.getJSONArray("pairs")
                    val pairsList = mutableListOf<List<String>>()
                    for (p in 0 until pairsJson.length()) {
                        val subArr = pairsJson.getJSONArray(p)
                        val subList = mutableListOf<String>()
                        for (s in 0 until subArr.length()) subList.add(subArr.getString(s))
                        pairsList.add(subList)
                    }

                    formsList.add(
                        MeterForm(
                            type = fObj.getString("type"),
                            feetPerShatr = fObj.getInt("feetPerShatr"),
                            bases = basesList,
                            hashw = hashwList,
                            pairs = pairsList
                        )
                    )
                }

                metersList.add(
                    MeterDefinition(
                        id = mObj.getString("id"),
                        name = mObj.getString("name"),
                        keyVerse = mObj.optString("keyVerse", ""),
                        exampleVerse = mObj.optString("exampleVerse", ""),
                        examplePoet = mObj.optString("examplePoet", ""),
                        description = mObj.optString("description", ""),
                        forms = formsList
                    )
                )
            }
            METERS = metersList

            val zihafJson = root.optJSONObject("zihafDictionary")
            if (zihafJson != null) {
                val zMap = mutableMapOf<String, Map<String, String>>()
                val keys = zihafJson.keys()
                while (keys.hasNext()) {
                    val baseKey = keys.next()
                    val subObj = zihafJson.getJSONObject(baseKey)
                    val subMap = mutableMapOf<String, String>()
                    val subKeys = subObj.keys()
                    while (subKeys.hasNext()) {
                        val patternKey = subKeys.next()
                        subMap[patternKey] = subObj.getString(patternKey)
                    }
                    zMap[baseKey] = subMap
                }
                ZIHAF_DICT = zMap
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private data class CandOption(val pattern: String, val name: String)

    private fun parseOptions(list: List<String>): List<CandOption> {
        return list.map {
            val parts = it.split(":")
            CandOption(parts.getOrElse(0) { "" }, parts.getOrElse(1) { "" })
        }
    }

    private fun getZihafDescription(baseFoot: String, pattern: String, footName: String): String {
        val dictForBase = ZIHAF_DICT[baseFoot]
        if (dictForBase != null && dictForBase.containsKey(pattern)) {
            return dictForBase[pattern] ?: "سالمة"
        }
        if (footName.contains(baseFoot)) return "سالمة"
        return "فرعية"
    }

    private fun matchShatrDfs(
        binary: String,
        targetOptions: List<List<CandOption>>,
        baseNames: List<String>
    ): List<FootMatch>? {
        fun dfs(bitIndex: Int, footIndex: Int, acc: List<FootMatch>): List<FootMatch>? {
            if (footIndex == targetOptions.size) {
                return if (bitIndex == binary.length) acc else null
            }

            val currentCandidates = targetOptions[footIndex]
            val baseName = baseNames.getOrElse(footIndex) { "فعولن" }

            for (cand in currentCandidates) {
                if (binary.startsWith(cand.pattern, bitIndex)) {
                    val isEnd = (footIndex == targetOptions.size - 1)
                    val desc = getZihafDescription(baseName, cand.pattern, cand.name)
                    val footMatch = FootMatch(
                        name = cand.name,
                        pattern = cand.pattern,
                        description = desc,
                        isEndFoot = isEnd
                    )
                    val res = dfs(bitIndex + cand.pattern.length, footIndex + 1, acc + footMatch)
                    if (res != null) return res
                }
            }
            return null
        }

        return dfs(0, 0, emptyList())
    }

    fun detectMeter(
        sadrAnalysis: ShatrAnalysis,
        ajuzAnalysis: ShatrAnalysis?
    ): MeterMatchResult {
        val isSingleShatr = ajuzAnalysis == null || ajuzAnalysis.binaryPattern.isBlank()
        val sadrPatterns = if (sadrAnalysis.alternativePatterns.isNotEmpty()) sadrAnalysis.alternativePatterns else listOf(sadrAnalysis.binaryPattern)
        val ajuzPatterns = if (ajuzAnalysis != null && ajuzAnalysis.alternativePatterns.isNotEmpty()) ajuzAnalysis.alternativePatterns else if (ajuzAnalysis != null) listOf(ajuzAnalysis.binaryPattern) else emptyList()

        data class SoundMatch(
            val meter: MeterDefinition,
            val form: MeterForm,
            val sadrFeet: List<FootMatch>,
            val ajuzFeet: List<FootMatch>?,
            val arudPairIndex: Int
        )

        val soundMatches = mutableListOf<SoundMatch>()

        for (meter in METERS) {
            for (form in meter.forms) {
                val hashwParsed = form.hashw.map { parseOptions(it) }
                val bases = form.bases

                for (pIdx in form.pairs.indices) {
                    val pair = form.pairs[pIdx]
                    val arudItem = pair.getOrElse(0) { "" }
                    val darbItem = pair.getOrElse(1) { "" }
                    val arudParsed = parseOptions(listOf(arudItem))
                    val darbParsed = parseOptions(listOf(darbItem))

                    val sadrOptions = hashwParsed + listOf(arudParsed)
                    val ajuzOptions = hashwParsed + listOf(darbParsed)

                    var matchedSadrFeet: List<FootMatch>? = null
                    for (sp in sadrPatterns) {
                        val matched = matchShatrDfs(sp, sadrOptions, bases)
                        if (matched != null) {
                            matchedSadrFeet = matched
                            break
                        }
                    }

                    if (matchedSadrFeet == null) continue

                    if (!isSingleShatr) {
                        var matchedAjuzFeet: List<FootMatch>? = null
                        for (ap in ajuzPatterns) {
                            val matched = matchShatrDfs(ap, ajuzOptions, bases)
                            if (matched != null) {
                                matchedAjuzFeet = matched
                                break
                            }
                        }

                        if (matchedAjuzFeet != null) {
                            soundMatches.add(
                                SoundMatch(
                                    meter = meter,
                                    form = form,
                                    sadrFeet = matchedSadrFeet,
                                    ajuzFeet = matchedAjuzFeet,
                                    arudPairIndex = pIdx
                                )
                            )
                        }
                    } else {
                        soundMatches.add(
                            SoundMatch(
                                meter = meter,
                                form = form,
                                sadrFeet = matchedSadrFeet,
                                ajuzFeet = null,
                                arudPairIndex = pIdx
                            )
                        )
                    }
                }
            }
        }

        if (soundMatches.isNotEmpty()) {
            val primary = soundMatches.first()
            val meter = primary.meter
            val form = primary.form
            val fullTitle = "بَحْرُ ${meter.name} (${form.type})"

            val sadrFootCount = primary.sadrFeet.size
            val sadrFootCountText = "${convertToArabicNumber(sadrFootCount)} تفعيلات"
            val lastSadrFoot = primary.sadrFeet.last()

            val sadrMatch = ShatrMeterMatch(
                shatrTitle = "الشطر الأول (الصَّدْر)",
                text = sadrAnalysis.originalText,
                feet = primary.sadrFeet,
                phoneticLine = sadrAnalysis.phoneticLine,
                feetCountText = sadrFootCountText,
                endFootTitle = "العروض",
                endFootName = lastSadrFoot.name,
                endFootDesc = lastSadrFoot.description
            )

            var ajuzMatch: ShatrMeterMatch? = null
            var darbDesc = "غير محدد (شطر مفرد)"

            if (primary.ajuzFeet != null && ajuzAnalysis != null) {
                val ajuzFootCount = primary.ajuzFeet.size
                val ajuzFootCountText = "${convertToArabicNumber(ajuzFootCount)} تفعيلات"
                val lastAjuzFoot = primary.ajuzFeet.last()

                ajuzMatch = ShatrMeterMatch(
                    shatrTitle = "الشطر الثاني (العَجُز)",
                    text = ajuzAnalysis.originalText,
                    feet = primary.ajuzFeet,
                    phoneticLine = ajuzAnalysis.phoneticLine,
                    feetCountText = ajuzFootCountText,
                    endFootTitle = "الضرب",
                    endFootName = lastAjuzFoot.name,
                    endFootDesc = lastAjuzFoot.description
                )
                darbDesc = "الضرب: ${lastAjuzFoot.name} (${lastAjuzFoot.description})"
            }

            val arudDesc = "العروض: ${lastSadrFoot.name} (${lastSadrFoot.description})"
            val secondary = soundMatches.drop(1).map {
                SecondaryMatch(meterName = it.meter.name, formType = it.form.type)
            }

            val prosodicExplanation = if (isSingleShatr) {
                "البيت جاء كشطر مفرد على بحر ${meter.name} (${form.type})؛ عروضه ${lastSadrFoot.name}، وتفعيلاته منتظمة سليمة."
            } else {
                "البيت جاء على بحر ${meter.name} ${form.type}؛ $arudDesc، و$darbDesc. والوزن تام وصحيح الإيقاع."
            }

            return MeterMatchResult(
                isSound = true,
                meterId = meter.id,
                meterName = meter.name,
                meterFormType = form.type,
                fullMeterTitle = fullTitle,
                sadrMatch = sadrMatch,
                ajuzMatch = ajuzMatch,
                arudDescription = arudDesc,
                darbDescription = darbDesc,
                prosodicExplanation = prosodicExplanation,
                secondaryMatches = secondary
            )
        }

        // إذا كان مكسوراً
        val correction = MeterCorrector.findClosestMeters(sadrAnalysis, ajuzAnalysis, METERS)
        val fallbackMeterName = correction.closestMeters.firstOrNull()?.name ?: "غير محدد"
        val fallbackFullTitle = "بَحْرُ $fallbackMeterName (مكسور)"

        val approxFeetSadr = approximateFeetBreakdown(sadrAnalysis.binaryPattern, correction.closestMeters.firstOrNull()?.id)
        val approxFeetAjuz = if (ajuzAnalysis != null) approximateFeetBreakdown(ajuzAnalysis.binaryPattern, correction.closestMeters.firstOrNull()?.id) else null

        val sadrMatch = ShatrMeterMatch(
            shatrTitle = "الشطر الأول (الصَّدْر)",
            text = sadrAnalysis.originalText,
            feet = approxFeetSadr,
            phoneticLine = sadrAnalysis.phoneticLine,
            feetCountText = "${convertToArabicNumber(approxFeetSadr.size)} تفعيلات",
            endFootTitle = "العروض",
            endFootName = if (approxFeetSadr.isNotEmpty()) approxFeetSadr.last().name else "مكسورة",
            endFootDesc = "موضع كسر عروضي"
        )

        val ajuzMatch = if (ajuzAnalysis != null) {
            ShatrMeterMatch(
                shatrTitle = "الشطر الثاني (العَجُز)",
                text = ajuzAnalysis.originalText,
                feet = approxFeetAjuz ?: emptyList(),
                phoneticLine = ajuzAnalysis.phoneticLine,
                feetCountText = "${convertToArabicNumber(approxFeetAjuz?.size ?: 0)} تفعيلات",
                endFootTitle = "الضرب",
                endFootName = if (!approxFeetAjuz.isNullOrEmpty()) approxFeetAjuz.last().name else "مكسورة",
                endFootDesc = "موضع كسر عروضي"
            )
        } else null

        return MeterMatchResult(
            isSound = false,
            meterId = correction.closestMeters.firstOrNull()?.id ?: "broken",
            meterName = fallbackMeterName,
            meterFormType = "مكسور",
            fullMeterTitle = fallbackFullTitle,
            sadrMatch = sadrMatch,
            ajuzMatch = ajuzMatch,
            arudDescription = "العروض: فيها انكسار عن الأصل",
            darbDescription = "الضرب: فيه انكسار أو عدم مطابقة للوزن",
            prosodicExplanation = correction.explanation,
            correction = correction
        )
    }

    private fun approximateFeetBreakdown(pattern: String, meterId: String?): List<FootMatch> {
        if (pattern.isEmpty()) return emptyList()
        val meter = METERS.find { it.id == meterId }
        val baseNames = meter?.forms?.firstOrNull()?.bases ?: listOf("فعولن", "مفاعيلن", "فعولن", "مفاعيلن")
        val feet = mutableListOf<FootMatch>()
        var remaining = pattern
        var idx = 0

        while (remaining.isNotEmpty() && idx < baseNames.size) {
            val chunkLen = if (idx % 2 == 0) 5 else 7
            val actualLen = minOf(remaining.length, chunkLen)
            val chunk = remaining.substring(0, actualLen)
            remaining = remaining.substring(actualLen)
            val baseName = baseNames.getOrElse(idx) { "تفعيلة" }

            feet.add(
                FootMatch(
                    name = baseName,
                    pattern = chunk,
                    description = if (idx == baseNames.size - 1) "عروض/ضرب" else "حشو",
                    isEndFoot = idx == baseNames.size - 1
                )
            )
            idx++
        }

        if (remaining.isNotEmpty() && feet.isNotEmpty()) {
            val last = feet.last()
            feet[feet.lastIndex] = last.copy(pattern = last.pattern + remaining)
        }

        return feet
    }

    private fun convertToArabicNumber(n: Int): String {
        return when (n) {
            1 -> "١"
            2 -> "٢"
            3 -> "٣"
            4 -> "٤"
            5 -> "٥"
            6 -> "٦"
            7 -> "٧"
            8 -> "٨"
            else -> n.toString()
        }
    }
}
