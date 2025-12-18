package net.fabricmc.fabric.dimension.api.v1.util;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.module.modules.client.ClickGui;
import net.minecraft.util.Mth;

import java.awt.*;

@SuppressWarnings("all")
public class ColorUtils {
    private static final Color topColor = new Color(20, 20, 20, 255);
    private static final Color backgroundColor = new Color(20, 20, 20, 255);
    private static final Color textColor = new Color(210, 210, 210);

    private static final Color secondColor = new Color(60, 60, 60);

    public static Color getTopColor() {
        return topColor;
    }

    public static Color getBackgroundColor() {
        return backgroundColor;
    }

    public static Color getTextColor() {
        return textColor;
    }

    public static Color getMainColor() {
        ClickGui clickGui = Client.moduleManager().getModule(ClickGui.class);

        return clickGui.clickGuiColor.getValue();
    }

    public static Color getSecondColor() {
        return secondColor;
    }

    public static Color smoothColorTransition(float speed, Color toColor, Color fromColor) {
        return new Color(Mth.lerpInt(speed, fromColor.getRed(), toColor.getRed()),
                         Mth.lerpInt(speed, fromColor.getGreen(), toColor.getGreen()),
                         Mth.lerpInt(speed, fromColor.getBlue(), toColor.getBlue()));
    }

    public static Color smoothAlphaTransition(float speed, int toAlpha, Color fromColor) {
        return new Color(fromColor.getRed(),
                         fromColor.getGreen(),
                         fromColor.getBlue(),
                         Mth.lerpInt(speed, fromColor.getAlpha(), toAlpha));
    }
}
