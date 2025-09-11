package dev.mitask.betaedit.mixin

import dev.mitask.betaedit.BetaEdit.Companion.wandIdentifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.modificationstation.stationapi.api.block.AbstractBlockState
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(value = [AbstractBlockState::class])
@Suppress("NonJavaMixin")
class AbstractBlockStateMixin {
    @Inject(method = ["calcBlockBreakingDelta"], at = [At("HEAD")], cancellable = true, order = 900)
    private fun calcBlockBreakingDelta(player: PlayerEntity, world: BlockView?, pos: BlockPos?, ci: CallbackInfoReturnable<Float?>) {
        val item = player.hand ?: return
        if (!item.stationNbt.getBoolean(wandIdentifier.toString())) return

        ci.setReturnValue(0.0f)
    }
}