package dev.mitask.betaedit.data

import dev.mitask.betaedit.util.Cuboid
import net.minecraft.world.World

data class HistoryEdit(
    val world: World,
    val cuboid: Cuboid,
    val blocks: MutableList<BlockInfo> = mutableListOf()
)