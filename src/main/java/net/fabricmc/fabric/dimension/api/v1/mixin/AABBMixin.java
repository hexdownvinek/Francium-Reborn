package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.fabricmc.fabric.dimension.api.v1.imixin.IAABB;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("all")
@Mixin(AABB.class)
public abstract class AABBMixin implements IAABB {
    @Mutable @Shadow @Final public double maxX;
    @Mutable @Shadow @Final public double minX;
    @Mutable @Shadow @Final public double minY;
    @Mutable @Shadow @Final public double minZ;
    @Mutable @Shadow @Final public double maxY;
    @Mutable @Shadow @Final public double maxZ;

    @Override
    public void expand(double n) {
        this.minX -= n;
        this.minY -= n;
        this.minZ -= n;
        this.maxX += n;
        this.maxY += n;
        this.maxZ += n;
    }
}
