package dev.mitask.betaedit.mixin

import dev.mitask.betaedit.BetaEdit.Companion.users
import dev.mitask.betaedit.BetaEdit.Companion.wandIdentifier
import dev.mitask.betaedit.data.User
import net.minecraft.block.Block
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(Block::class)
@Suppress("NonJavaMixin")
class BlockMixin {
    @Inject(method = ["onBlockBreakStart"], at = [At("HEAD")])
    private fun onBlockBreakStart(world: World?, x: Int, y: Int, z: Int, player: PlayerEntity, cir: CallbackInfo?) {
        var user = users[player.name]

        if (user == null) {
            user = User()
            users[player.name] = user
        }

        val item = player.hand ?: return
        if (!item.stationNbt.getBoolean(wandIdentifier.toString())) return

        user.pos1 = Vec3i(x, y, z)
        player.sendMessage("§bPos 1 set to [$x, $y, $z]")
    }
}
