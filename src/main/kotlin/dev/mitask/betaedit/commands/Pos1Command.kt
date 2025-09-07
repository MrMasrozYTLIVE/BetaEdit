package dev.mitask.betaedit.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.mitask.betaedit.BetaEdit
import dev.mitask.betaedit.data.User
import net.glasslauncher.glassbrigadier.api.argument.coordinate.CoordinateArgumentType
import net.glasslauncher.glassbrigadier.api.argument.coordinate.CoordinateArgumentType.getCoordinate
import net.glasslauncher.glassbrigadier.api.command.CommandProvider
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource
import net.glasslauncher.glassbrigadier.api.predicate.HasPermission.booleanPermission
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder.literal
import net.minecraft.util.math.Vec3i

class Pos1Command : CommandProvider {
    override fun get(): LiteralArgumentBuilder<GlassCommandSource> {
        return literal("/pos1")
            .requires {
                if(it.player == null) {
                    it.sendFeedback("Only players can execute this command!")
                    return@requires false
                }

                return@requires booleanPermission("betaedit.pos").test(it)
            }
            .executes { execute(it) }
            .then(GlassArgumentBuilder.argument("pos", CoordinateArgumentType.intCoordinate()).executes { execute(it) })
    }

    fun execute(context: CommandContext<GlassCommandSource>): Int {
        val player = context.source.player ?: return 1

        var user = BetaEdit.users[player.name]
        if(user == null) {
            user = User()
            BetaEdit.users[player.name] = user
        }

        var pos: Vec3i? = null
        try {
            pos = getCoordinate(context, "pos").getVec3i(context.getSource())
        } catch (_: IllegalArgumentException) {}

        if(pos == null) user.pos1 = Vec3i(player.x.toInt(), player.y.toInt(), player.z.toInt())
        else user.pos1 = pos

        context.source.sendFeedback("§bPos 1 set to [${user.pos1!!.x}, ${user.pos1!!.y}, ${user.pos1!!.z}]")

        return 0
    }
}