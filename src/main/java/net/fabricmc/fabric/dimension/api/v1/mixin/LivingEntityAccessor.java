package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("all")
@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker
    boolean callCheckTotemDeathProtection(DamageSource damageSource);
}
