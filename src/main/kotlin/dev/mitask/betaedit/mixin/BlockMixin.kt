package dev.mitask.betaedit.mixin

import dev.mitask.betaedit.BetaEdit
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
    @Suppress("unused_parameter")
    @Inject(method = ["onBlockBreakStart(Lnet/minecraft/world/World;IIILnet/minecraft/entity/player/PlayerEntity;)V"], at = [At("HEAD")])
    fun onBlockBreakStart(world: World, x: Int, y: Int, z: Int, player: PlayerEntity, cir: CallbackInfo) {
        var user = BetaEdit.users[player.name]

        if(user == null) {
            user = User()
            BetaEdit.users[player.name] = user
        }

        val isWand = player.hand?.stationNbt?.getBoolean("wand") ?: false
        if(!isWand) return

        user.pos1 = Vec3i(x, y, z)
        player.sendMessage("§bPos 1 set to [$x, $y, $z]")
    }
}
