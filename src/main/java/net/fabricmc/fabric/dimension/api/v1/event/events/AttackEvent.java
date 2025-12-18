package net.fabricmc.fabric.dimension.api.v1.event.events;

import net.fabricmc.fabric.dimension.api.v1.event.Cancellable;

@SuppressWarnings("all")
public class AttackEvent extends Cancellable {
    private static final AttackEvent INSTANCE = new AttackEvent();

    public static AttackEvent get() {
        INSTANCE.setCancelled(false);
        return INSTANCE;
    }
}
