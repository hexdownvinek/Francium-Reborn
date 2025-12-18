package net.fabricmc.fabric.dimension.api.v1.orbit.listeners;

public enum Opcode {
    Handshake,
    Frame,
    Close,
    Ping,
    Pong;

    private static final Opcode[] VALUES = values();

    public static Opcode valueOf(int i) {
        return VALUES[i];
    }
}
