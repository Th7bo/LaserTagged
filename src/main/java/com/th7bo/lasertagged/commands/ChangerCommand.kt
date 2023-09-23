package com.th7bo.lasertagged.commands

import com.th7bo.lasertagged.LaserTagged
import com.th7bo.lasertagged.arenas.ArenaGamemode
import com.th7bo.lasertagged.utils.sendColored
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.metadata.FixedMetadataValue

class ChangerCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        LaserTagged.instance.arenaManager?.setRandomArena()
        return true;
    }
}