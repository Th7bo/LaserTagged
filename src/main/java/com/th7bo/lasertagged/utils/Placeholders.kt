package com.th7bo.lasertagged.utils

import com.th7bo.lasertagged.LaserTagged
import com.th7bo.lasertagged.arenas.ArenaGamemode
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player


class Placeholders : PlaceholderExpansion() {
    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (params == "team") {
            val onlinePlayer = player?.player
            if (onlinePlayer?.hasMetadata("team") == true) {
                return onlinePlayer.getMetadata("team")[0].asString()
            }
        } else if (params == "team1_display") {
            return if (LaserTagged.instance.arenaManager?.currentArena?.gamemode == ArenaGamemode.FFA) {
                "FFA"
            } else {
                "Team 1"
            }
        } else if (params == "team2_display") {
            return if (LaserTagged.instance.arenaManager?.currentArena?.gamemode == ArenaGamemode.FFA) {
                "FFA"
            } else {
                "Team 2"
            }
        } else if (params == "team_color") {
            return if (LaserTagged.instance.arenaManager?.currentArena?.gamemode == ArenaGamemode.FFA) {
                "&7"
            } else if (player?.player?.hasMetadata("team") == true) {
                if (player.player!!.getMetadata("team")[0].asInt() == 1) {
                    return "&9Blue "
                } else {
                    return "&cRed "
                }
            } else {
                "&7"
            }
        } else if (params == "mode") {
            return if (LaserTagged.instance.arenaManager?.currentArena != null) {
                LaserTagged.instance.arenaManager?.currentArena?.gamemode.toString()
            } else {
                "None"
            }
        } else if (params == "time") {
            return if (LaserTagged.instance.arenaManager?.timeStarted != null) {
                val started = LaserTagged.instance.arenaManager?.timeStarted?.div(1000)
                val now = System.currentTimeMillis() / 1000
                val time = 15 * 60 - (now - started!!)
                // format time in mm:ss
                val minutes = time / 60
                val seconds = time % 60
                String.format("%02d:%02d", minutes, seconds)
            } else {
                "Game is going to start soon!"
            }
        }
        return null
    }

    override fun getIdentifier(): String {
        return "lasertagged"
    }

    override fun getAuthor(): String {
        return "Th7bo"
    }

    override fun getVersion(): String {
        return "1.0"
    }
}