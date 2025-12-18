package net.fabricmc.fabric.dimension.api.v1.module.setting.settings;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.font.FontRenderer;
import net.fabricmc.fabric.dimension.api.v1.module.setting.RenderableSetting;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.util.ColorUtils;
import net.fabricmc.fabric.dimension.api.v1.util.MathUtils;
import net.fabricmc.fabric.dimension.api.v1.util.RenderUtils;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public class NumberSetting extends RenderableSetting {
	public double value;
	public double minimum;
	public double maximum;
	public double increment;
	public boolean sliding;
	  
	public NumberSetting(String name, Module parent, double value, double minimum, double maximum, double increment) {
		this.name = name;
	    this.parent = parent;
	    this.value = value;
	    this.minimum = minimum;
	    this.maximum = maximum;
	    this.increment = increment;
		this.sliding = false;

		this.width = 200;
		this.height = 20;
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
		super.render(poseStack, mouseX, mouseY, delta);

		RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getSecondColor(), getX(), getY() + (height / 2), getX() + getWidth(), getY() + (height / 2) + 2);

		double offsetX = (getValue() - getMinimum()) / (getMaximum() - getMinimum()) * getWidth();
		RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getMainColor(), getX() + offsetX, getY(), getX() + offsetX + 2, getY() + getHeight());

		FontRenderer.drawString(poseStack, String.valueOf(getValue()), (float) (x + getWidth() + 10), (float) (getY() + (height / 2)), ColorUtils.getTextColor());
	}

	private void slide(double mouseX) {
		double translated = mouseX - getX();
		double perIn = Mth.clamp(translated / (width), 0, 1);
		setValue(MathUtils.roundToDecimal(perIn * (getMaximum() - getMinimum()) + getMinimum(), getIncrement()));
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (isCursorInside(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_1) {
			sliding = true;
			slide(mouseX);
		}
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		if (sliding && button == GLFW.GLFW_MOUSE_BUTTON_1) {
			sliding = false;
		}
	}

	@Override
	public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (isCursorInside(mouseX, mouseY) && sliding) {
			slide(mouseX);
		}
	}

	public double getValue() {
	    return this.value;
	}

	public int getIntValue() {
		return (int) this.value;
	}

	public float getFloatValue() {
		return (float) this.value;
	}
	  
	public void setValue(double value) {
	    double precision = 1.0D / this.increment;
	    this.value = Math.round(Math.max(this.minimum, Math.min(this.maximum, value)) * precision) / precision;
	}
	 
	public void increment(boolean positive) {
	    setValue(getValue() + (positive ? 1 : -1) * increment);
	}
	  
	public double getMinimum() {
	    return this.minimum;
	}

	public void setMinimum(double minimum) {
	    this.minimum = minimum;
	}
	  
	public double getMaximum() {
	    return this.maximum;
	}
	
	public void setMaximum(double maximum) {
	    this.maximum = maximum;
	}
	  
	public double getIncrement() {
	    return this.increment;
	}
	  
	public void setIncrement(double increment) {
	    this.increment = increment;
	}
}