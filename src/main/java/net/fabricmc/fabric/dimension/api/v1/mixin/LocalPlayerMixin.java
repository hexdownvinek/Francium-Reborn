package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.minecraft.client.player.LocalPlayer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
@SuppressWarnings("all")
public class LocalPlayerMixin {

    @Inject(method = "tick()V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/player/AbstractClientPlayer;tick()V",
                    ordinal = 0,
                    opcode = Opcodes.INVOKESPECIAL))
    private void onTick(CallbackInfo ci) {
        if (Client.EVENTBUS != null) Client.EVENTBUS.post(PlayerTickEvent.get());
    }

}
