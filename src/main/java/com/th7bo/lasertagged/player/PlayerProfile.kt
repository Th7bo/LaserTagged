package com.th7bo.lasertagged.player

import com.google.gson.JsonArray
import org.bukkit.entity.Player

class PlayerProfile(data: JsonArray, p: Player) {

    var player: Player = p
    var kills: Int = 0
    var deaths: Int = 0
    var shots: Int = 0
    var hits: Int = 0
    var wins: Int = 0
    var losses: Int = 0
    var gamesPlayed: Int = 0
    var accuracy: Double = 0.0
    var weapons: ArrayList<String> = ArrayList()

    init {
        val formatted = data[0].asJsonArray[0].asJsonObject.get("data")
        kills = formatted.asJsonObject["kills"].asInt
        deaths = formatted.asJsonObject["deaths"].asInt
        shots = formatted.asJsonObject["shots"].asInt
        hits = formatted.asJsonObject["hits"].asInt
        wins = formatted.asJsonObject["wins"].asInt
        losses = formatted.asJsonObject["losses"].asInt
        gamesPlayed = formatted.asJsonObject["gamesPlayed"].asInt
        accuracy = formatted.asJsonObject["accuracy"].asDouble
        weapons = try {
            formatted.asJsonObject["weapons"]?.asString?.split(";;")?.let { ArrayList(it) }!!
        } catch (e: Exception) {
            ArrayList()
        }
    }
}