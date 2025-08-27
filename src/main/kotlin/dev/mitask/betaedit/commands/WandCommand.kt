package dev.mitask.betaedit.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.glasslauncher.glassbrigadier.api.command.CommandProvider
import net.glasslauncher.glassbrigadier.api.command.GlassCommandSource
import net.glasslauncher.glassbrigadier.api.predicate.HasPermission.booleanPermission
import net.glasslauncher.glassbrigadier.impl.argument.GlassArgumentBuilder.literal
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class WandCommand : CommandProvider {
    override fun get(): LiteralArgumentBuilder<GlassCommandSource?>? {
        return literal("/wand")
            .requires {
                if(it.player == null) {
                    it.sendFeedback("Only players can execute this command!")
                    return@requires false
                }

                return@requires booleanPermission("betaedit.wand").test(it)
            }
            .executes { context ->
                val source = context.source

                val item = ItemStack(Item.WOODEN_AXE)
                item.stationNbt.putBoolean("wand", true)

                val success = source.player?.inventory?.addStack(item) ?: return@executes 1
                source.sendFeedback(if(success) "§aSuccessfully gave wand" else "§cCould not give wand to the player")

                return@executes 0
            }
    }
}