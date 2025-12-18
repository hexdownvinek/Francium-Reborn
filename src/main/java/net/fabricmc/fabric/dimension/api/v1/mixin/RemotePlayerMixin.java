package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerRemoveEvent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("all")
@Mixin(RemotePlayer.class)
public abstract class RemotePlayerMixin extends AbstractClientPlayer {

    public RemotePlayerMixin() {
        super(null, null);
    }

    @Override
    public void remove(RemovalReason removalReason) {
        if (Client.EVENTBUS != null) Client.EVENTBUS.post(PlayerRemoveEvent.get(removalReason, (RemotePlayer) (Object) this));
        super.remove(removalReason);
    }
}
