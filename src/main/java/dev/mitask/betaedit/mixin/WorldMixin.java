package dev.mitask.betaedit.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.mitask.betaedit.BetaEdit;
import dev.mitask.betaedit.util.Cuboid;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
class WorldMixin {
    @WrapMethod(method = "queueLightUpdate(Lnet/minecraft/world/LightType;IIIIII)V")
    private void queueLightUpdates(LightType type, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Operation<Void> original) {
        if (BetaEdit.Companion.getTasks().isEmpty()) {
            original.call(type, minX, minY, minZ, maxX, maxY, maxZ);
            return;
        }

        Vec3i pos1 = new Vec3i(minX, minY, minZ);
        Vec3i pos2 = new Vec3i(maxX, maxY, maxZ);
        Cuboid cuboid = new Cuboid(pos1, pos2);
        var shouldUpdate = true;

        for (Cuboid task : BetaEdit.Companion.getTasks()) {
            if (cuboid.isInside(task)) {
                shouldUpdate = false;
                break;
            }
        }

        if (shouldUpdate) original.call(type, minX, minY, minZ, maxX, maxY, maxZ);
    }
}
