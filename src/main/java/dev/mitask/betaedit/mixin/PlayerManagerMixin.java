package dev.mitask.betaedit.mixin;

import dev.mitask.betaedit.BetaEdit;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
class PlayerManagerMixin {
    @Inject(method = "disconnect(Lnet/minecraft/entity/player/ServerPlayerEntity;)V", at = @At("HEAD"))
    private void betaedit$onDisconnect(ServerPlayerEntity player, CallbackInfo ci) {
        BetaEdit.Companion.getUsers().remove(player.name);
    }
}