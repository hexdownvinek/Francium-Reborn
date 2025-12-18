package net.fabricmc.fabric.dimension.api.v1.module.modules.client;

import net.fabricmc.fabric.dimension.api.v1.font.JColor;
import net.fabricmc.fabric.dimension.api.v1.gui.ClickGuiScreen;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.ColorSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.ModeSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.util.ColorUtils;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("all")
public class ClickGui extends Module {
    public final ColorSetting clickGuiColor = new ColorSetting("Color", this, new JColor(37, 150, 190));
    public final ModeSetting fontMode = new ModeSetting("Font", this, "Comfortaaa", "Default", "Josefin Sans", "Comfortaaa");
    public final BooleanSetting fontShadow = new BooleanSetting("Font Shadow", this, true);

    public static Screen clickGuiScreen;

    public ClickGui() {
        super("Click Gui", "click gui", GLFW.GLFW_KEY_DELETE, Category.CLIENT);
        addSettings(clickGuiColor, fontMode, fontShadow);
    }

    @Override
    public void onEnable() {
        clickGuiScreen = new ClickGuiScreen();
        mc.setScreen(clickGuiScreen);
    }

    @Override
    public void onDisable() {
        clickGuiScreen.onClose();
    }
}
