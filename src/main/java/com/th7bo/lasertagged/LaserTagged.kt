package com.th7bo.lasertagged

import com.th7bo.lasertagged.commands.TestingCommand
import com.th7bo.lasertagged.database.DatabaseManager
import com.th7bo.lasertagged.player.ConnectionListener
import com.th7bo.lasertagged.utils.DesignFeatures
import fr.mrmicky.fastboard.FastBoard
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.sql.SQLException
import java.util.*
import com.th7bo.lasertagged.utils.playerData


class LaserTagged : JavaPlugin() {
    val boards: HashMap<UUID, FastBoard> = HashMap<UUID, FastBoard>()
    var databaseManager: DatabaseManager? = null
    companion object {
        public lateinit var instance: LaserTagged
    }
    override fun onEnable() {
        // Plugin startup logic
        instance = this
        logger.info("LaserTagged has been enabled")
        saveDefaultConfig()
        try {
            databaseManager = DatabaseManager()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        enableListeners()
        enableCommands()
    }

    private fun enableListeners() {
        ConnectionListener()
        DesignFeatures()
    }

    private fun enableCommands() {
        getCommand("testing")?.setExecutor(TestingCommand())
    }

    override fun onDisable() {
        logger.info("LaserTagged has been disabled")
        server.onlinePlayers.forEach() {

            databaseManager?.addPlayerData(it)
            databaseManager?.playerDataCached?.remove(it)
            boards[it.uniqueId]?.delete()
        }
    }
}
