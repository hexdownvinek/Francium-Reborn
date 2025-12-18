package net.fabricmc.fabric.dimension.api.v1.util;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.dimension.api.v1.orbit.listeners.DiscordIPC;
import net.fabricmc.fabric.dimension.api.v1.orbit.listeners.RichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;

import java.time.Instant;

@SuppressWarnings("all")
public class GlowstoneUtils {

    public static void startFranciumRPC() {
        if (!DiscordIPC.start(1105221218199678977L)) {
            System.out.println("NULL");
            return;
        }

        RichPresence presence = new RichPresence();
        presence.setDetails("Playing Francium Reborn");
        presence.setLargeImage("francium", "WIT DA FELLAS");
        presence.setSmallImage(getPlayerHeadImage(), getPlayerIGN());
        presence.setStart(Instant.now().getEpochSecond());
        DiscordIPC.setActivity(presence);
    }

    public static void startLunarRPC() {
        if (!DiscordIPC.start(1127276267641770014L)) {
            System.out.println("NULL");
            return;
        }

        RichPresence presence = new RichPresence();
        presence.setDetails("Playing Minecraft 1.19.4");
        presence.setLargeImage("client", "Lunar Client");
        presence.setStart(Instant.now().getEpochSecond());
        DiscordIPC.setActivity(presence);
    }

    public static String getPlayerHeadImage() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player != null) {
            GameProfile gameProfile = mc.player.getGameProfile();
            String uuid = gameProfile.getId().toString();
            return "https://crafthead.net/avatar/" + uuid;
        }
        return null;
    }

    private static String getPlayerIGN() {
        Minecraft mc = Minecraft.getInstance();
        AbstractClientPlayer player = mc.player;
        if (player != null) {
            GameProfile gameProfile = player.getGameProfile();
            return gameProfile.getName();
        }
        return null;
    }

    public static void stopRPC() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DiscordIPC.stop();
    }
}