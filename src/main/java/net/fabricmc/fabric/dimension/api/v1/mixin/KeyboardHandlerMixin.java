package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.event.events.KeyPressEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
@SuppressWarnings("all")
public class KeyboardHandlerMixin {

    @Shadow private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void keyPress(long windowHandle, int keyCode, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (windowHandle == this.minecraft.getWindow().getWindow()) {
            if (Client.EVENTBUS != null) Client.EVENTBUS.post(KeyPressEvent.get(keyCode, scanCode, action));
        }
    }

}
