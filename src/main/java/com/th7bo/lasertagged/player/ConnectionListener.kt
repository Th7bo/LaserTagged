package com.th7bo.lasertagged.player

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.th7bo.lasertagged.LaserTagged
import com.th7bo.lasertagged.utils.DesignFeatures
import com.th7bo.lasertagged.utils.playerData
import fr.mrmicky.fastboard.FastBoard
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent


class ConnectionListener : Listener {
    val defaultArray: JsonArray = JsonArray()
    private var defaultElement: JsonElement = JsonParser.parseString("{ \n" +
            "    \"data\": { \n" +
            "        \"kills\": 0, \n" +
            "        \"deaths\": 0, \n" +
            "        \"shots\": 0, \n" +
            "        \"hits\": 0, \n" +
            "        \"wins\": 0, \n" +
            "        \"losses\": 0, \n" +
            "        \"gamesPlayed\": 0, \n" +
            "        \"accuracy\": 0.0, \n" +
            "        \"weapons\": \"\" \n" +
            "    } \n" +
            "}")


    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        var playerData = LaserTagged.instance.databaseManager?.getPlayerData(event.player)
        if (playerData == null) {
            playerData = defaultArray
        }
        val raw = playerData.toString()
        playerData = JsonParser.parseString(raw).asJsonArray
        val playerProfile = PlayerProfile(playerData, event.player)
        LaserTagged.instance.databaseManager?.playerDataCached?.set(event.player, playerProfile)

        val board = FastBoard(event.player)

        LaserTagged.instance.boards[event.player.uniqueId] = board
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        LaserTagged.instance.databaseManager?.addPlayerData(event.player)
        LaserTagged.instance.databaseManager?.playerDataCached?.remove(event.player)

        val player: Player = event.player

        LaserTagged.instance.boards[player.uniqueId]?.delete()
    }

    init {
        defaultArray.add(defaultElement)
        LaserTagged.instance.server.pluginManager.registerEvents(this, LaserTagged.instance)
        // create a runnable that will run every 2 minutes to save all player data
        LaserTagged.instance.server.scheduler.scheduleSyncRepeatingTask(LaserTagged.instance, {
            for (player in LaserTagged.instance.databaseManager?.playerDataCached?.keys!!) {
                val playerProfile = player.playerData()
                if (playerProfile != null) {
                    LaserTagged.instance.databaseManager?.addPlayerData(player)
                }
            }
        }, 300, 300)
    }

}