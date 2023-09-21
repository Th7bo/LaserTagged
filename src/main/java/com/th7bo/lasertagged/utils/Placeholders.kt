package com.th7bo.lasertagged.utils

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer


class Placeholders : PlaceholderExpansion() {
    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (params.equals("abovename", ignoreCase = true)) {
            val onlinePlayer = player?.player
            if (onlinePlayer?.hasMetadata("team") == true) {
                return onlinePlayer.getMetadata("last_hit")[0].asString()
            }
            return ""
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