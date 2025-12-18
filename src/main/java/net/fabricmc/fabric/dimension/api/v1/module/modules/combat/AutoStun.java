package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.KeybindSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.util.InventoryUtils;
import net.fabricmc.fabric.dimension.api.v1.util.KeyUtils;
import net.fabricmc.fabric.dimension.api.v1.util.MouseSimulation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public class AutoStun extends Module {
    private final BooleanSetting clickSimulate = new BooleanSetting("Click Simulate", this, true);
    private final NumberSetting delay = new NumberSetting("Delay", this, 0d, 0d, 10d, 1d);
    private final NumberSetting switchDelay = new NumberSetting("Switch Delay", this, 0d, 0d, 10d, 1d);
    private final NumberSetting attackDelay = new NumberSetting("Attack Delay", this, 0d, 0d, 10d, 1d);
    private final KeybindSetting activateKey = new KeybindSetting("Activate Key", this, 0);

    public AutoStun() {
        super("Auto Stun", "automatically breaks shield of player with axe", 0, Category.COMBAT);
        addSettings(clickSimulate, delay, switchDelay, attackDelay, activateKey);
    }

    private int switchClock, attackClock, delayClock;

    public void reset() {
        switchClock = switchDelay.getIntValue();
        attackClock = attackDelay.getIntValue();
        delayClock = delay.getIntValue();
    }

    private boolean isUsingShield(Player player) {
        for (InteractionHand interactionHand : InteractionHand.values()) {
            if (player.getItemInHand(interactionHand).is(Items.SHIELD) &&
                    player.isUsingItem() &&
                    player.getUsedItemHand() == interactionHand) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onEnable() {
        reset();
    }

    @EventHandler
    private void onPlayerTick(PlayerTickEvent event) {
        if (KeyUtils.isKeyPressed(activateKey.getKeyCode())) {
            if (delayClock > 0) {
                delayClock--;
                return;
            }

            if (mc.hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof Player player) {
                if (isUsingShield(player)) {
                    if (!(mc.player.getMainHandItem().getItem() instanceof AxeItem)) {
                        if (switchClock > 0) {
                            switchClock--;
                            return;
                        }

                        InventoryUtils.selectAxe();

                        switchClock = switchDelay.getIntValue();
                    }
                    if (mc.player.getMainHandItem().getItem() instanceof AxeItem) {
                        if (attackClock > 0) {
                            attackClock--;
                            return;
                        }

                        if (clickSimulate.isEnabled())
                            MouseSimulation.mouseClick(GLFW.GLFW_MOUSE_BUTTON_1);

                        mc.gameMode.attack(mc.player, player);
                        mc.player.swing(InteractionHand.MAIN_HAND);

                        attackClock = attackDelay.getIntValue();
                        delayClock = delay.getIntValue();
                    }
                }
            }
        } else {
            reset();
        }
    }
}
