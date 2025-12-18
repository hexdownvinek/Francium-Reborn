package net.fabricmc.fabric.dimension.api.v1.gui;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.module.modules.client.ClickGui;
import net.fabricmc.fabric.dimension.api.v1.util.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@SuppressWarnings("all")
public class ClickGuiScreen extends Screen {

    public ClickGuiScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        RenderUtils.unscaledProjection();

        double guiScale = Client.mc.getWindow().getGuiScale();
        mouseX *= guiScale;
        mouseY *= guiScale;

        super.render(poseStack, mouseX, mouseY, delta);
        Client.clickGui().render(poseStack, mouseX, mouseY, delta);

        RenderUtils.scaledProjection();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double guiScale = Client.mc.getWindow().getGuiScale();
        mouseX *= guiScale;
        mouseY *= guiScale;

        Client.clickGui().mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        double guiScale = Client.mc.getWindow().getGuiScale();
        mouseX *= guiScale;
        mouseY *= guiScale;

        Client.clickGui().mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        double guiScale = Client.mc.getWindow().getGuiScale();
        mouseX *= guiScale;
        mouseY *= guiScale;

        Client.clickGui().mouseScrolled(mouseX, mouseY, amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Client.clickGui().keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        Client.moduleManager().getModule(ClickGui.class).setEnabled(false);
        super.onClose();
    }
}
