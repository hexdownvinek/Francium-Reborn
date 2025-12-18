package net.fabricmc.fabric.dimension.api.v1.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.module.setting.Setting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.KeybindSetting;
import net.minecraft.client.Minecraft;


@SuppressWarnings("all")
public abstract class Module {

	public String name, description;
	public KeybindSetting keyCode = new KeybindSetting("Keybind", this, 0);
	public Category category;
	public boolean enabled;
	public int index;
	public List<Setting> settings = new ArrayList<>();
	protected Minecraft mc;
	protected Client client;

	public Module(String name, String description, int key, Category category) {
		this.name = name;
		this.description = description;
		keyCode.code = key;
		addSettings(keyCode);
		this.category = category;
		enabled = false;
		mc = Client.mc;
		client = Client.INSTANCE;
	}

	public enum Category {
		COMBAT("Combat"), PLAYER("Player"), RENDER("Render"), CLIENT("Client");

		public String name;
		
		Category(String name) {
			this.name = name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	public void addSettings(Setting... settings) {
		this.settings.addAll(Arrays.asList(settings));
		this.settings.sort(Comparator.comparingInt(s -> s == keyCode ? 1 : 0));
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Category getCategory() {
		return this.category;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getKey() {
		return keyCode.code;
	}
	
	public void setKey(int key) {
		this.keyCode.code = key;
	}

	public List<Setting> getSettings() {
		return new ArrayList<>(settings);
	}
	
	public void toggle() {
		enabled = !enabled;
		if (enabled) {
			enable();
		} else {
			disable();
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		if (enabled) {
			Client.EVENTBUS.subscribe(this);
		} else {
			Client.EVENTBUS.unsubscribe(this);
		}
	}
	
	public void enable() {
		onEnable();
		setEnabled(true);
	}

	public void disable() {
		onDisable();
		setEnabled(false);
	}
	
	public void onEnable() {

	}
	
	public void onDisable() {

	}
}
