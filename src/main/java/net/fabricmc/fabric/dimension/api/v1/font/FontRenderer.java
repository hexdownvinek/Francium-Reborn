package net.fabricmc.fabric.dimension.api.v1.font;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.module.modules.client.ClickGui;

import java.awt.*;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;
import static net.fabricmc.fabric.dimension.api.v1.font.IFont.*;

@SuppressWarnings("all")
public class FontRenderer {

    private static float vanillaFontSize = 2.0f;
    private static float COMFORTAA_Yoffset = 1.5f;
    private static ClickGui getClickGui() {
        return Client.moduleManager().getModule(ClickGui.class);
    }

    private static String getFontMode() {
        return getClickGui().fontMode.getMode();
    }

    private static boolean isShadow() {
        return getClickGui().fontShadow.isEnabled();
    }

    public static double getFontWidth(String text) {
        String fontMode = getFontMode();

        switch (fontMode) {
            case "Josefin Sans" -> {
                return JOSEFIN_SANS.getStringWidth(text);
            }
            case "Comfortaaa" -> {
                return COMFORTAA.getStringWidth(text);
            }
            default -> {
                return mc.font.width(text) * vanillaFontSize;
            }
        }
    }

    public static void drawString(PoseStack poses, String text, float x, float y, Color color) {
        String fontMode = getFontMode();
        boolean shadow = isShadow();

        switch (fontMode) {
            case "Josefin Sans" -> {
                if (shadow) {
                    JOSEFIN_SANS.drawStringWithShadow(poses, text, x, y, color.getRGB());
                } else {
                    JOSEFIN_SANS.drawString(poses, text, x, y, color.getRGB());
                }
            }
            case "Comfortaaa" -> {
                if (shadow) {
                    COMFORTAA.drawStringWithShadow(poses, text, x, y - COMFORTAA_Yoffset, color.getRGB());
                } else {
                    COMFORTAA.drawString(poses, text, x, y - COMFORTAA_Yoffset, color.getRGB());
                }
            }
            default -> {
                drawDefault(poses, text, x, y, shadow, color);
            }
        }
    }

    public static void drawCenteredString(PoseStack poses, String text, float centeredX, float centeredY, Color color) {
        String fontMode = getFontMode();
        boolean shadow = isShadow();

        switch (fontMode) {
            case "Josefin Sans" -> {
                if (shadow) {
                    JOSEFIN_SANS.drawCenteredStringWithShadow(poses, text, centeredX, centeredY, color.getRGB());
                } else {
                    JOSEFIN_SANS.drawCenteredString(poses, text, centeredX, centeredY, color.getRGB());
                }
            }
            case "Comfortaaa" -> {
                if (shadow) {
                    COMFORTAA.drawCenteredStringWithShadow(poses, text, centeredX, centeredY - COMFORTAA_Yoffset, color.getRGB());
                } else {
                    COMFORTAA.drawCenteredString(poses, text, centeredX, centeredY - COMFORTAA_Yoffset, color.getRGB());
                }
            }
            default -> {
                drawCenteredDefault(poses, text, centeredX, centeredY, shadow, color);
            }
        }
    }

    public static void drawDefault(PoseStack poses, String text, float x, float y, boolean shadow, Color color) {
        poses.pushPose();

        poses.scale(vanillaFontSize, vanillaFontSize, vanillaFontSize);
        if (shadow) {
            mc.font.drawShadow(poses, text, x / vanillaFontSize, y / vanillaFontSize, color.getRGB());
        } else {
            mc.font.draw(poses, text, x / vanillaFontSize, y / vanillaFontSize, color.getRGB());
        }
        poses.scale(1, 1, 1);

        poses.popPose();
    }

    public static void drawCenteredDefault(PoseStack poses, String text, float centeredX, float centeredY, boolean shadow, Color color) {
        poses.pushPose();

        poses.scale(vanillaFontSize, vanillaFontSize, vanillaFontSize);
        drawCenteredDefaultString(poses, text, centeredX / vanillaFontSize, centeredY / vanillaFontSize, shadow, color.getRGB());
        poses.scale(1, 1, 1);

        poses.popPose();
    }

    public static void drawCenteredDefaultString(PoseStack poseStack, String string, float centeredX, float centeredY, boolean shadow, int color) {
        int stringWidth = mc.font.width(string);
        if (shadow) {
            mc.font.drawShadow(poseStack, string, centeredX - (stringWidth / 2), centeredY - (mc.font.lineHeight / 2), color);
        } else {
            mc.font.draw(poseStack, string, centeredX - (stringWidth / 2), centeredY - (mc.font.lineHeight / 2), color);
        }
    }

}
