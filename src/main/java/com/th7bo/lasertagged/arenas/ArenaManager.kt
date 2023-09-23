package com.th7bo.lasertagged.arenas

import com.fastasyncworldedit.core.FaweAPI
import com.sk89q.worldedit.EditSession
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.WorldEditException
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extension.input.ParserContext
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.function.pattern.Pattern
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.regions.Region
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.world.World
import com.th7bo.lasertagged.LaserTagged
import com.th7bo.lasertagged.utils.Misc
import com.th7bo.lasertagged.utils.color
import com.th7bo.lasertagged.utils.playerData
import com.th7bo.lasertagged.utils.sendColored
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.util.Vector
import java.io.File
import java.io.IOException
import java.nio.file.Files

class ArenaManager {

    var currentArena: Arena? = null
    val pf = WorldEdit.getInstance().patternFactory
    val pc = ParserContext()
    val session = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld("world"))
    var timeStarted: Long? = System.currentTimeMillis()
    companion object {
        var arenas: HashMap<String, Arena> = HashMap<String, Arena>()

    }

    init {
        if(LaserTagged.instance.arenaManager != null) {
            LaserTagged.instance.logger.warning("ArenaManager already initialized")
        } else {
            timeStarted = System.currentTimeMillis()
            pc.actor = BukkitAdapter.adapt(Bukkit.getConsoleSender())
            arenas["Pirates"] = Arena("Pirates", ArenaGamemode.FFA, arrayListOf(Location(Bukkit.getWorld("world"), 38.5, -45.0, 22.5), Location(Bukkit.getWorld("world"), -54.5, -26.0, -33.5)),
                arrayListOf(Vector(-2, 0, -2), Vector(2, 3, 2)))
            arenas["Ruins"] = Arena("Ruins", ArenaGamemode.FFA, arrayListOf(Location(Bukkit.getWorld("world"), -3.5, -41.0, 2.5), Location(Bukkit.getWorld("world"), -10.5, -29.0, -102.5)),
                arrayListOf(Vector(-2, 0, -2), Vector(2, 3, 2)))
            arenas["Winter"] = Arena("Winter", ArenaGamemode.FFA, arrayListOf(Location(Bukkit.getWorld("world"), 105.0, -38.0, -14.5), Location(Bukkit.getWorld("world"), 79.5, -38.0, 37.5),
                Location(Bukkit.getWorld("world"), -46.5, -27.0, 48.5)),
                arrayListOf(Vector(-2, 0, -2), Vector(2, 3, 2)))
            setRandomArena()
            startRunnable()
        }
    }

    private fun startRunnable() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(LaserTagged.instance, Runnable {
            for (cuboid in currentArena?.safeZoneCuboids?.values ?: emptyList()) {
                for (location in Misc.cube(cuboid.point1, cuboid.point2, 4.0)) {
                    location.world.spawnParticle(org.bukkit.Particle.VILLAGER_HAPPY, location, 1, 0.0, 0.0, 0.0, 0.0)
                }
            }
        }, 0, 10)
    }

    fun createArena(name: String, gamemode: ArenaGamemode, spawns: ArrayList<Location>, safeZones:ArrayList<Vector>) {
        arenas[name] = Arena(name, gamemode, spawns, safeZones)
        currentArena = arenas[name]
    }


    fun cutArea() {
        Bukkit.getOnlinePlayers().forEach() {
            it.sendColored("&#f53b22&lWARNING: &r&#f25f4bThe arena is being cleaned, please wait a few seconds")
        }
        val loc1 = BukkitAdapter.asBlockVector(Location(Bukkit.getWorld("world"), -250.0, 65.0, -250.0))
        val loc2 = BukkitAdapter.asBlockVector(Location(Bukkit.getWorld("world"), 250.0, -64.0, 250.0))
        val region = CuboidRegion(loc1, loc2) as Region
        val pattern: Pattern = pf.parseFromInput("100%air", pc)

        session.setBlocks(region, pattern)
        session.flushQueue()
    }
    fun pasteSchematic(arena: Arena) {
        Bukkit.getOnlinePlayers().forEach() {
            it.sendColored("&#f53b22&lWARNING: &r&#f25f4bPasting schematic, please wait a few seconds")
        }
        val file = File(LaserTagged.instance.server.pluginManager.getPlugin("FastAsyncWorldEdit")?.dataFolder, "/schematics/${arena.name}.schematic")
        if (!file.exists()) {
            println("Schematic for ${arena.name} not found")
            return
        }
        val clipboardFormat: ClipboardFormat? = ClipboardFormats.findByFile(file)
        var clipboard: Clipboard

        val location = Location(
            Bukkit.getWorld("world"),
            0.0,
            -40.0,
            0.0
        )

        val blockVector3: BlockVector3 = BlockVector3.at(location.blockX, location.blockY, location.blockZ)

        if (clipboardFormat != null) {
            var operation: Operation? = null
            try {
                clipboardFormat.getReader(Files.newInputStream(file.toPath())).use { clipboardReader ->
                    if (location.getWorld() == null) throw NullPointerException("Failed to paste schematic due to world being null")
                    val world: World = FaweAPI.getWorld("world")
                    val editSession: EditSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build()
                    clipboard = clipboardReader.read()
                    operation = ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(blockVector3)
                        .ignoreAirBlocks(true)
                        .build()
                    try {
                        Operations.complete(operation)
                        editSession.close()
                    } catch (e: WorldEditException) {
                        println("woops?")
                        e.printStackTrace()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            println(operation?.statusMessages)
        }
    }

    fun setRandomArena() {
        timeStarted = null;
        val arena = arenas.values.random()
        Bukkit.getOnlinePlayers().forEach() {
            it.teleport(Location(Bukkit.getWorld("world"), -119.5, 209.0, 14.5, 90.0f, -7.0f))
        }
        // run next line after 10 seconds
        Bukkit.getScheduler().scheduleSyncDelayedTask(LaserTagged.instance, Runnable {
            try {
                LaserTagged.instance.logger.info("Cleaning up")
                cutArea()
            } finally {
                LaserTagged.instance.logger.info("Finally #1")
                try {
                    LaserTagged.instance.logger.info("Pasting schematic #2")
                    pasteSchematic(arena)
                } finally {
                    LaserTagged.instance.logger.info("Finally #2")
                    currentArena = arena

                    Bukkit.broadcastMessage("Arena set to ${arena.name}")
                    for (player in Bukkit.getOnlinePlayers()) {
                        player.removeMetadata("team", LaserTagged.instance)
                        player.setMetadata("safezone", FixedMetadataValue(LaserTagged.instance, true))
                        player.playerData()?.gamesPlayed = player.playerData()?.gamesPlayed?.plus(1) ?: 1
                    }
                    for (player in Bukkit.getOnlinePlayers().shuffled()) {
                        val gamemode = LaserTagged.instance.arenaManager?.currentArena?.gamemode
                        var team = 1
                        if (gamemode == ArenaGamemode.FFA) {
                            player.setMetadata("team", FixedMetadataValue(LaserTagged.instance, 1))
                        } else {
                            team = LaserTagged.instance.arenaManager?.currentArena?.getTeamWithLeastPlayers()!!
                            player.setMetadata("team", FixedMetadataValue(LaserTagged.instance, team))
                        }
                        val spawnLocation = LaserTagged.instance.arenaManager?.currentArena?.getRandomSpawn(team)
                        player.sendColored("&aYou joined the game! You are on team $team&r!")
                        if (spawnLocation != null) {
                            player.teleport(spawnLocation)
                        }
                    }
                    runEndRunnable()
                }

            }
        }, 20L)
    }
    fun runEndRunnable() {
        // run next line after 10 seconds
        val arena = currentArena
        timeStarted = System.currentTimeMillis()
        Bukkit.getScheduler().scheduleSyncDelayedTask(LaserTagged.instance, Runnable {
            if (arena == currentArena) {
                setRandomArena()
            }
        }, 15 * 60 * 20L)
    }
}