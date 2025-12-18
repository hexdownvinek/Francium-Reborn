package net.fabricmc.fabric.dimension.api.v1.gui.components.windows;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.font.FontRenderer;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.RenderableSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.Setting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.ColorSetting;
import net.fabricmc.fabric.dimension.api.v1.util.ColorUtils;

import java.util.Comparator;
import java.util.List;


@SuppressWarnings("all")
public class SettingsWindow extends AbstractWindow {

    private Module module;
    private List<Setting> settings;

    public SettingsWindow(Module module, double x, double y, double width, double height) {
        super(x, y, width, height);
        this.module = module;
        this.settings = module.getSettings();
        double offsetY = getY() + topBarHeight + 30;

        for (Setting setting : settings) {
            if (setting instanceof RenderableSetting renderableSetting) {
                renderableSetting.setX(getX() + 10);
                renderableSetting.setY(offsetY);
                offsetY += (30 + renderableSetting.getHeight());
            }
        }

        settings.sort(Comparator.comparingInt(s -> s instanceof ColorSetting ? 1 : 0));
    }

    public Module getModule() {
        return module;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        super.render(poseStack, mouseX, mouseY, delta);

        FontRenderer.drawCenteredString(poseStack, module.name, (float) ((getX() + (getWidth() / 2))), (float) ((getY() + (topBarHeight / 2))), ColorUtils.getTextColor());

        for (Setting setting : settings) {
            if (setting instanceof RenderableSetting renderableSetting) {
                renderableSetting.render(poseStack, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        for (Setting setting : settings) {
            if (setting instanceof RenderableSetting renderableSetting) {
                renderableSetting.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        for (Setting setting : settings) {
            if (setting instanceof RenderableSetting renderableSetting) {
                renderableSetting.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (Setting setting : settings) {
            if (setting instanceof RenderableSetting renderableSetting) {
                renderableSetting.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            }
        }
    }

    public boolean isCursorInside(double mouseX, double mouseY) {
        return (getX() <= mouseX && mouseX <= getX() + getWidth()) && (getY() <= mouseY && mouseY <= getY() + getWidth());
    }
}
