package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.event.events.MouseMoveEvent;
import net.fabricmc.fabric.dimension.api.v1.event.events.MouseUpdateEvent;
import net.fabricmc.fabric.dimension.api.v1.event.events.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@Mixin(MouseHandler.class)
@SuppressWarnings("all")
public class MouseHandlerMixin {
    @Shadow private Minecraft minecraft;

    @Inject(method = "onMove", at = @At("RETURN"))
    private void onMouseMove(long windowHandle, double mouseX, double mouseY, CallbackInfo callbackInfo) {
        if (windowHandle == this.minecraft.getWindow().getWindow()) {
            if (Client.EVENTBUS != null)
                Client.EVENTBUS.post(MouseMoveEvent.get(mouseX, mouseY));
        }
    }

    @Inject(method = "turnPlayer", at = @At("RETURN"))
    private void onMouseUpdate(CallbackInfo callbackInfo) {
        if (Client.EVENTBUS != null)
            Client.EVENTBUS.post(MouseUpdateEvent.get());
    }

}
