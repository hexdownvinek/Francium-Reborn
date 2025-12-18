package net.fabricmc.fabric.dimension.api.v1.module.setting.settings;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.font.FontRenderer;
import net.fabricmc.fabric.dimension.api.v1.module.setting.RenderableSetting;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.util.ColorUtils;
import net.fabricmc.fabric.dimension.api.v1.util.RenderUtils;
import org.lwjgl.glfw.GLFW;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class ModeSetting extends RenderableSetting {
	public int index;
	  
	public List<String> modes;
	  
	public ModeSetting(String name, Module parent, String defaultMode, String... modes) {
	    this.name = name;
	    this.parent = parent;
	    this.modes = Arrays.asList(modes);
	    this.index = this.modes.indexOf(defaultMode);
		this.width = 200;
		this.height = 20;
	}
	  
	public String getMode() {
	    return this.modes.get(this.index);
	}
	  
	public void setMode(String mode) {
		this.index = this.modes.indexOf(mode);
	}
	  
	public boolean is(String mode) {
	    return (this.index == this.modes.indexOf(mode));
	}
	  
	public void cycle() {
	    if (this.index < this.modes.size() - 1) {
			this.index++;
	    } else {
	      	this.index = 0;
	    }
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
		super.render(poseStack, mouseX, mouseY, delta);

		setWidth(FontRenderer.getFontWidth(getMode()) + 10);

		RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getBackgroundColor(), getX(), getY(), getX() + getWidth(), getY() + getHeight());

		FontRenderer.drawString(poseStack, getMode(), (float) (getX() + 5), (float) (getY() + 2), ColorUtils.getTextColor());
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (isCursorInside(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_1)
			cycle();
	}
}