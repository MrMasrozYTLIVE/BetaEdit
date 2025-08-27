package dev.mitask.betaedit.mixin

import dev.mitask.betaedit.BetaEdit
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
class ItemMixin {
    @Suppress("unused_parameter")
    @Inject(method = ["useOnBlock(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;IIII)Z"], at = [At("HEAD")])
    fun useOnBlock(stack: ItemStack, player: PlayerEntity, world: World, x: Int, y: Int, z: Int, side: Int, cir: CallbackInfoReturnable<Boolean>) {
        var user = BetaEdit.users[player.name]

        if(user == null) {
            user = User()
            BetaEdit.users[player.name] = user
        }

        if(!stack.stationNbt.getBoolean("wand")) return

        user.pos2 = Vec3i(x, y, z)
        player.sendMessage("§bPos 2 set to [$x, $y, $z]")
    }
}
