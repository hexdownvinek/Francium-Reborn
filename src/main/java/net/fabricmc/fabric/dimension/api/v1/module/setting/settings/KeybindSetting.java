package net.fabricmc.fabric.dimension.api.v1.module.setting.settings;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.font.FontRenderer;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.RenderableSetting;

import net.fabricmc.fabric.dimension.api.v1.util.ColorUtils;
import net.fabricmc.fabric.dimension.api.v1.util.KeyUtils;
import net.fabricmc.fabric.dimension.api.v1.util.RenderUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class KeybindSetting extends RenderableSetting {
	
	public int code;
	private boolean mouseButton;
	
	public KeybindSetting(String name, Module parent, int code) {
		this.name = name;
		this.parent = parent;
		this.code = code;
		this.width = 200;
		this.height = 20;
		this.mouseButton = 1 <= code && code <= 8;
	}

	public KeybindSetting setKeybindName(String name) {
		this.setName(name);
		return this;
	}

	public boolean isMouseButton() {
		return mouseButton;
	}

	public int getKeyCode() {
		return this.code;
	}
	
	public void setKeyCode(int code) {
		this.code = code;
		this.mouseButton = 1 <= code && code <= 8;
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
		super.render(poseStack, mouseX, mouseY, delta);

		String keyName = KeyUtils.getKeyName(code);

		setWidth(FontRenderer.getFontWidth(keyName) + 10);

		RenderUtils.R2D.renderQuad(poseStack, ColorUtils.getBackgroundColor(), getX(), getY(), getX() + getWidth(), getY() + getHeight());

		FontRenderer.drawString(poseStack, keyName, (float) (getX() + 5), (float) (getY() + 2), ColorUtils.getTextColor());
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (isCursorInside(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_1) {
			mc.setScreen(new Screen(Component.empty()) {
				private final Screen prevScreen = mc.screen;

				@Override
				public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
					renderBackground(poseStack);
					FontRenderer.drawCenteredString(poseStack, Client.pressKey, width / 2, height / 2, ColorUtils.getTextColor());
				}

				@Override
				public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
					setKeyCode(keyCode);

					mc.setScreen(prevScreen);
					return false;
				}

				@Override
				public boolean mouseClicked(double mouseX, double mouseY, int keyCode) {
					if (1 <= keyCode && keyCode <= 8) {
						setKeyCode(keyCode);

						mc.setScreen(prevScreen);
					}

					return false;
				}
			});
		}
	}
}