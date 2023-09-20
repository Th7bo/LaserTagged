package com.th7bo.lasertagged.database

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.th7bo.lasertagged.LaserTagged
import com.th7bo.lasertagged.player.PlayerProfile
import fr.mrmicky.fastboard.FastBoard
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

class DatabaseManager {

    var playerDataCached: HashMap<Player, PlayerProfile> = HashMap()


    private var instance: LaserTagged = LaserTagged.instance
    private var connectionUrl: String = "jdbc:sqlite:" + instance.dataFolder + "/storage.db";
    val defaultArray: JsonArray = JsonArray()
    private var defaultElement: JsonElement = JsonParser.parseString("{ \n" +
            "    \"data\": { \n" +
            "        \"kills\": 0, \n" +
            "        \"deaths\": 0, \n" +
            "        \"shots\": 0, \n" +
            "        \"hits\": 0, \n" +
            "        \"wins\": 0, \n" +
            "        \"losses\": 0, \n" +
            "        \"gamesPlayed\": 0, \n" +
            "        \"accuracy\": 0.0, \n" +
            "        \"weapons\": \"\" \n" +
            "    } \n" +
            "}")


    init {
        defaultArray.add(defaultElement)
        println("defaultArray: $defaultArray")
        try {
            setupDatabase()
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
    }

    @Throws(SQLException::class)
    private fun setupDatabase() {
        val file: File = File(instance.dataFolder, "/storage.db")
        if (!file.exists()) {
            try {
                val conn = getConnection()
                if (conn != null) {
                    Bukkit.getLogger()
                        .info("Created DataBase: $connectionUrl")
                    conn.close()
                }
            } catch (e: SQLException) {
                Bukkit.getLogger().severe("Error creating folder: SQLite")
            }
        }
        try {
            getConnection().use { conn ->
                if (!conn.isValid(1)) {
                    throw SQLException("Could not establish database connection.")
                } else {
                    instance.logger.info("Connected to database.")
                }
            }
        } catch (e: SQLException) {
            throw java.lang.RuntimeException(e)
        } catch (e: ClassNotFoundException) {
            throw java.lang.RuntimeException(e)
        }
        val con = getConnection()
        val sql = "CREATE TABLE IF NOT EXISTS players (\n" +
                "\tuuid text PRIMARY KEY,\n" +
                "\tdata json NOT NULL\n" +
                "\t\n);"
        val stmt = con.createStatement()
        stmt.execute(sql)
        con.close()
        stmt.close()

        LaserTagged.instance.server.onlinePlayers.forEach() {
            var playerData = getPlayerData(it)
            if (playerData == null) {
                playerData = defaultArray
            }
            val raw = playerData.toString()
            playerData = JsonParser.parseString(raw).asJsonArray
            val playerProfile = PlayerProfile(playerData, it)
            playerDataCached[it] = playerProfile

            val board = FastBoard(it)

            LaserTagged.instance.boards[it.uniqueId] = board
        }
    }

    fun getPlayerData(player: Player): JsonArray {
        try {
            getConnection().use { conn ->
                conn.prepareStatement(
                    "SELECT uuid, data FROM players WHERE uuid = ?;"
                ).use { stmt ->
                    stmt.setString(1, player.uniqueId.toString())
                    val resultSet = stmt.executeQuery()
                    if (resultSet.getString("data") == null) {
                        println("Player not found in database, adding...")
                        addPlayer(player)
                        return getPlayerData(player)
                    }
                    val array = JsonArray()
                    val obj: JsonElement = JsonParser.parseString(resultSet.getString("data")?:defaultArray.toString())
                    println("Player found in database, returning data... $obj")
                    array.add(obj)
                    return array
                }
            }
        } catch (e: SQLException) {
            return defaultArray
        }
    }

    fun addPlayerData(player: Player): Boolean {
        if (getPlayerData(player).isEmpty) addPlayer(player)
        try {
            getConnection().use { conn ->
                conn.prepareStatement(
                    "UPDATE players SET data = ? WHERE uuid = ?"
                ).use { stmt ->
                    stmt.setString(1, (formatPlayerData(player).toString()))
                    stmt.setString(2, player.uniqueId.toString())
                    stmt.execute()
                    return true
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false
    }

    fun addPlayer(player: Player) {
        try {
            getConnection().use { conn ->
                conn.prepareStatement(
                    "INSERT INTO players(uuid, data) VALUES(?,?)"
                ).use { stmt ->
                    stmt.setString(1, player.uniqueId.toString())
                    stmt.setString(2, defaultArray.toString())
                    stmt.execute()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun formatPlayerData(player: Player): JsonArray {
        val playerData = playerDataCached[player]
        val json = JsonParser.parseString("{ \n" +
                "    \"data\": { \n" +
                "        \"kills\": "+ (playerData?.kills ?: 0) +", \n" +
                "        \"deaths\": "+ (playerData?.deaths ?: 0) +", \n" +
                "        \"shots\": "+ (playerData?.shots ?: 0) +", \n" +
                "        \"hits\": "+ (playerData?.hits ?: 0) +", \n" +
                "        \"wins\": "+ (playerData?.wins ?: 0) +", \n" +
                "        \"losses\": "+ (playerData?.losses ?: 0) +", \n" +
                "        \"gamesPlayed\": "+ (playerData?.gamesPlayed ?: 0) +", \n" +
                "        \"accuracy\": "+ (playerData?.accuracy ?: 0.0) +", \n" +
                "        \"weapons\": \""+ (playerData?.weapons?.joinToString(separator = ";;") ?: "") +"\" \n" +
                "    } \n" +
                "}")
        val array = JsonArray()
        array.add(json)
        return array
    }

    @Throws(SQLException::class)
    fun getConnection(): Connection {
        return DriverManager.getConnection(connectionUrl);
    }
}