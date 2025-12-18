package net.fabricmc.fabric.dimension.api.v1.gui;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.gui.components.windows.AbstractWindow;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.gui.components.windows.ModulesWindow;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.ArrayList;

@SuppressWarnings("all")
public class ClickGui {

    public ArrayList<AbstractWindow> windows = new ArrayList<>();

    public ClickGui() {
        int width = 300;
        int offsetX = 10;
        int topBarHeight = 42;

        for (Module.Category category : Module.Category.values()) {
            int height = Client.moduleManager().getModulesByCategory(category).size() * topBarHeight;
            windows.add(new ModulesWindow(category, offsetX, 10, width, height));
            offsetX += width + 20;
        }

        for (AbstractWindow window : windows) {
            window.init();
        }
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        for (AbstractWindow window : windows) {
            window.render(poseStack, mouseX, mouseY, delta);
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        for (AbstractWindow window : windows) {
            window.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        for (AbstractWindow window : windows) {
            window.mouseReleased(mouseX, mouseY, button);
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double amount) {
        for (AbstractWindow window : windows) {
            window.mouseScrolled(mouseX, mouseY, amount);
        }
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        for (AbstractWindow window : windows) {
            window.keyPressed(keyCode, scanCode, modifiers);
        }
    }

}
