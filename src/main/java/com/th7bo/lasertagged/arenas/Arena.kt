package com.th7bo.lasertagged.arenas

import com.th7bo.lasertagged.utils.Cuboid
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class Arena(name: String, gamemode: ArenaGamemode, spawns: ArrayList<Location>, safeZones: ArrayList<Vector>) {

    var name: String = name
    var gamemode: ArenaGamemode = gamemode
    var spawns: ArrayList<Location> = spawns
    var safeZones: ArrayList<Vector> = safeZones
    var safeZoneCuboids: HashMap<Int, Cuboid> = HashMap()

    init {
        ArenaManager.arenas[name] = this
        // loop through spawns and create cuboids
        repeat(spawns.size) {
            safeZoneCuboids[it] = Cuboid(spawns[it].clone().add(safeZones[0]), spawns[it].clone().add(safeZones[1]))
        }
    }

    fun getRandomSpawn(team: Int): Location {
        if (gamemode == ArenaGamemode.FFA) {
            return spawns[(Math.random() * spawns.size).toInt()]
        }
        if (team - 1 < 0) {
            return spawns[team]
        }
        return spawns[team - 1]
    }

    fun isPlayerSafe(p: Player): Boolean {
        return p.hasMetadata("safezone")
    }

    fun getTeamWithLeastPlayers(): Int {
        if (gamemode == ArenaGamemode.FFA) {
            return 1
        }
        var teams = HashMap<Int, Int>(3)
        teams[1] = 0
        teams[2] = 0
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.hasMetadata("team")) {
                val teamId = player.getMetadata("team")[0].asInt()
                teams[teamId] = teams.getOrDefault(teamId, 0) + 1
            }
        }
        // get team with least players
        var teamId = 1
        var teamSize = Int.MAX_VALUE
        for (team in teams) {
            println("Team: ${team.key}")
            println("Members: ${team.value}")
            if (team.value < teamSize) {
                println("Team ${team.key} (${team.value}) has less players than $teamId ($teamSize)")
                teamId = team.key
                teamSize = team.value
            }
        }
        if (teamId == 0) {
            // set teamid to random team and only choose from 1 and 2
            teamId = (Math.random() * 2 + 1).toInt()
        }
        return teamId
    }

}