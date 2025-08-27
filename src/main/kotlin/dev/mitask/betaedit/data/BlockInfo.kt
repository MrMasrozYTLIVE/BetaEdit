package dev.mitask.betaedit.data

import net.minecraft.util.math.Vec3i

data class BlockInfo(
    val pos: Vec3i,
    val blockId: Int,
    val meta: Int
)
