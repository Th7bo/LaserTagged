package com.th7bo.lasertagged

import com.th7bo.lasertagged.arenas.ArenaManager
import com.th7bo.lasertagged.commands.ChangerCommand
import com.th7bo.lasertagged.commands.GrenadeCommand
import com.th7bo.lasertagged.commands.TestingCommand
import com.th7bo.lasertagged.database.DatabaseManager
import com.th7bo.lasertagged.listeners.DeathEvent
import com.th7bo.lasertagged.listeners.GrenadeThrowEvent
import com.th7bo.lasertagged.listeners.GunShootEvent
import com.th7bo.lasertagged.listeners.MoveEvent
import com.th7bo.lasertagged.player.ConnectionListener
import com.th7bo.lasertagged.utils.DesignFeatures
import com.th7bo.lasertagged.utils.Placeholders
import fr.mrmicky.fastboard.FastBoard
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.sql.SQLException
import java.util.*
import com.th7bo.lasertagged.utils.playerData
import org.bukkit.Bukkit


class LaserTagged : JavaPlugin() {
    val boards: HashMap<UUID, FastBoard> = HashMap<UUID, FastBoard>()
    var databaseManager: DatabaseManager? = null
    var arenaManager: ArenaManager? = null
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
        arenaManager = ArenaManager()
        enableListeners()
        enableCommands()
        enableFeatures()
    }

    private fun enableListeners() {
        ConnectionListener()
        GunShootEvent()
        DeathEvent()
        MoveEvent()
        GrenadeThrowEvent()
    }

    private fun enableFeatures() {
        Placeholders().register()
        DesignFeatures()
    }

    private fun enableCommands() {
        getCommand("testing")?.setExecutor(TestingCommand())
        getCommand("changer")?.setExecutor(ChangerCommand())
        getCommand("getfb")?.setExecutor(GrenadeCommand())
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
