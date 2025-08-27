package dev.mitask.betaedit.util

import net.minecraft.util.math.Vec3i
import kotlin.math.max
import kotlin.math.min

data class Cuboid(val pos1: Vec3i, val pos2: Vec3i) {
    val minX: Int = min(pos1.x, pos2.x)
    val maxX: Int = max(pos1.x, pos2.x)
    val minY: Int = max(0, min(pos1.y, pos2.y))
    val maxY: Int = min(127, max(pos1.y, pos2.y))
    val minZ: Int = min(pos1.z, pos2.z)
    val maxZ: Int = max(pos1.z, pos2.z)
    val volume: Long = (maxX - minX + 1L) * (maxY - minY + 1L) * (maxZ - minZ + 1L)

    fun isInside(other: Cuboid): Boolean {
        if (minY > maxY) return false
        return minX >= other.minX && maxX <= other.maxX &&
                minY >= other.minY && maxY <= other.maxY &&
                minZ >= other.minZ && maxZ <= other.maxZ
    }

    fun forEach(action: (x: Int, y: Int, z: Int) -> Unit) {
        val minChunkX = minX shr 4
        val maxChunkX = maxX shr 4
        val minChunkZ = minZ shr 4
        val maxChunkZ = maxZ shr 4

        for (chunkX in minChunkX..maxChunkX) {
            for (chunkZ in minChunkZ..maxChunkZ) {
                val localMinX = max(minX, chunkX shl 4)
                val localMaxX = min(maxX, (chunkX shl 4) + 15)
                val localMinZ = max(minZ, chunkZ shl 4)
                val localMaxZ = min(maxZ, (chunkZ shl 4) + 15)

                for (x in localMinX..localMaxX) {
                    for (y in minY..maxY) {
                        for (z in localMinZ..localMaxZ) {
                            action(x, y, z)
                        }
                    }
                }
            }
        }
    }

    operator fun iterator(): Iterator<Vec3i> = object : Iterator<Vec3i> {
        private val minChunkX = minX shr 4
        private val maxChunkX = maxX shr 4
        private val minChunkZ = minZ shr 4
        private val maxChunkZ = maxZ shr 4

        private var currentChunkX = minChunkX
        private var currentChunkZ = minChunkZ

        private var currentX = max(minX, currentChunkX shl 4)
        private var currentY = minY
        private var currentZ = max(minZ, currentChunkZ shl 4)

        private var localMaxX = min(maxX, (currentChunkX shl 4) + 15)
        private var localMaxZ = min(maxZ, (currentChunkZ shl 4) + 15)

        override fun hasNext(): Boolean {
            return currentChunkX <= maxChunkX
        }

        override fun next(): Vec3i {
            val pos = Vec3i(currentX, currentY, currentZ)

            currentZ++
            if (currentZ > localMaxZ) {
                currentZ = max(minZ, currentChunkZ shl 4)
                currentY++
            }
            if (currentY > maxY) {
                currentY = minY
                currentX++
            }
            if (currentX > localMaxX) {
                currentChunkZ++
                if (currentChunkZ > maxChunkZ) {
                    currentChunkZ = minChunkZ
                    currentChunkX++
                }
                if (currentChunkX <= maxChunkX) {
                    currentX = max(minX, currentChunkX shl 4)
                    currentY = minY
                    currentZ = max(minZ, currentChunkZ shl 4)
                    localMaxX = min(maxX, (currentChunkX shl 4) + 15)
                    localMaxZ = min(maxZ, (currentChunkZ shl 4) + 15)
                }
            }

            return pos
        }
    }

    fun getSubCuboids(maxVol: Int = 32768): List<Cuboid> {
        if (volume <= 0) return emptyList()
        if (volume <= maxVol) return listOf(this)

        val dx = maxX - minX + 1L
        val dy = maxY - minY + 1L
        val dz = maxZ - minZ + 1L

        val splitAxis = when {
            dx >= dy && dx >= dz -> 0
            dz >= dy -> 2
            else -> 1
        }

        val mid: Int = when (splitAxis) {
            0 -> minX + ((dx / 2).toInt())
            1 -> minY + ((dy / 2).toInt())
            else -> minZ + ((dz / 2).toInt())
        }

        val left: Cuboid
        val right: Cuboid
        when (splitAxis) {
            0 -> {
                left = Cuboid(Vec3i(minX, minY, minZ), Vec3i(mid, maxY, maxZ))
                right = Cuboid(Vec3i(mid + 1, minY, minZ), Vec3i(maxX, maxY, maxZ))
            }
            1 -> {
                left = Cuboid(Vec3i(minX, minY, minZ), Vec3i(maxX, mid, maxZ))
                right = Cuboid(Vec3i(minX, mid + 1, minZ), Vec3i(maxX, maxY, maxZ))
            }
            else -> {
                left = Cuboid(Vec3i(minX, minY, minZ), Vec3i(maxX, maxY, mid))
                right = Cuboid(Vec3i(minX, minY, mid + 1), Vec3i(maxX, maxY, maxZ))
            }
        }

        return left.getSubCuboids(maxVol) + right.getSubCuboids(maxVol)
    }
}