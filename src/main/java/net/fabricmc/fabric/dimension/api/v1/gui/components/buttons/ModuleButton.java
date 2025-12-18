package net.fabricmc.fabric.dimension.api.v1.gui.components.buttons;

import net.fabricmc.fabric.dimension.api.v1.font.FontRenderer;
import net.fabricmc.fabric.dimension.api.v1.font.JColor;
import net.fabricmc.fabric.dimension.api.v1.gui.SettingsScreen;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.util.ColorUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.dimension.api.v1.util.RenderUtils;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class ModuleButton extends AbstractBButton {

    private final Module module;
    public Color currentColor;
    public int currentAlpha;

    public ModuleButton(Module module, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.module = module;
        this.currentColor = module.isEnabled() ? ColorUtils.getMainColor() : ColorUtils.getTextColor();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        Color toColor;

        boolean cursorInside = isCursorInside(mouseX, mouseY);

        if (cursorInside) {
            if (module.isEnabled()) {
                toColor = ColorUtils.getMainColor().brighter();
            } else {
                toColor = ColorUtils.getTextColor().brighter();
            }
        } else {
            if (module.isEnabled()) {
                toColor = ColorUtils.getMainColor();
            } else {
                toColor = ColorUtils.getTextColor();
            }
        }

        if (currentColor != toColor) {
            this.currentColor = ColorUtils.smoothColorTransition(0.1f, toColor, currentColor);
        } else if (module.isEnabled() && cursorInside) {
            this.currentColor = ColorUtils.getMainColor();
        }

        int toAlpha = cursorInside ? 50 : 0;

        if (currentAlpha != toAlpha)
            currentAlpha = Mth.lerpInt(0.03f, currentAlpha, toAlpha);

        RenderUtils.R2D.renderRoundedQuad(poseStack, new JColor(ColorUtils.getTextColor()).setAlpha(currentAlpha), getX(), getY(), getX() + getWidth(), getY() + getHeight(), 10, 20);
        FontRenderer.drawCenteredString(poseStack, module.getName(), (float) (getX() + (getWidth() / 2)), (float) (getY() + (getHeight() / 2)), currentColor);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        switch (button) {
            case GLFW.GLFW_MOUSE_BUTTON_1 -> {
                if (isCursorInside(mouseX, mouseY))
                    module.toggle();
            }
            case GLFW.GLFW_MOUSE_BUTTON_2 -> {
                if (isCursorInside(mouseX, mouseY))
                    mc.setScreen(new SettingsScreen(module));
            }
        }
    }
}
