package com.example.engine

data class ArudiSyllable(
    val letter: String,
    val harakah: String,
    val bit: Char, // '0' or '1'
    val srcIndex: Int
)

data class ArudiWord(
    val originalText: String,
    val phoneticText: String,
    val syllables: List<ArudiSyllable>,
    val binaryPattern: String
)

data class ShatrAnalysis(
    val originalText: String,
    val words: List<ArudiWord>,
    val phoneticLine: String,
    val binaryPattern: String,
    val hasOptionalIshba: Boolean,
    val alternativePatterns: List<String>
)

data class FootMatch(
    val name: String,
    val pattern: String,
    val description: String,
    val isEndFoot: Boolean
)

data class ShatrMeterMatch(
    val shatrTitle: String,
    val text: String,
    val feet: List<FootMatch>,
    val phoneticLine: String,
    val feetCountText: String,
    val endFootTitle: String,
    val endFootName: String,
    val endFootDesc: String
)

data class SecondaryMatch(
    val meterName: String,
    val formType: String
)

data class ClosestMeterInfo(
    val id: String,
    val name: String,
    val type: String,
    val distance: Int,
    val expectedPattern: String
)

data class BrokenPosition(
    val shatr: String, // "sadr" or "ajuz"
    val wordIndex: Int,
    val word: String,
    val charIndex: Int,
    val letter: String,
    val operation: String, // "replace", "insert", "delete"
    val detail: String
)

data class BrokenMeterCorrection(
    val closestMeters: List<ClosestMeterInfo>,
    val brokenPositions: List<BrokenPosition>,
    val currentBinary: String,
    val expectedBinary: String,
    val currentSymbols: String,
    val expectedSymbols: String,
    val explanation: String
)

data class MeterMatchResult(
    val isSound: Boolean,
    val meterId: String,
    val meterName: String,
    val meterFormType: String,
    val fullMeterTitle: String,
    val sadrMatch: ShatrMeterMatch,
    val ajuzMatch: ShatrMeterMatch?,
    val arudDescription: String,
    val darbDescription: String,
    val prosodicExplanation: String,
    val secondaryMatches: List<SecondaryMatch> = emptyList(),
    val correction: BrokenMeterCorrection? = null
)

data class MeterDefinition(
    val id: String,
    val name: String,
    val keyVerse: String,
    val exampleVerse: String,
    val examplePoet: String,
    val description: String,
    val forms: List<MeterForm>
)

data class MeterForm(
    val type: String,
    val feetPerShatr: Int,
    val bases: List<String>,
    val hashw: List<List<String>>, // e.g. [["11010:فعولن", "1101:فعول"], ...]
    val pairs: List<List<String>>  // e.g. [["110110:مفاعلن", "1101010:مفاعيلن"], ...]
)
