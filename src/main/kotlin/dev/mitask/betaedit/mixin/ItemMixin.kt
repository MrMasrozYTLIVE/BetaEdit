package dev.mitask.betaedit.mixin

import dev.mitask.betaedit.BetaEdit.Companion.users
import dev.mitask.betaedit.BetaEdit.Companion.wandIdentifier
import dev.mitask.betaedit.data.User
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(Item::class)
@Suppress("NonJavaMixin")
internal class ItemMixin {
    @Inject(method = ["useOnBlock"], at = [At("HEAD")])
    private fun useOnBlock(stack: ItemStack, player: PlayerEntity, world: World?, x: Int, y: Int, z: Int, side: Int, cir: CallbackInfoReturnable<Boolean?>?) {
        var user = users[player.name]

        if (user == null) {
            user = User()
            users[player.name] = user
        }

        val item = player.hand ?: return
        if (!item.stationNbt.getBoolean(wandIdentifier.toString())) return

        user.pos2 = Vec3i(x, y, z)
        player.sendMessage("§bPos 2 set to [$x, $y, $z]")
    }
}
