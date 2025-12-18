package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.event.events.PacketEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("all")
@Mixin(Connection.class)
public class ConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onPacketSend(Packet<?> packet, CallbackInfo ci) {
        if (Client.EVENTBUS != null) if (Client.EVENTBUS.post(PacketEvent.Send.get(packet)).isCancelled()) ci.cancel();
    }

    @Inject(method = "genericsFtw", at = @At("HEAD"), cancellable = true)
    private static void onPacketReceive(Packet<PacketListener> packet, PacketListener packetListener, CallbackInfo ci) {
        if (Client.EVENTBUS != null) if (Client.EVENTBUS.post(PacketEvent.Receive.get(packet)).isCancelled()) ci.cancel();
    }
}
