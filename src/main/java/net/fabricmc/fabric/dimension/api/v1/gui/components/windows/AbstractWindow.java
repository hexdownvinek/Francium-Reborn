package net.fabricmc.fabric.dimension.api.v1.gui.components.windows;

import net.fabricmc.fabric.dimension.api.v1.Client;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.dimension.api.v1.util.ColorUtils;
import net.fabricmc.fabric.dimension.api.v1.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;

@SuppressWarnings("all")
public abstract class AbstractWindow implements Renderable {

    protected static Minecraft mc;
    protected static Client client;

    private double x, y;
    private double width, height;
    protected final double topBarHeight = 42;


    public AbstractWindow(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        mc = Client.mc;
        client = Client.INSTANCE;
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

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void init() {}

    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        double topBarOffsetY = getY() + topBarHeight;

        // Render background of the window
        RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getBackgroundColor(), getX(), topBarOffsetY, getX() + getWidth(), topBarOffsetY + getHeight());

        // Render top bar
        RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getTopColor(), getX(), getY(), getX() + getWidth(), topBarOffsetY);

        // Render main content area
        RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getMainColor(), getX(), getY() + topBarHeight - 2, getX() + getWidth(), getY() + topBarHeight);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {

    }

    public void mouseReleased(double mouseX, double mouseY, int button) {

    }

    public void mouseScrolled(double mouseX, double mouseY, double amount) {}

    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {}

    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

}
