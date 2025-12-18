package net.fabricmc.fabric.dimension.api.v1.gui.components.windows;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.font.FontRenderer;
import net.fabricmc.fabric.dimension.api.v1.gui.components.buttons.AbstractBButton;
import net.fabricmc.fabric.dimension.api.v1.gui.components.buttons.ModuleButton;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.util.ColorUtils;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.ArrayList;

@SuppressWarnings("all")
public class ModulesWindow extends DraggableWindow {

    private ArrayList<AbstractBButton> buttons = new ArrayList<>();
    private Module.Category category;

    public ModulesWindow(Module.Category category, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.category = category;

        double topBarOffsetY = getY() + topBarHeight;

        for (Module module : client.moduleManager.getModulesByCategory(category)) {
            buttons.add(new ModuleButton(module, getX(), topBarOffsetY, getWidth(), topBarHeight));
            topBarOffsetY += topBarHeight;
        }
    }

    public Module.Category getCategory() {
        return category;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        super.render(poseStack, mouseX, mouseY, delta);

        double topBarOffsetY = getY() + topBarHeight;

        FontRenderer.drawCenteredString(poseStack, category.name, (float) ((getX() + (getWidth() / 2))), (float) ((getY() + (topBarHeight / 2))), ColorUtils.getTextColor());

        for (AbstractBButton button : buttons) {
            button.render(poseStack, mouseX, mouseY, delta);
            button.setX(getX());
            button.setY(topBarOffsetY);
            topBarOffsetY += topBarHeight;
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        for (AbstractBButton button1 : buttons) {
            button1.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean isCursorInside(double mouseX, double mouseY) {
        return (getX() <= mouseX && mouseX <= getX() + getWidth()) && (getY() <= mouseY && mouseY <= getY() + topBarHeight);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
    }
}
