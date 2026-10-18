package com.hathway.solatnadi.domain.model

data class CalculationMethod(
    val id: Int,
    val name: String,
    val description: String
) {
    companion object {
        val AVAILABLE_METHODS = listOf(
            CalculationMethod(3, "JAKIM (Malaysia)", "Uses the Department of Islamic Development Malaysia (JAKIM) method."),
            CalculationMethod(2, "ISNA (North America)", "Islamic Society of North America calculation method."),
            CalculationMethod(4, "Muslim World League", "Standard Muslim World League calculation method."),
            CalculationMethod(5, "Umm Al-Qura (Makkah)", "Umm Al-Qura University, Makkah calculation method."),
            CalculationMethod(8, "Gulf Region", "Gulf Region calculation method."),
            CalculationMethod(11, "Majlis Ugama Islam Singapura", "MUIS Singapore calculation method."),
            CalculationMethod(20, "KEMENAG (Indonesia)", "Kementerian Agama Republik Indonesia method.")
        )

        fun getById(id: Int): CalculationMethod {
            return AVAILABLE_METHODS.find { it.id == id } ?: AVAILABLE_METHODS.first()
        }
    }
}
