package net.fabricmc.fabric.dimension.api.v1;

import net.fabricmc.fabric.dimension.api.v1.gui.ClickGui;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventBus;
import net.fabricmc.fabric.dimension.api.v1.orbit.IEventBus;
import net.fabricmc.fabric.dimension.api.v1.util.MouseSimulation;
import net.fabricmc.fabric.dimension.api.v1.util.rotation.RotatorManager;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.fabric.dimension.api.v1.module.ModuleManager;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public final class Client {

	public static Client INSTANCE;
	public static Minecraft mc;
	public static String name = "Francium";
	public static String pressKey = "Press your key...";
	public static String packagePrefix = "net.fabricmc.fabric.dimension.api.v1";
	public static Logger LOGGER = LogManager.getLogger(name);
	public static IEventBus EVENTBUS = new EventBus();
	
	public Client() {
		mc = Minecraft.getInstance();
		INSTANCE = this;
		INSTANCE.init();
	}

	public ModuleManager moduleManager;
	public ClickGui clickGui;
	public RotatorManager rotatorManager;

	public static ModuleManager moduleManager() {
		return INSTANCE.moduleManager;
	}

	public static ClickGui clickGui() {
		return INSTANCE.clickGui;
	}

	public static RotatorManager rotatorManager() {
		return INSTANCE.rotatorManager;
	}
	public static String getName() {
		return name;
	}

	public final Object syncronize = new Object();
	public void printLog(String text) {
		synchronized (syncronize) {
			LOGGER.info(text);
		}
	}

	public void init() {
		this.moduleManager = new ModuleManager();
		this.clickGui = new ClickGui();
		this.rotatorManager = new RotatorManager();

		EVENTBUS.registerLambdaFactory(packagePrefix, (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
		EVENTBUS.subscribe(moduleManager);
		EVENTBUS.subscribe(rotatorManager);
	}

	public static void shutDown() {
		name = null;
		pressKey = null;
		packagePrefix = null;

		EVENTBUS.unsubscribe(INSTANCE.moduleManager);
		EVENTBUS.unsubscribe(INSTANCE.rotatorManager);

		INSTANCE.moduleManager.clearModules();
		INSTANCE.moduleManager = null;
		INSTANCE.clickGui = null;
		INSTANCE.rotatorManager = null;

		mc = null;
		INSTANCE = null;
		LOGGER = null;
		EVENTBUS = null;

		try {
			MouseSimulation.clickExecutor.shutdown();
			MouseSimulation.clickExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {

		}
	}

}
