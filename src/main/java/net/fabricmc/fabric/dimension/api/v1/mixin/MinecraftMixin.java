package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.event.events.AttackEvent;
import net.fabricmc.fabric.dimension.api.v1.event.events.ItemUseEvent;
import net.fabricmc.fabric.dimension.api.v1.event.events.TickEvent;
import net.fabricmc.fabric.dimension.api.v1.util.MouseSimulation;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("all")
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onPreTick(CallbackInfo ci) {
        if (Client.EVENTBUS != null) Client.EVENTBUS.post(TickEvent.Pre.get());
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onPostTick(CallbackInfo ci) {
        if (Client.EVENTBUS != null) Client.EVENTBUS.post(TickEvent.Post.get());
    }

    @Inject(at = @At("HEAD"), method = "startUseItem", cancellable = true)
    private void onItemUse(CallbackInfo ci) {
        if (Client.EVENTBUS != null)
            if (Client.EVENTBUS.post(new ItemUseEvent()).isCancelled()) ci.cancel();

        if (MouseSimulation.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_2)) {
            MouseSimulation.mouseButtons.put(GLFW.GLFW_MOUSE_BUTTON_2, false);
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "startAttack", cancellable = true)
    private void onAttack(CallbackInfoReturnable<Boolean> cir) {
        if (Client.EVENTBUS != null)
            if (Client.EVENTBUS.post(new AttackEvent()).isCancelled()) cir.setReturnValue(false);

        if (MouseSimulation.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
            MouseSimulation.mouseButtons.put(GLFW.GLFW_MOUSE_BUTTON_1, false);
            cir.setReturnValue(false);
        }
    }


}
