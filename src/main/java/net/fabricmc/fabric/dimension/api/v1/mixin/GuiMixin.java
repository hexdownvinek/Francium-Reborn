package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.event.events.HudRenderEvent;
import net.fabricmc.fabric.dimension.api.v1.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
@SuppressWarnings("all")
public class GuiMixin {
    @Inject(method = "render", at = @At(value = "TAIL"))
    public void onHudRender(PoseStack poseStack, float tickDelta, CallbackInfo ci) {
        if (Client.EVENTBUS != null) {
            RenderUtils.unscaledProjection();
            Client.EVENTBUS.post(HudRenderEvent.get(poseStack, tickDelta));
            RenderUtils.scaledProjection();
        }
    }
}
