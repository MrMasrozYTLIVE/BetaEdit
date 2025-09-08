package dev.mitask.betaedit.mixin;

import dev.mitask.betaedit.BetaEdit;
import dev.mitask.betaedit.data.User;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "onBlockBreakStart", at = @At("HEAD"))
    private void betaedit$onBlockBreakStart(World world, int x, int y, int z, PlayerEntity player, CallbackInfo cir) {
        User user = BetaEdit.Companion.getUsers().get(player.name);

        if(user == null) {
            user = new User();
            BetaEdit.Companion.getUsers().put(player.name, user);
        }

        ItemStack item = player.getHand();
        if(item == null) return;
        if(!item.getStationNbt().getBoolean(BetaEdit.Companion.getWandIdentifier().toString())) return;

        user.setPos1(new Vec3i(x, y, z));
        player.sendMessage("§bPos 1 set to [" + x + ", " + y + ", " + z + "]");
    }
}
