package net.fabricmc.fabric.dimension.api.v1.gui.components.windows;

import com.mojang.blaze3d.vertex.PoseStack;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public class DraggableWindow extends AbstractWindow {

    private double dragX, dragY;
    private boolean dragging;

    public DraggableWindow(double x, double y, double width, double height) {
        super(x, y, width, height);
        dragging = false;
        dragX = 0;
        dragY = 0;
    }

    public void setDragX(double dragX) {
        this.dragX = dragX;
    }

    public void setDragY(double dragY) {
        this.dragY = dragY;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        if (dragging) {
            double newX = mouseX - dragX;
            double newY = mouseY - dragY;

            if (newX >= 0 && newX + getWidth() <= mc.getWindow().getWidth())
                this.setX(newX);
            if (newY >= 0  && newY + getHeight() <= mc.getWindow().getHeight())
                this.setY(newY);
        }

        super.render(poseStack, mouseX, mouseY, delta);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isCursorInside(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            dragging = true;
            dragX = mouseX - getX();
            dragY = mouseY - getY();
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1)
            dragging = false;
    }

    public boolean isCursorInside(double mouseX, double mouseY) {
        return (getX() <= mouseX && mouseX <= getX() + getWidth()) && (getY() <= mouseY && mouseY <= getY() + topBarHeight);
    }
}
