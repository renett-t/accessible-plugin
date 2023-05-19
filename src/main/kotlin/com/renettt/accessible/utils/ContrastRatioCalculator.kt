package com.renettt.accessible.utils

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

object ContrastRatioCalculator {

    fun getContrastRatio(hexColor1: String, hexColor2: String): Double {
        // Convert hex colors to RGB values
        val rgbColor1 = hexToRgb(hexColor1)
        val rgbColor2 = hexToRgb(hexColor2)

        // Calculate the relative luminance of each color
        val luminance1 = calculateRelativeLuminance(rgbColor1.red, rgbColor1.green, rgbColor1.blue)
        val luminance2 = calculateRelativeLuminance(rgbColor2.red, rgbColor2.green, rgbColor2.blue)

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
        val luminance =
            0.2126 * Math.pow(rLinear, gamma) + 0.7152 * Math.pow(gLinear, gamma) + 0.0722 * Math.pow(bLinear, gamma)

        return luminance
    }

    data class RgbColor(val red: Int, val green: Int, val blue: Int)

    fun calculateContrastRatio(color1: RgbColor, color2: RgbColor): Double {
        val luminance1 = calculateLuminance(color1)
        val luminance2 = calculateLuminance(color2)

        val brighter = max(luminance1, luminance2)
        val darker = min(luminance1, luminance2)

        return (brighter + 0.05) / (darker + 0.05)
    }

    fun calculateLuminance(color: RgbColor): Double {
        val red = color.red / 255.0
        val green = color.green / 255.0
        val blue = color.blue / 255.0

        val redComponent = if (red <= 0.03928)
            red / 12.92
        else
            ((red + 0.055) / 1.055).pow(2.4)
        val greenComponent = if (green <= 0.03928)
            green / 12.92
        else
            ((green + 0.055) / 1.055).pow(2.4)
        val blueComponent = if (blue <= 0.03928)
            blue / 12.92
         else
            ((blue + 0.055) / 1.055).pow(2.4)

        return 0.2126 * redComponent + 0.7152 * greenComponent + 0.0722 * blueComponent
    }


}
