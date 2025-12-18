package net.fabricmc.fabric.dimension.api.v1.module.setting;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.font.FontRenderer;
import net.fabricmc.fabric.dimension.api.v1.util.ColorUtils;
import net.minecraft.client.gui.components.Renderable;

@SuppressWarnings("all")
public abstract class RenderableSetting extends Setting implements Renderable {
    protected double x, y;
    protected double width, height;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        FontRenderer.drawString(poseStack, getName() + ":", (float) x, (float) (y - 20), ColorUtils.getTextColor());
    }

    public abstract void mouseClicked(double mouseX, double mouseY, int button);

    public void mouseReleased(double mouseX, double mouseY, int button) {

    }

    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {}

    public boolean isCursorInside(double mouseX, double mouseY) {
        return (x <= mouseX && mouseX <= x + width) && (y <= mouseY && mouseY <= y + height);
    }

}
