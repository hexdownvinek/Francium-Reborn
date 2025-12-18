package net.fabricmc.fabric.dimension.api.v1.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.imixin.IAABB;
import net.fabricmc.fabric.dimension.api.v1.module.modules.combat.Hitboxes;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@SuppressWarnings("all")
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLineBox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/AABB;FFFF)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void onRenderHitbox(PoseStack poses, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo info, AABB aabb) {
        if (Client.INSTANCE != null) {
            Hitboxes hitboxes = Client.moduleManager().getModule(Hitboxes.class);
            if (hitboxes.isEnabled())
                ((IAABB) aabb).expand(hitboxes.getRenderHitboxSize(entity));
        }
    }
}
