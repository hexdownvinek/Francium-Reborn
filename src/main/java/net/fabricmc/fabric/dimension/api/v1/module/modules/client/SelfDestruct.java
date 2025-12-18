package net.fabricmc.fabric.dimension.api.v1.module.modules.client;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.module.setting.Setting;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public class SelfDestruct extends Module {

    public SelfDestruct() {
        super("Self Destruct", "removes string traces from game", 0, Category.CLIENT);
    }

    @Override
    public void onEnable() {
        if (mc.screen != null)
            mc.setScreen(null);

        for (int moduleIndex = 0; moduleIndex < client.moduleManager.modules.size(); moduleIndex++) {
            Module module = client.moduleManager.modules.get(moduleIndex);

            if (module.enabled)
                module.setEnabled(false);

            module.setName(null);
            module.setDescription(null);

            for (Setting setting : module.getSettings())
                setting.setName(null);

            client.moduleManager.modules.set(moduleIndex, null);
        }

        for (Category category1 : Category.values()) {
            category1.setName(null);
        }

        Client.shutDown();

        Runtime currentRuntime = Runtime.getRuntime();

        for (int i = 1; i <= 10; i++) {
            currentRuntime.gc();
            currentRuntime.runFinalization();

            try {
                Thread.sleep(100 * i);
            } catch (InterruptedException e) {
                // empty
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
