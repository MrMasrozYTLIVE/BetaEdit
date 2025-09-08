package dev.mitask.betaedit.mixin;

import dev.mitask.betaedit.BetaEdit;
import dev.mitask.betaedit.data.User;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
class ItemMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"))
    private void betaedit$useOnBlock(ItemStack stack, PlayerEntity player, World world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir) {
        User user = BetaEdit.Companion.getUsers().get(player.name);

        if(user == null) {
            user = new User();
            BetaEdit.Companion.getUsers().put(player.name, user);
        }

        if(!stack.getStationNbt().getBoolean(BetaEdit.Companion.getWandIdentifier().toString())) return;

        user.setPos2(new Vec3i(x, y, z));
        player.sendMessage("§bPos 2 set to [" + x + ", " + y + ", " + z + "]");
    }
}
