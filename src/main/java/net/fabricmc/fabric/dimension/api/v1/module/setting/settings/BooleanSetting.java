package net.fabricmc.fabric.dimension.api.v1.module.setting.settings;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.dimension.api.v1.font.JColor;
import net.fabricmc.fabric.dimension.api.v1.module.setting.RenderableSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.Setting;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.util.ColorUtils;
import net.fabricmc.fabric.dimension.api.v1.util.RenderUtils;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

@SuppressWarnings("all")
public class BooleanSetting extends RenderableSetting {
	public boolean enabled;
	public Color currentColor;

	public BooleanSetting(String name, Module parent, boolean enabled) {
		this.enabled = enabled;
		this.name = name;
		this.parent = parent;
		this.width = 30;
		this.height = 30;
	}
	  
	public boolean isEnabled() {
	    return this.enabled;
	}
	  
	public void setEnabled(boolean enabled) {
	    this.enabled = enabled;
	}
	
	public void toggle() {
	    this.enabled = !this.enabled;
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
		super.render(poseStack, mouseX, mouseY, delta);

		RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getSecondColor(), getX(), getY(), getX() + 2, getY() + getHeight());
		RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getSecondColor(), getX() + getWidth() - 2, getY(), getX() + getWidth(), getY() + getHeight());
		RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getSecondColor(), getX(), getY() + getHeight() - 2, getX() + getWidth(), getY() + getHeight());
		RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getSecondColor(), getX(), getY(), getX() + getWidth(), getY() + 2);

		if (currentColor == null) {
			this.currentColor = new JColor(ColorUtils.getMainColor()).setAlpha(isEnabled() ? ColorUtils.getMainColor().getAlpha() : 0);
		} else {
			this.currentColor = new JColor(ColorUtils.getMainColor()).setAlpha(this.currentColor.getAlpha());
		}

		int toAlpha = isEnabled() ? ColorUtils.getMainColor().getAlpha() : 0;

		if (currentColor.getAlpha() != toAlpha)
			currentColor = ColorUtils.smoothAlphaTransition(0.05f, toAlpha, currentColor);

		RenderUtils.R2D.renderQuad(poseStack, currentColor, getX() + 5, getY() + 5, getX() + getWidth() - 5, getY() + getHeight() - 5);
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (isCursorInside(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_1)
			toggle();
	}
}
