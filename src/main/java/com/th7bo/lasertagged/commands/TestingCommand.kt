package com.th7bo.lasertagged.commands

import com.th7bo.lasertagged.LaserTagged
import com.th7bo.lasertagged.utils.Misc
import com.th7bo.lasertagged.utils.color
import com.th7bo.lasertagged.utils.playerData
import com.th7bo.lasertagged.utils.sendColored
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class TestingCommand : CommandExecutor, TabExecutor {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String>? {
        if(!sender.isOp) {
            return mutableListOf()
        }
        if(args?.size == 1) {
            return LaserTagged.instance.server.onlinePlayers.map { it.name }.toMutableList()
        } else if(args?.size == 2) {
            val list = mutableListOf("kills", "deaths", "shots", "hits", "wins", "losses", "gamesPlayed", "accuracy", "weapons")
            for(i in mutableListOf("kills", "deaths", "shots", "hits", "wins", "losses", "gamesPlayed", "accuracy", "weapons")) {
                if(!i.startsWith(args[1])) {
                    list.remove(i)
                }
            }
            return list
        }
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        // check if executor is console
        if (sender !is Player) {
            sender.sendMessage("You must be a player to run this command")
            return true
        }

        val player = sender as Player
        if(!player.isOp) {
            sender.sendColored("&#f53b22ERROR &#f25f4bYou must be an operator to run this command")
            return true
        }
        val target = player.server.getPlayer(args?.get(0)!!)
        if(target == null) {
            sender.sendColored("&#f53b22ERROR &#f25f4bPlayer not found")
            return true
        }
        if(args.size < 3) {
            sender.sendColored("&#f53b22ERROR &#f25f4bYou must specify a player, a stat and a value")
            return true
        }
        val playerData = target.playerData()
        if (playerData == null) {
            sender.sendMessage("")
            return true
        }
        try {
            when (args[1]) {
                "kills" -> playerData.kills = args[2].toInt()
                "deaths" -> playerData.deaths = args[2].toInt()
                "shots" -> playerData.shots = args[2].toInt()
                "hits" -> playerData.hits = args[2].toInt()
                "wins" -> playerData.wins = args[2].toInt()
                "losses" -> playerData.losses = args[2].toInt()
                "gamesPlayed" -> playerData.gamesPlayed = args[2].toInt()
                "accuracy" -> playerData.accuracy = args[2].toDouble()
                "weapons" -> playerData.weapons = args[2].split(",").toTypedArray().toCollection(ArrayList())
                else -> {
                    sender.sendColored("&#f53b22ERROR &#f25f4bInvalid stat")
                    return true
                }
            }
        } catch (e: NumberFormatException) {
            sender.sendColored("&#f53b22ERROR &#f25f4bInvalid value for " + args[2])
            return true
        }
        val name = target.name
        val arg = args[1]
        val value = args[2]
        sender.sendColored("&aSet $arg &#FF0000to $value for $name")

        return true
    }
}