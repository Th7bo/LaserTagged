package com.th7bo.lasertagged.utils

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.Vector
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

        fun getLine(point1: Location, point2: Location, space: Double): ArrayList<Location> {
            val returns = ArrayList<Location>()
            val points = point1.distance(point2) * space
            val delta = Vector(point2.x - point1.x, point2.y - point1.y, point2.z - point1.z).divide(Vector(points, points, points))
            repeat(points.toInt()) {
                val clone = point1.clone().add(delta.clone().multiply(it.toDouble()))
                returns.add(clone)
            }
            return returns
        }

        fun newY(loc: Location, y: Double): Location {
            return Location(loc.world, loc.x, y, loc.z)
        }

        fun cube(from: Location, to: Location, space: Double): ArrayList<Location> {
            val list = listOf(Location(from.world, to.x, from.y, from.z), Location(from.world, from.x, from.y, from.z), Location(from.world, from.x, from.y, to.z), Location(to.world, to.x, from.y, to.z))
            val returning = ArrayList<Location>()
            repeat(2) {
                if ((it+1) % 2 == 0) {
                    var int = -1
                    for (loc in list) {
                        int++
                        list[int].y = to.y
                    }
                }
                for (location in getLine(list[1], list[2], space)) {
                    returning.add(location)
                }
                for (location in getLine(list[0], list[1], space)) {
                    returning.add(location)
                }
                for (location in getLine(list[0], list[3], space)) {
                    returning.add(location)
                }
                for (location in getLine(list[2], list[3], space)) {
                    returning.add(location)
                }
            }
            repeat (4) {
                for (location in getLine(list[it], newY(list[it], from.y), space)){
                    returning.add(location)
                }
            }
            return returning
        }
    }
}