package com.th7bo.lasertagged.listeners

import com.th7bo.lasertagged.LaserTagged
import com.th7bo.lasertagged.arenas.ArenaGamemode
import com.th7bo.lasertagged.utils.playerData
import com.th7bo.lasertagged.utils.sendColored
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.util.RayTraceResult
import org.bukkit.util.Vector


class GunShootEvent : Listener {

    init {
        LaserTagged.instance.server.pluginManager.registerEvents(this, LaserTagged.instance)
    }

    @EventHandler
    fun onGunShoot(event: PlayerInteractEvent) {
        println("Player ${event.player.name} shot a gun")
        println("Action: ${event.action}")
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            if (event.player.inventory.itemInMainHand.type == Material.AIR) return
            if (LaserTagged.instance.arenaManager?.currentArena == null) return
            if (LaserTagged.instance.arenaManager?.currentArena?.isPlayerSafe(event.player) == true) return
            // do other checks
            val world: World = event.player.world
            val location: Location = event.player.eyeLocation
            val direction: Vector = location.getDirection()
            val rayTraceResult: RayTraceResult? = event.player.world.rayTrace(
                event.player.eyeLocation, direction, 100.0,
                FluidCollisionMode.NEVER, false, 0.0
            ) { entity -> entity !== event.player && entity is Player && canHitPlayer(event.player, entity) }
            event.player.playerData()?.shots = event.player.playerData()?.shots!! + 1
            var hits = event.player.playerData()?.hits
            event.player.playerData()?.accuracy = hits?.div(event.player.playerData()?.shots!!)?.times(100.0)!!
            if (rayTraceResult?.hitEntity == null) {
                val hitLocation = rayTraceResult?.hitPosition?.toLocation(world)
                if (hitLocation != null) {
                    drawLine(location, hitLocation, 0.25)
                } else {
                    val locations = location.clone().add(direction.multiply(50))
                    println(locations)
                    drawLine(location, locations, 0.25)
                }
                return
            }
            val target = rayTraceResult.hitEntity as Player
            if (canHitPlayer(event.player, target)) {
                event.player.playerData()?.hits = event.player.playerData()?.hits!! + 1
                hits = event.player.playerData()?.hits
                event.player.playerData()?.accuracy = hits?.div(event.player.playerData()?.shots!!)?.times(100.0)!!
                event.player.sendColored("&7You shot at &f${rayTraceResult.hitEntity?.name}")
                target.sendColored("&#f25f4bYou were shot by &#f53b22${event.player.name}")
                val health = target.health - 4.0
                if (health <= 0) {
                    event.player.playerData()?.kills = event.player.playerData()?.kills!! + 1
                    event.player.sendColored("&7You killed &f${rayTraceResult.hitEntity?.name}")
                    target.sendColored("&#f25f4bYou were killed by &#f53b22${event.player.name}")
                    target.playerData()?.deaths = target.playerData()?.deaths!! + 1
                    target.health = 0.0
                } else {
                    target.damage(4.0, event.player)
                }
            }
            val hitLocation = rayTraceResult.hitPosition.toLocation(world)
            drawLine(location, hitLocation, 0.25)
        }
    }

    private fun canHitPlayer(shooter: Player, target: Player): Boolean {
        val team_shooter = shooter.getMetadata("team")[0].asString()
        val team_target = target.getMetadata("team")[0].asString()
        if (LaserTagged.instance.arenaManager?.currentArena?.gamemode == ArenaGamemode.FFA && target.gameMode != GameMode.SPECTATOR && LaserTagged.instance.arenaManager?.currentArena?.isPlayerSafe(target) == false) {
            return true
        }
        return team_shooter != team_target && target.health > 0.0 && target.gameMode != GameMode.SPECTATOR && LaserTagged.instance.arenaManager?.currentArena?.isPlayerSafe(target) == false;
    }

    private fun drawLine(point1: Location, point2: Location, space: Double) {
        val points = point1.distance(point2) * (1/space)
        val delta = Vector(point2.x - point1.x, point2.y - point1.y, point2.z - point1.z).divide(Vector(points, points, points))
        repeat(points.toInt()) {
            val clone = point1.clone().add(delta.clone().multiply(it.toDouble()))
            clone.world.spawnParticle(Particle.REDSTONE, clone.x, clone.y, clone.z, 1, Particle.DustOptions(Color.BLACK, 1f))
        }
    }
}