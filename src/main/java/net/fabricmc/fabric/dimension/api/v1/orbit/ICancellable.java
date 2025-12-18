package net.fabricmc.fabric.dimension.api.v1.orbit;

/**
 * Cancellable event need to implement this interface.
 */
@SuppressWarnings("all")
public interface ICancellable {
    /**
     * Sets if this event is cancelled.
     * @param cancelled Is cancelled
     */
    void setCancelled(boolean cancelled);

    /**
     * Cancels this event.
     */
    default void cancel() { setCancelled(true); }

    /**
     * @return True if this event is cancelled.
     */
    boolean isCancelled();
}
