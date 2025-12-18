package net.fabricmc.fabric.dimension.api.v1.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.gui.components.windows.SettingsWindow;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.util.RenderUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class SettingsScreen extends Screen {

    private final Module module;
    private SettingsWindow settingsWindow;
    private final double width = 500;
    private final double height = 800;

    public SettingsScreen(Module module) {
        super(Component.empty());
        this.module = module;

        double centeredX = mc.getWindow().getWidth() / 2;
        double centeredY = mc.getWindow().getHeight() / 2;

        double halfWidth = width / 2;
        double halfHeight = height / 2;
        settingsWindow = new SettingsWindow(module, centeredX - halfWidth, centeredY - halfHeight, width, height);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        RenderUtils.unscaledProjection();

        double guiScale = Client.mc.getWindow().getGuiScale();
        mouseX *= guiScale;
        mouseY *= guiScale;

        super.render(poseStack, mouseX, mouseY, delta);
        settingsWindow.render(poseStack, mouseX, mouseY, delta);

        RenderUtils.scaledProjection();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double guiScale = Client.mc.getWindow().getGuiScale();
        mouseX *= guiScale;
        mouseY *= guiScale;

        settingsWindow.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        double guiScale = Client.mc.getWindow().getGuiScale();
        mouseX *= guiScale;
        mouseY *= guiScale;

        settingsWindow.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        double guiScale = Client.mc.getWindow().getGuiScale();
        mouseX *= guiScale;
        mouseY *= guiScale;

        settingsWindow.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void onClose() {
        mc.setScreen(new ClickGuiScreen());
    }
}
