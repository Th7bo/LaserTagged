package com.th7bo.lasertagged.listeners

import com.th7bo.lasertagged.LaserTagged
import com.th7bo.lasertagged.utils.color
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import java.time.Duration


class DeathEvent : Listener {

    init {
        // register events
        LaserTagged.instance.server.pluginManager.registerEvents(this, LaserTagged.instance)
        val world = Bukkit.getWorld("world")
        world?.setGameRule(org.bukkit.GameRule.DO_IMMEDIATE_RESPAWN, true)
        world?.setGameRule(org.bukkit.GameRule.KEEP_INVENTORY, true)
        world?.setGameRule(org.bukkit.GameRule.DO_WEATHER_CYCLE, false)
        world?.setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, false)
        world?.setGameRule(org.bukkit.GameRule.DO_MOB_SPAWNING, false)
        world?.setGameRule(org.bukkit.GameRule.DO_FIRE_TICK, false)
        world?.setGameRule(org.bukkit.GameRule.DO_PATROL_SPAWNING, false)
        world?.setGameRule(org.bukkit.GameRule.DO_TRADER_SPAWNING, false)
        world?.setGameRule(org.bukkit.GameRule.DO_TILE_DROPS, false)
        world?.setGameRule(org.bukkit.GameRule.DO_ENTITY_DROPS, false)
        world?.setGameRule(org.bukkit.GameRule.ANNOUNCE_ADVANCEMENTS, false)
        world?.setGameRule(org.bukkit.GameRule.DO_TRADER_SPAWNING, false)
        world?.setGameRule(org.bukkit.GameRule.DO_VINES_SPREAD, false)
        world?.setGameRule(org.bukkit.GameRule.FIRE_DAMAGE, false)
        world?.setGameRule(org.bukkit.GameRule.FREEZE_DAMAGE, false)
        world?.setGameRule(org.bukkit.GameRule.RANDOM_TICK_SPEED, 0)
        world?.setGameRule(org.bukkit.GameRule.SPAWN_RADIUS, 0)
        world?.setGameRule(org.bukkit.GameRule.SPECTATORS_GENERATE_CHUNKS, false)
        world?.setGameRule(org.bukkit.GameRule.NATURAL_REGENERATION, false)
        world?.time = 6000
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity
        val killer = player.killer
        if (killer != null) {
            event.deathMessage = "&#f53b22${event.player.name} &#f25f4bwas killed by &#f53b22${killer.name}".color()
            event.keepInventory = true
            player.setMetadata("safezone", org.bukkit.metadata.FixedMetadataValue(LaserTagged.instance, true))
        }
        event.deathMessage(Component.text(""))
        val tit: Component = Component.text("\uD835\uDDB8\uD835\uDDAE\uD835\uDDB4 \uD835\uDDA3\uD835\uDDA8\uD835\uDDA4\uD835\uDDA3").color(TextColor.color(181, 27, 16))
        val subtitle: Component = Component.text("Ｃ").color(TextColor.color(0,0,0))
        val title = Title.title(tit, subtitle, Title.Times.times(Duration.ofMillis(250L), Duration.ofSeconds(5L), Duration.ofMillis(250L)))
        player.showTitle(title)
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        event.entity.teleport(event.entity.location)
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        player.gameMode = org.bukkit.GameMode.SPECTATOR
        player.setMetadata("safezone", org.bukkit.metadata.FixedMetadataValue(LaserTagged.instance, true))
        // set gamemode back after 5 seconds
        Bukkit.getScheduler().scheduleSyncDelayedTask(LaserTagged.instance, Runnable {
            player.gameMode = org.bukkit.GameMode.SURVIVAL
            val teamID = player.getMetadata("team")[0].asInt()
            player.teleport(LaserTagged.instance.arenaManager?.currentArena?.getRandomSpawn(teamID)!!)
        }, 100)
    }

}