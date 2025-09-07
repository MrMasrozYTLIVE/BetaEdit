package dev.mitask.betaedit.commands

import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.mitask.betaedit.BetaEdit
import dev.mitask.betaedit.data.BlockInfo
import dev.mitask.betaedit.data.HistoryEdit
import dev.mitask.betaedit.data.User
import dev.mitask.betaedit.util.Cuboid
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

class SetCommand : CommandProvider {
    override fun get(): LiteralArgumentBuilder<GlassCommandSource> {
        return literal("/set")
            .requires {
                if(it.player == null) {
                    it.sendFeedback("Only players can execute this command!")
                    return@requires false
                }

                return@requires booleanPermission("betaedit.set").test(it)
            }
            .then(argument("id", tileId()).apply {
                executes { set(it) }

                then(argument("meta", integer()).apply {
                    executes { setWithMeta(it) }
                })
            })
    }

    fun set(context: CommandContext<GlassCommandSource>): Int {
        val block: BlockId = getTileId(context, "id")
        val user = BetaEdit.users[context.source.player?.name ?: ""]
        if(user == null) {
            context.source.sendFeedback("§cSomething went wrong! (User == null)")
            return 1
        }
        val blockName = if(block.numericId == 0) "Air" else BlockRegistry.INSTANCE.get(block.id)?.translatedName ?: "Unknown"

        if(user.pos1 == null || user.pos2 == null) {
            context.source.sendFeedback("§cPos1 or Pos2 is not set!")
            return 1
        }

        val world = (context.getSource())?.world!!
        val cuboid = Cuboid(user.pos1!!, user.pos2!!)
        val history = HistoryEdit(world, cuboid, changedToId = block.numericId)

        BetaEdit.tasks.add(cuboid)
        val time = measureTimeMillis {
            cuboid.forEach { x, y, z ->
                history.blocks.add(BlockInfo(Vec3i(x, y, z), world.getBlockId(x, y, z), world.getBlockMeta(x, y, z)))
                world.setBlock(x, y, z, block.numericId, 0)
            }
        }
        BetaEdit.tasks.remove(cuboid)
        user.addHistory(history)

        sendFeedbackAndLog(
            context.getSource(),
            "§aSet ${cuboid.volume} blocks to $blockName in ${if(time > 5000) "${time / 1000} seconds" else "$time ms"}"
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

    fun setWithMeta(context: CommandContext<GlassCommandSource>): Int {
        val meta: Int = getInteger(context, "meta")
        val block: BlockId = getTileId(context, "id")
        val user = BetaEdit.users[context.source.player?.name ?: ""] ?: User()
        val blockName = if(block.numericId == 0) "Air" else BlockRegistry.INSTANCE.get(block.id)?.translatedName ?: "Unknown"

        if(user.pos1 == null || user.pos2 == null) {
            context.source.sendFeedback("§cPos1 or Pos2 is not set!")
            return 1
        }

        val world = (context.getSource())?.world!!
        val cuboid = Cuboid(user.pos1!!, user.pos2!!)
        val history = HistoryEdit(world, cuboid, changedToId = block.numericId, changedToMeta = meta)

        BetaEdit.tasks.add(cuboid)
        val time = measureTimeMillis {
            cuboid.forEach { x, y, z ->
                history.blocks.add(BlockInfo(Vec3i(x, y, z), world.getBlockId(x, y, z), world.getBlockMeta(x, y, z)))
                world.setBlock(x, y, z, block.numericId, meta)
            }
        }
        BetaEdit.tasks.remove(cuboid)
        user.addHistory(history)

        sendFeedbackAndLog(
            context.getSource(),
            "§aSet ${cuboid.volume} blocks to $blockName in ${if(time > 5000) "${time / 1000} seconds" else "$time ms"}"
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