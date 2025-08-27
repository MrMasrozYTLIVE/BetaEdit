package dev.mitask.betaedit.mixin

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import dev.mitask.betaedit.BetaEdit.Companion.tasks
import dev.mitask.betaedit.util.Cuboid
import net.minecraft.util.math.Vec3i
import net.minecraft.world.LightType
import net.minecraft.world.World
import org.spongepowered.asm.mixin.Mixin

@Mixin(World::class)
@Suppress("NonJavaMixin")
class WorldMixin {
    @WrapMethod(method = ["queueLightUpdate(Lnet/minecraft/world/LightType;IIIIII)V"])
    fun queueLightUpdates(type: LightType?, minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int, original: Operation<Void?>) {
        if (tasks.isEmpty()) {
            original.call(type, minX, minY, minZ, maxX, maxY, maxZ)
            return
        }

        val pos1 = Vec3i(minX, minY, minZ)
        val pos2 = Vec3i(maxX, maxY, maxZ)
        val cuboid = Cuboid(pos1, pos2)
        var shouldUpdate = true

        for (task in tasks) {
            if (cuboid.isInside(task)) {
                shouldUpdate = false
                break
            }
        }

        if (shouldUpdate) original.call(type, minX, minY, minZ, maxX, maxY, maxZ)
    }
}
