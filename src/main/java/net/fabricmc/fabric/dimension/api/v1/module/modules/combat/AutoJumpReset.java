package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.event.events.WorldRenderEvent;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.KeybindSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.util.KeyUtils;

@SuppressWarnings("all")
public class AutoJumpReset extends Module {
    private final BooleanSetting activateOnKey = new BooleanSetting("Activate On Key", this, false);
    private final KeybindSetting activateKey = new KeybindSetting("Activate Key", this, 0);

    public AutoJumpReset() {
        super("Auto Jump Reset", "automatically doing jump reset", 0, Category.COMBAT);
        addSettings(activateOnKey, activateKey);
    }

    @EventHandler
    private void onWorldRender(WorldRenderEvent event) {
        if (mc.player == null)
            return;

        if (mc.screen != null)
            return;

        if (activateOnKey.isEnabled() && KeyUtils.isKeyNotPressed(activateKey.getKeyCode()))
            return;

        if (mc.player.isBlocking())
            return;

        if (mc.player.isUsingItem())
            return;

        if (!mc.player.isOnGround())
            return;

        if (mc.player.hurtDuration == 0)
            return;

        if (mc.player.hurtTime == 0)
            return;

        if (mc.player.hurtTime == mc.player.hurtDuration - 1)
            mc.player.jumpFromGround();
    }

}
