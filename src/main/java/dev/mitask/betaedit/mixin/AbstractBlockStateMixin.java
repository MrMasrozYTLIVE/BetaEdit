package dev.mitask.betaedit.mixin;

import dev.mitask.betaedit.BetaEdit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.block.AbstractBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractBlockState.class)
public class AbstractBlockStateMixin {
    @Inject(method = "calcBlockBreakingDelta", at = @At("HEAD"), cancellable = true, order = 900)
    private void calcBlockBreakingDelta(PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> ci) {
        ItemStack item = player.getHand();

        if(item == null) return;
        if(!item.getStationNbt().getBoolean(BetaEdit.Companion.getWandIdentifier().toString())) return;

        ci.setReturnValue(0.0F);
    }
}