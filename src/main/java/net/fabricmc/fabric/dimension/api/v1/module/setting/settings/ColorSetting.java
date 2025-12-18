package net.fabricmc.fabric.dimension.api.v1.module.setting.settings;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.dimension.api.v1.gui.components.windows.ColorWindow;
import net.fabricmc.fabric.dimension.api.v1.module.setting.RenderableSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.Setting;
import net.fabricmc.fabric.dimension.api.v1.font.JColor;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.util.ColorUtils;
import net.fabricmc.fabric.dimension.api.v1.util.RenderUtils;
import org.lwjgl.glfw.GLFW;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class ColorSetting extends RenderableSetting {

	private boolean rainbow;
	private JColor value;
	private ColorWindow colorWindow;
	private final int colorWindowWidth = 300;
	private final int colorWindowHeight = 490;


	public ColorSetting(String name, Module parent, final JColor value) {
		this.name = name;
		this.parent = parent;
		this.value = value;
		this.width = 30;
		this.height = 30;
	}

	public void setColorWindow(ColorWindow colorWindow) {
		this.colorWindow = colorWindow;
	}

	public boolean isRainbow() {
		return this.rainbow;
	}

	public JColor getValue() {
		if (rainbow) {
			return getRainbowColor(0, this.getColor().getAlpha());
		}
		return this.value;
	}

	public static JColor getRainbowColor(int incr, int alpha) {
		JColor color =  JColor.fromHSB(((System.currentTimeMillis() + incr * 200)%(360*20))/(360f * 20),0.5f,1f);
		return new JColor(color.getRed(), color.getBlue(), color.getGreen(), alpha);
	}


	public void setValue (boolean rainbow, final JColor value) {
		this.rainbow = rainbow;
		this.value = value;
	}

	public long toInteger() {
		return this.value.getRGB() & (0xFFFFFFFF);
	}

	public void fromInteger (long number) {
		this.value = new JColor(Math.toIntExact(number & 0xFFFFFFFF),true);
	}
	
	public JColor getColor() {
		return this.value;
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
		super.render(poseStack, mouseX, mouseY, delta);

		RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getSecondColor(), getX(), getY(), getX() + 1, getY() + getHeight());
		RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getSecondColor(), getX() + getWidth() - 1, getY(), getX() + getWidth(), getY() + getHeight());
		RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getSecondColor(), getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight());
		RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getSecondColor(), getX(), getY(), getX() + getWidth(), getY() + 1);

		RenderUtils.R2D.renderQuad(poseStack, getValue(), getX() + 2, getY() + 2, getX() + getWidth() - 2, getY() + getHeight() - 2);

		if (colorWindow != null)
			colorWindow.render(poseStack, mouseX, mouseY, delta);
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (colorWindow != null)
			colorWindow.mouseClicked(mouseX, mouseY, button);

		if (isCursorInside(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_1 && colorWindow == null)
			colorWindow = new ColorWindow(this, (mc.getWindow().getWidth() / 2) - (colorWindowWidth / 2), (mc.getWindow().getHeight() / 2) - (colorWindowHeight / 2), colorWindowWidth, colorWindowHeight);
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		if (colorWindow != null)
			colorWindow.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (colorWindow != null)
			colorWindow.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}
}