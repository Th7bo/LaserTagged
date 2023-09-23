package com.th7bo.lasertagged.utils

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class Cuboid(point1: Location, point2: Location) {
    private val xMin: Int
    private val xMax: Int
    private val yMin: Int
    private val yMax: Int
    private val zMin: Int
    private val zMax: Int
    private val xMinCentered: Double
    private val xMaxCentered: Double
    private val yMinCentered: Double
    private val yMaxCentered: Double
    private val zMinCentered: Double
    private val zMaxCentered: Double
    private val world: World

    init {
        xMin = min(point1.blockX.toDouble(), point2.blockX.toDouble()).toInt()
        xMax = max(point1.blockX.toDouble(), point2.blockX.toDouble()).toInt()
        yMin = min(point1.blockY.toDouble(), point2.blockY.toDouble()).toInt()
        yMax = max(point1.blockY.toDouble(), point2.blockY.toDouble()).toInt()
        zMin = min(point1.blockZ.toDouble(), point2.blockZ.toDouble()).toInt()
        zMax = max(point1.blockZ.toDouble(), point2.blockZ.toDouble()).toInt()
        world = point1.getWorld()
        xMinCentered = xMin + 0.5
        xMaxCentered = xMax + 0.5
        yMinCentered = yMin + 0.5
        yMaxCentered = yMax + 0.5
        zMinCentered = zMin + 0.5
        zMaxCentered = zMax + 0.5
    }

    fun blockList(): Iterator<Block> {
        val bL: MutableList<Block> = ArrayList(
            totalBlockSize
        )
        for (x in xMin..xMax) {
            for (y in yMin..yMax) {
                for (z in zMin..zMax) {
                    val b = world.getBlockAt(x, y, z)
                    bL.add(b)
                }
            }
        }
        return bL.iterator()
    }

    val center: Location
        get() = Location(
            world,
            ((xMax - xMin) / 2 + xMin).toDouble(),
            ((yMax - yMin) / 2 + yMin).toDouble(),
            ((zMax - zMin) / 2 + zMin).toDouble()
        )
    val distance: Double
        get() = point1.distance(point2)
    val distanceSquared: Double
        get() = point1.distanceSquared(point2)
    val height: Int
        get() = yMax - yMin + 1
    val point1: Location
        get() = Location(world, xMin.toDouble(), yMin.toDouble(), zMin.toDouble())
    val point2: Location
        get() = Location(world, xMax.toDouble(), yMax.toDouble(), zMax.toDouble())
    val randomLocation: Location
        get() {
            val rand = Random()
            val x = rand.nextInt((abs((xMax - xMin).toDouble()) + 1).toInt()) + xMin
            val y = rand.nextInt((abs((yMax - yMin).toDouble()) + 1).toInt()) + yMin
            val z = rand.nextInt((abs((zMax - zMin).toDouble()) + 1).toInt()) + zMin
            return Location(world, x.toDouble(), y.toDouble(), z.toDouble())
        }
    val totalBlockSize: Int
        get() = height * xWidth * zWidth
    val xWidth: Int
        get() = xMax - xMin + 1
    val zWidth: Int
        get() = zMax - zMin + 1

    fun isIn(loc: Location): Boolean {
        return loc.getWorld() === world && loc.blockX >= xMin && loc.blockX <= xMax && loc.blockY >= yMin && loc.blockY <= yMax && loc
            .blockZ >= zMin && (loc.blockZ <= zMax)
    }

    fun isIn(player: Player): Boolean {
        return this.isIn(player.location)
    }

    fun isInWithMarge(loc: Location, marge: Double): Boolean {
        return (loc.getWorld() === world) && (loc.x >= xMinCentered - marge) && (loc.x <= xMaxCentered + marge) && (loc.y >= yMinCentered - marge) && (loc
            .y <= yMaxCentered + marge) && (loc.z >= zMinCentered - marge) && (loc.z <= zMaxCentered + marge)
    }
}