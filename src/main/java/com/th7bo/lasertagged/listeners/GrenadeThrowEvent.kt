package com.th7bo.lasertagged.listeners

import com.th7bo.lasertagged.LaserTagged
import com.th7bo.lasertagged.utils.playerData
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import org.bukkit.Sound
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.time.Duration

class GrenadeThrowEvent : Listener {
    val item = ItemStack(Material.CARROT_ON_A_STICK)
    private val meta: ItemMeta = item.itemMeta


    init {
        LaserTagged.instance.server.pluginManager.registerEvents(this, LaserTagged.instance)
        meta.setCustomModelData(6121198)
        item.itemMeta = meta
    }

    private var tasks = HashMap<ArmorStand, BukkitTask>()

    @EventHandler
    fun onGrenadeThrow(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return
        if (event.player.inventory.itemInMainHand != item) return
        event.isCancelled = true
        val armorStand =
            event.player.location.world?.spawnEntity(event.player.eyeLocation, EntityType.ARMOR_STAND) as ArmorStand
        armorStand.isInvisible = true
        armorStand.isInvulnerable = true
        armorStand.isSmall = true
        armorStand.setGravity(true)
        armorStand.velocity = armorStand.velocity.add(event.player.location.direction.normalize().multiply(2))
        armorStand.isSilent = true
        armorStand.equipment.helmet = item
        event.player.location.world.playSound(armorStand.location, "entity.horse.saddle", 0.3f, 1.5f)
        val task = Bukkit.getScheduler().runTaskTimer(LaserTagged.instance, Runnable {
            if (armorStand.isOnGround || armorStand.location.clone().add(0.0, -1.0, 0.0).block.type != Material.AIR) {
                event.player.location.world.playSound(armorStand.location, "fb.land", 2.5f, 1f)
                cancelTask(armorStand)
                Bukkit.getScheduler().runTaskLater(LaserTagged.instance, Runnable {
                    doFlashBang(event.player, armorStand)
                }, 20)
            }
        }, 1, 1)
        tasks[armorStand] = task
    }

    private fun cancelTask(armorStand: ArmorStand) = tasks[armorStand]?.cancel()

    private fun doFlashBang(players: Player, armorStand: ArmorStand) {
        val location = armorStand.location
        tasks.remove(armorStand)
        for (player in Bukkit.getOnlinePlayers()) {
            println("Armorstand: $location")
            // check if the player is looking at the armor stand
            if (armorStand.location.distance(player.location) <= 50) {
                if (player.location.direction.angle(armorStand.location.subtract(player.location).toVector()) < 0.5) {
                    val tit: Component = Component.text("\uE000")
                    val subtitle: Component = Component.text("").color(TextColor.color(0,0,0))
                    val title = Title.title(tit, subtitle, Title.Times.times(Duration.ofMillis(0L), Duration.ofMillis(2500L), Duration.ofMillis(3500L)))
                    player.showTitle(title)
                }
            }
            if (armorStand.location.distance(player.location) <= 10) {
                println("Armorstand: <= 10")
                player.playSound(player, "fb.ring1", 1f, 1f)
            } else if (armorStand.location.distance(player.location) <= 20) {
                player.playSound(player, "fb.ring2", 1f, 1f)
                println("Armorstand: <= 20")
            } else if (player.location.direction.angle(armorStand.location.subtract(player.location).toVector()) < 0.5) {
                println("Armorstand: looking + 20")
                player.playSound(player, "fb.ring1", 1f, 1f)
            }
        }
        players.location.world.playSound(armorStand.location, "fb.explode", 3f, 1f)
        players.location.world.spawnParticle(Particle.SMOKE_NORMAL, armorStand.location, 100)
        armorStand.remove()
    }

}