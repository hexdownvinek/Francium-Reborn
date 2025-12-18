package net.fabricmc.fabric.dimension.api.v1.event.events;

import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;

@SuppressWarnings("all")
public class PlayerRemoveEvent {

    private static final PlayerRemoveEvent INSTANCE = new PlayerRemoveEvent();

    public Entity.RemovalReason removalReason;
    public RemotePlayer player;

    public static PlayerRemoveEvent get(Entity.RemovalReason removalReason, RemotePlayer player) {
        INSTANCE.removalReason = removalReason;
        INSTANCE.player = player;
        return INSTANCE;
    }

}
