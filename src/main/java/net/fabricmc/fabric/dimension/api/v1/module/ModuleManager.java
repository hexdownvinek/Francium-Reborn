package net.fabricmc.fabric.dimension.api.v1.module;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.dimension.api.v1.event.events.KeyPressEvent;
import net.fabricmc.fabric.dimension.api.v1.module.modules.client.*;
import net.fabricmc.fabric.dimension.api.v1.module.modules.combat.*;
import net.fabricmc.fabric.dimension.api.v1.module.modules.player.*;
import net.fabricmc.fabric.dimension.api.v1.module.modules.render.*;
import net.fabricmc.fabric.dimension.api.v1.module.setting.Setting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.util.KeyUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public class ModuleManager {
	public ArrayList<Module> modules = new ArrayList<>();
	
	public ModuleManager() {
		addModules();
		Setting.init();
	}

	public boolean isModuleEnabled(String name) {
		Module module = getModule(name);

		if (module == null)
			return false;

		return module.isEnabled();
	}

	@Nullable
	public Module getModule(String name) {
		for (Module m : modules) {
			if (m.getName().equalsIgnoreCase(name)) {
				return m;
			}
		}
		return null;
	}

	public <T extends Module> T getModule(Class<T> moduleClass) {
		for (Module module : modules) {
			if (moduleClass.isAssignableFrom(module.getClass())) {
				return (T) module;
			}
		}
		return null;
	}

	@Nullable
	public List<Setting> getAllSettings() {
		List<Setting> allSettings = new ArrayList<>();

		for (Module module : modules)
			allSettings.addAll(module.getSettings());

		if (allSettings.isEmpty())
			return null;

		return allSettings;
	}

	public List<Setting> getSettingsByMod(Module module) {
		return module.settings;
	}

	@Nullable
	public Setting getSettingByName(Module module, String name) {
		for (Setting setting : module.settings) {
			if (setting.name.equalsIgnoreCase(name))
				return setting;
		}

		return null;
	}

	public ArrayList<Module> getModules() {
		return modules;
	}

	public void clearModules() {
		this.modules.clear();
	}

	public List<Module> getModulesByCategory(Module.Category c) {
		List<Module> modulesByCategory = new ArrayList<>();

		for (Module m : modules) {
			if (m.getCategory() == c)
				modulesByCategory.add(m);
		}

		return modulesByCategory;
	}

	public void addModule(Module module) {
		modules.add(module);
	}

	public void addModules() {
		// COMBAT
		addModule(new AimAssist());
		addModule(new AnchorMacro());
		addModule(new AutoBowSpam());
		addModule(new AutoDoubleHand());
		addModule(new AutoHitCrystal());
		addModule(new AutoHeadBob());
		addModule(new AutoJumpReset());
		addModule(new AutoStun());
		addModule(new CwCrystal());
		addModule(new Hitboxes());
		addModule(new LegitLootYeeter());
		addModule(new LegitRetotem());
		addModule(new Reach());
		addModule(new TriggerBot());
		addModule(new AutoTotem());
		addModule(new FastCrystals());



		// RENDER
		addModule(new NoBounce());
		addModule(new Freecam());
		// PLAYER
		addModule(new AutoXP());
		addModule(new FakePlayerModule());
		addModule(new FastPlace());

		// CLIENT
		addModule(new ClickGui());
		addModule(new DiscordRPC());
		addModule(new SelfDestruct());
	}

	@EventHandler
	private void onKeyPress(KeyPressEvent event) {
		if (KeyUtils.isKeyPressed(GLFW.GLFW_KEY_F3) || event.action != GLFW.GLFW_PRESS)
			return;

		modules.forEach(module -> {
			if (module.getKey() == event.key)
				module.toggle();
		});
	}

}
