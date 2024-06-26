package com.th7bo.lasertagged.utils

import com.th7bo.lasertagged.LaserTagged
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import java.util.regex.Pattern
import com.th7bo.lasertagged.utils.Misc
import org.bukkit.entity.LivingEntity

fun Player.playerData() = LaserTagged.instance.databaseManager?.playerDataCached?.get(this)
fun String.color() = Misc.Color(this)
fun LivingEntity.sendColored(s: String) = s.color()?.let { this.sendMessage(it) }