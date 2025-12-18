package net.fabricmc.fabric.dimension.api.v1.gui.components.windows;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.font.FontRenderer;
import net.fabricmc.fabric.dimension.api.v1.font.JColor;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.RenderableSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.ColorSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.util.ColorUtils;
import net.fabricmc.fabric.dimension.api.v1.util.RenderUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class ColorWindow extends DraggableWindow {

    private ColorSetting colorSetting;
    private Module module;
    private List<RenderableSetting> settings = new ArrayList<>();
    private NumberSetting red, green, blue;
    private BooleanSetting rainbow;

    public ColorWindow(ColorSetting colorSetting, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.colorSetting = colorSetting;
        this.module = colorSetting.getParent();

        red = new NumberSetting("Red", module, colorSetting.getValue().getRed(), 0d, 255d, 1d);
        green = new NumberSetting("Green", module, colorSetting.getValue().getGreen(), 0d, 255d, 1d);
        blue = new NumberSetting("Blue", module, colorSetting.getValue().getBlue(), 0d, 255d, 1d);
        rainbow = new BooleanSetting("Rainbow", module, colorSetting.isRainbow());

        red.setWidth(225);
        green.setWidth(225);
        blue.setWidth(225);

        settings.add(red);
        settings.add(green);
        settings.add(blue);
        settings.add(rainbow);

        double topBarOffsetY = getY() + topBarHeight + 30;

        for (RenderableSetting setting : settings) {
            setting.setX(getX() + 5);
            setting.setY(topBarOffsetY);
            topBarOffsetY += 30;
        }
    }

    public ColorSetting getColorSetting() {
        return colorSetting;
    }

    public Module getModule() {
        return module;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        super.render(poseStack, mouseX, mouseY, delta);

        FontRenderer.drawCenteredString(poseStack, colorSetting.name, (float) ((getX() + (getWidth() / 2))), (float) ((getY() + (topBarHeight / 2))), ColorUtils.getTextColor());

        double colorWidth = getWidth() - 10;
        double colorHeight = getWidth() - 10;

        double topBarOffsetY = getY() + topBarHeight + 5;

        double colorX = getX() + 5;

        RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getSecondColor(), colorX, topBarOffsetY, colorX + 2, topBarOffsetY + colorWidth);
        RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getSecondColor(), colorX + colorWidth - 2, topBarOffsetY, colorX + colorWidth, topBarOffsetY + colorHeight);
        RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getSecondColor(), colorX, topBarOffsetY + colorHeight - 2, colorX + colorWidth, topBarOffsetY + colorHeight);
        RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getSecondColor(), colorX, topBarOffsetY, colorX + colorWidth, topBarOffsetY + 2);

        RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getMainColor(), colorX + 2, topBarOffsetY + 2, colorX + colorWidth - 2, topBarOffsetY + colorHeight - 2);

        topBarOffsetY += getWidth() + 20;

        for (RenderableSetting setting : settings) {
            setting.render(poseStack, mouseX, mouseY, delta);
            setting.setX(colorX);
            setting.setY(topBarOffsetY);
            topBarOffsetY += topBarHeight;
        }

        colorSetting.setValue(rainbow.isEnabled(), new JColor(red.getIntValue(), green.getIntValue(), blue.getIntValue()));
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        for (RenderableSetting setting : settings) {
            setting.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);

        for (RenderableSetting setting : settings) {
            setting.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

        for (RenderableSetting setting : settings) {
            setting.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    @Override
    public boolean isCursorInside(double mouseX, double mouseY) {
        return (getX() <= mouseX && mouseX <= getX() + getWidth()) && (getY() <= mouseY && mouseY <= getY() + topBarHeight);
    }
}
