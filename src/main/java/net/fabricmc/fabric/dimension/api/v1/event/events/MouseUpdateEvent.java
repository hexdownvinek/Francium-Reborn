package net.fabricmc.fabric.dimension.api.v1.event.events;

@SuppressWarnings("all")
public class MouseUpdateEvent {

    private static final MouseUpdateEvent INSTANCE = new MouseUpdateEvent();

    public static MouseUpdateEvent get() {
        return INSTANCE;
    }

}
