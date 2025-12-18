package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.module.modules.combat.Hitboxes;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("all")
@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "getPickRadius", at = @At("HEAD"), cancellable = true)
    private void onGetPickRadius(CallbackInfoReturnable<Float> cir) {
        if (Client.INSTANCE != null) {
            Hitboxes hitboxes = Client.moduleManager().getModule(Hitboxes.class);
            if (hitboxes.isEnabled())
                cir.setReturnValue(hitboxes.getHitboxSize((Entity) (Object) this));
        }
    }
}
