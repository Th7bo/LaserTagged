package com.th7bo.lasertagged.listeners

import com.th7bo.lasertagged.LaserTagged
import com.th7bo.lasertagged.arenas.ArenaGamemode
import com.th7bo.lasertagged.utils.sendColored
import me.clip.placeholderapi.libs.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent

class MoveEvent : Listener {

    init {
        LaserTagged.instance.server.pluginManager.registerEvents(this, LaserTagged.instance)
    }

    @EventHandler
    fun hunger(event: FoodLevelChangeEvent) {
        event.entity.foodLevel = 20
    }

    @EventHandler
    fun move(event: PlayerMoveEvent) {
        val player = event.player
        if (player.hasMetadata("safezone") && player.gameMode == org.bukkit.GameMode.SURVIVAL) {
            val arena = LaserTagged.instance.arenaManager?.currentArena
            if (arena != null) {
                val team = player.getMetadata("team")[0].asInt()
                var left = true
                if (arena.gamemode == ArenaGamemode.FFA) {
                    for (cuboid in arena.safeZoneCuboids) {
                        if (cuboid.value.isIn(player.location)) {
                            left = false
                        }
                    }
                } else {
                    val cube = arena.safeZoneCuboids[team - 1]
                    if (cube?.isIn(player.location) == true) {
                        left = false
                    }
                }
                if (left) {
                    player.removeMetadata("safezone", LaserTagged.instance)
                    player.sendColored("&cYou left the safezone")
                }
            }
        }
    }

    @EventHandler
    fun teleport(event: PlayerTeleportEvent) {
        val player = event.player
        if (player.hasMetadata("safezone")) {
            val arena = LaserTagged.instance.arenaManager?.currentArena
            if (arena != null) {
                val team = player.getMetadata("team")[0].asInt()
                var left = true
                if (arena.gamemode == ArenaGamemode.FFA) {
                    for (cuboid in arena.safeZoneCuboids) {
                        if (cuboid.value.isIn(event.to)) {
                            left = false
                        }
                    }
                } else {
                    val cube = arena.safeZoneCuboids[team - 1]
                    if (cube?.isIn(event.to) == true) {
                        left = false
                    }
                }
                if (left) {
                    player.removeMetadata("safezone", LaserTagged.instance)
                    player.sendColored("&cYou left the safezone")
                }
            }
        }
    }

}