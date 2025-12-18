package net.fabricmc.fabric.dimension.api.v1.event.events;

import net.fabricmc.fabric.dimension.api.v1.event.Cancellable;

@SuppressWarnings("all")
public class ItemUseEvent extends Cancellable {
    private static final ItemUseEvent INSTANCE = new ItemUseEvent();

    public static ItemUseEvent get() {
        INSTANCE.setCancelled(false);
        return INSTANCE;
    }
}
