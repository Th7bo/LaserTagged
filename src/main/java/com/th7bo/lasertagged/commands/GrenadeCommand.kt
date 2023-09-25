package com.th7bo.lasertagged.commands

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class GrenadeCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(sender !is Player) return true
        val item = ItemStack(Material.CARROT_ON_A_STICK)
        val meta = item.itemMeta
        meta.setCustomModelData(6121198)
        item.itemMeta = meta
        sender.inventory.addItem(item)
        return true
    }
}