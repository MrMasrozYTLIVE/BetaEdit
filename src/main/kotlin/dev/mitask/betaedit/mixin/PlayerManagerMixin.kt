package dev.mitask.betaedit.mixin

import dev.mitask.betaedit.BetaEdit
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.server.PlayerManager
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(PlayerManager::class)
@Suppress("NonJavaMixin")
class PlayerManagerMixin {
    @Inject(method = ["disconnect(Lnet/minecraft/entity/player/ServerPlayerEntity;)V"], at = [At("HEAD")])
    @Suppress("unused_parameter")
    fun onDisconnect(player: ServerPlayerEntity, ci: CallbackInfo) {
        BetaEdit.users.remove(player.name)
    }
}