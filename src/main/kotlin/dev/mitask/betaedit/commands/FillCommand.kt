package dev.mitask.betaedit.commands

import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.mitask.betaedit.BetaEdit
import dev.mitask.betaedit.util.Cuboid
import net.glasslauncher.glassbrigadier.api.argument.coordinate.CoordinateArgumentType.getCoordinate
import net.glasslauncher.glassbrigadier.api.argument.coordinate.CoordinateArgumentType.intCoordinate
import net.glasslauncher.glassbrigadier.api.argument.tileid.BlockId
import net.glasslauncher.glassbrigadier.api.argument.tileid.BlockIdArgumentType.getTileId
import net.glasslauncher.glassbrigadier.api.argument.tileid.BlockIdArgumentType.tileId
import net.glasslauncher.glassbrigadier.api.command.CommandProvider
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource
import net.glasslauncher.glassbrigadier.api.predicate.HasPermission.booleanPermission
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder.argument
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder.literal
import net.minecraft.util.math.Vec3i
import net.minecraft.world.LightType
import net.modificationstation.stationapi.api.registry.BlockRegistry
import kotlin.system.measureTimeMillis

class FillCommand : CommandProvider {
    override fun get(): LiteralArgumentBuilder<GlassCommandSource?>? {
        val root = literal("fill")
            .requires(booleanPermission("betaedit.fill"))

        val pos1 = argument("pos1", intCoordinate())
        val pos2 = argument("pos2", intCoordinate())
            .then(argument("id", tileId()).apply {
                executes { fill(it) }

                then(argument("meta", integer()).apply {
                    executes { fillWithMeta(it) }
                })
            })

        return root.then(pos1.then(pos2))
    }

    fun fill(context: CommandContext<GlassCommandSource?>): Int {
        val pos1: Vec3i = getCoordinate(context, "pos1").getVec3i(context.getSource())
        val pos2: Vec3i = getCoordinate(context, "pos2").getVec3i(context.getSource())
        val block: BlockId = getTileId(context, "id")
        val blockName = if(block.numericId == 0) "Air" else BlockRegistry.INSTANCE.get(block.id)?.translatedName ?: "Unknown"

        val cuboid = Cuboid(pos1, pos2)
        val world = (context.getSource())?.world!!
        var success = 0

        BetaEdit.tasks.add(cuboid)
        val time = measureTimeMillis {
            cuboid.forEach { x, y, z ->
                world.setBlock(x, y, z, block.numericId)
                success++
            }
        }
        BetaEdit.tasks.remove(cuboid)

        sendFeedbackAndLog(
            context.getSource(),
            "§aReplaced $success/${cuboid.volume} blocks with $blockName in ${if(time > 5000) "${time / 1000} seconds" else "$time ms"}"
        )

        val subCuboids = cuboid.getSubCuboids()
        sendFeedbackAndLog(
            context.getSource(),
            "§eQueueing ${subCuboids.size * 2} light updates"
        )
        for (sub in subCuboids) {
            world.queueLightUpdate(LightType.SKY, sub.minX, sub.minY, sub.minZ, sub.maxX, sub.maxY, sub.maxZ, false)
            world.queueLightUpdate(LightType.BLOCK, sub.minX, sub.minY, sub.minZ, sub.maxX, sub.maxY, sub.maxZ, false)
        }

        return 0
    }

    fun fillWithMeta(context: CommandContext<GlassCommandSource?>): Int {
        val pos1: Vec3i = getCoordinate(context, "pos1").getVec3i(context.getSource())
        val pos2: Vec3i = getCoordinate(context, "pos2").getVec3i(context.getSource())
        val block: BlockId = getTileId(context, "id")
        val meta: Int = getInteger(context, "meta")
        val blockName = if(block.numericId == 0) "Air" else BlockRegistry.INSTANCE.get(block.id)?.translatedName ?: "Unknown"

        val cuboid = Cuboid(pos1, pos2)
        val world = (context.getSource())?.world!!
        var success = 0

        BetaEdit.tasks.add(cuboid)
        val time = measureTimeMillis {
            cuboid.forEach { x, y, z ->
                world.setBlock(x, y, z, block.numericId, meta)
                success++
            }
        }
        BetaEdit.tasks.remove(cuboid)

        sendFeedbackAndLog(
            context.getSource(),
            "§aReplaced $success/${cuboid.volume} blocks with $blockName (meta: $meta) in ${if(time > 5000) "${time / 1000} seconds" else "$time ms"}"
        )

        val subCuboids = cuboid.getSubCuboids()
        sendFeedbackAndLog(
            context.getSource(),
            "§eQueueing ${subCuboids.size * 2} light updates"
        )
        for (sub in subCuboids) {
            world.queueLightUpdate(LightType.SKY, sub.minX, sub.minY, sub.minZ, sub.maxX, sub.maxY, sub.maxZ, false)
            world.queueLightUpdate(LightType.BLOCK, sub.minX, sub.minY, sub.minZ, sub.maxX, sub.maxY, sub.maxZ, false)
        }
        return 0
    }
}