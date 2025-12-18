package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventPriority;
import net.fabricmc.fabric.dimension.api.v1.util.KeyUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public class AutoBowSpam extends Module {

    private NumberSetting charge = new NumberSetting("Bow Charge", this, 5d, 3d, 20d, 1d);

    public AutoBowSpam() {
        super("Auto Bow Spam", "Automatically bow spamming on right click", 0, Category.COMBAT);
        addSettings(charge);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerTick(PlayerTickEvent event) {
        if (mc.player.getMainHandItem().is(Items.BOW)) {
            if (KeyUtils.isKeyPressed(GLFW.GLFW_MOUSE_BUTTON_2)) {
                if (mc.player.getTicksUsingItem() >= charge.getIntValue()) {
                    mc.player.stopUsingItem();
                    mc.gameMode.releaseUsingItem(mc.player);
                } else {
                    mc.options.keyUse.setDown(true);
                }
            } else {
                mc.options.keyUse.setDown(false);
            }
        }
    }
}
