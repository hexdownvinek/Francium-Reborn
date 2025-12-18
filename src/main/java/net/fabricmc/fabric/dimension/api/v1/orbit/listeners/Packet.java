package net.fabricmc.fabric.dimension.api.v1.orbit.listeners;

import com.google.gson.JsonObject;

public record Packet(Opcode opcode, JsonObject data) {
}
