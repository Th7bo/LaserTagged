package com.th7bo.lasertagged.listeners

import com.th7bo.lasertagged.LaserTagged
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
            // do other checks
            val world: World = event.player.world
            val location: Location = event.player.eyeLocation
            val direction: Vector = location.getDirection()
            val rayTraceResult: RayTraceResult? = event.player.world.rayTrace(
                event.player.eyeLocation, direction, 100.0,
                FluidCollisionMode.NEVER, false, 0.0
            ) { entity -> entity !== event.player && entity is Player && !entity.hasMetadata("last_hit") }
            if (rayTraceResult?.hitEntity == null) {
                val hitLocation = rayTraceResult?.hitPosition?.toLocation(world)
                if (hitLocation != null) {
                    drawLine(location, hitLocation, 0.25)
                }
                return
            }
            event.player.sendMessage("You shot at ${rayTraceResult.hitEntity?.name}")
            val hitLocation = rayTraceResult.hitPosition.toLocation(world)
            drawLine(location, hitLocation, 0.25)
        }
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