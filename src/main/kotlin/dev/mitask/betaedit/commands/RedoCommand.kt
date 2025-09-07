package dev.mitask.betaedit.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.mitask.betaedit.BetaEdit
import dev.mitask.betaedit.util.Cuboid
import net.glasslauncher.glassbrigadier.api.command.CommandProvider
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource
import net.glasslauncher.glassbrigadier.api.predicate.HasPermission.booleanPermission
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder.literal
import net.minecraft.world.LightType
import kotlin.system.measureTimeMillis

class RedoCommand : CommandProvider {
    override fun get(): LiteralArgumentBuilder<GlassCommandSource> {
        return literal("/redo")
            .requires {
                if(it.player == null) {
                    it.sendFeedback("Only players can execute this command!")
                    return@requires false
                }

                return@requires booleanPermission("betaedit.redo").test(it)
            }
            .executes { redo(it) }
    }

    fun redo(context: CommandContext<GlassCommandSource>): Int {
        val user = BetaEdit.users[context.source.player?.name ?: ""]
        if(user == null) {
            context.source.sendFeedback("§cSomething went wrong! (User == null)")
            return 1
        }

        val world = (context.getSource())?.world!!
        val cuboid = Cuboid(user.pos1!!, user.pos2!!)
        val history = user.popUndoHistory()

        if(history == null) {
            context.source.sendFeedback("§cNo history found")
            return 1
        }

        BetaEdit.tasks.add(cuboid)
        val time = measureTimeMillis {
            val meta = history.changedToMeta

            if(meta == null) {
                history.blocks.forEach {
                    world.setBlock(it.pos.x, it.pos.y, it.pos.z, history.changedToId)
                }
            } else {
                history.blocks.forEach {
                    world.setBlock(it.pos.x, it.pos.y, it.pos.z, history.changedToId, meta)
                }
            }
        }
        BetaEdit.tasks.remove(cuboid)

        sendFeedbackAndLog(
            context.getSource(),
            "§aRedid ${cuboid.volume} blocks in ${if(time > 5000) "${time / 1000} seconds" else "$time ms"}"
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