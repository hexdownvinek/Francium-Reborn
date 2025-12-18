package net.fabricmc.fabric.dimension.api.v1.gui.components.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Renderable;

@SuppressWarnings("all")
public abstract class AbstractBButton implements Renderable {

    private double x, y;
    private double width, height;

    public AbstractBButton(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

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

    public double getHeight() {
        return height;
    }

    public abstract void render(PoseStack poseStack, int mouseX, int mouseY, float delta);

    public abstract void mouseClicked(double mouseX, double mouseY, int button);

    public boolean isCursorInside(double mouseX, double mouseY) {
        return (x <= mouseX && mouseX <= x + width) && (y <= mouseY && mouseY <= y + height);
    }

}
