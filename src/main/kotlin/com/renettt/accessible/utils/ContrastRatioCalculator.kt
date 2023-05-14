package com.renettt.accessible.utils

object ContrastRatioCalculator {

    fun getContrastRatio(hexColor1: String, hexColor2: String): Double {
        // Convert hex colors to RGB values
        val rgbColor1 = hexToRgb(hexColor1)
        val rgbColor2 = hexToRgb(hexColor2)

        // Calculate the relative luminance of each color
        val luminance1 = calculateRelativeLuminance(rgbColor1.r, rgbColor1.g, rgbColor1.b)
        val luminance2 = calculateRelativeLuminance(rgbColor2.r, rgbColor2.g, rgbColor2.b)

        // Calculate the contrast ratio
        val contrastRatio = (Math.max(luminance1, luminance2) + 0.05) / (Math.min(luminance1, luminance2) + 0.05)

        // Round the contrast ratio to two decimal places
        return Math.round(contrastRatio * 100) / 100.0
    }

    private fun hexToRgb(hexColor: String): RgbColor {
        // Remove any '#' or '0x' prefix from the hex color
        var color = hexColor.removePrefix("#").removePrefix("0x")

        // Convert the hex color to an RGB color
        val r = color.substring(0, 2).toInt(16)
        val g = color.substring(2, 4).toInt(16)
        val b = color.substring(4, 6).toInt(16)

        return RgbColor(r, g, b)
    }

    private fun calculateRelativeLuminance(r: Int, g: Int, b: Int): Double {
        // Calculate the relative luminance of an RGB color
        val gamma = 2.2
        val rLinear = r / 255.0
        val gLinear = g / 255.0
        val bLinear = b / 255.0
        val luminance = 0.2126 * Math.pow(rLinear, gamma) + 0.7152 * Math.pow(gLinear, gamma) + 0.0722 * Math.pow(bLinear, gamma)

        return luminance
    }

    data class RgbColor(val r: Int, val g: Int, val b: Int)

}
