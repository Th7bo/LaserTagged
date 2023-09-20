package com.th7bo.lasertagged.utils

import net.md_5.bungee.api.ChatColor
import java.text.SimpleDateFormat
import java.util.regex.Pattern
import kotlin.math.floor


class Misc {
    companion object {

        val HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-fA-F])")

        fun Color(s: String?): String? {
            return translateHexCodes(s)
        }

        private fun translateHexCodes(textToTranslate: String?): String? {
            val matcher = textToTranslate?.let { HEX_PATTERN.matcher(it) }
            val buffer = StringBuffer()
            if (matcher != null) {
                while (matcher.find()) {
                    matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString().lowercase())
                }
            }
            if (matcher != null) {
                return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString())
            }
            return textToTranslate
        }
        fun createProgressBar(int: Double, max: Double, barLength: Int, color1: String, color2: String): String? {
            var string = ""
            for (i in 0 until barLength) {
                string = if (i+1 == barLength || i == 0) {
                    string.plus("•")
                } else {
                    string.plus(" ")
                }
            }
            return (color1 + "&m" + string.substring(0, floor((barLength * (int/max))).toInt()) + color2 + "&m" + string.substring(floor((barLength * (int/max))).toInt(), barLength)).color()
        }
    }
}