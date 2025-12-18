package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.mixin.AbstractContainerScreenAccessor;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.KeybindSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.util.KeyUtils;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public class LegitRetotem extends Module {

    private final NumberSetting delay = new NumberSetting("Delay", this, 0d, 0d, 10d, 1d);
    private final NumberSetting totemSlot = new NumberSetting("Totem Slot", this, 7d, 0d, 9d, 1d);
    private final BooleanSetting activateOnKey = new BooleanSetting("Activate On Key", this, false);
    private final KeybindSetting activateKey = new KeybindSetting("Keybind", this, GLFW.GLFW_KEY_X);
    private int totemClock;

    public LegitRetotem() {
        super("Legit Retotem", "Hover over a totem and it will put it in your offhand", 0, Category.COMBAT);
        addSettings(delay, totemSlot, activateOnKey, activateKey);
    }

    public void reset() {
        totemClock = delay.getIntValue();
    }

    public void onEnable(){
        reset();
    }

    @EventHandler
    public void onPlayerTick(PlayerTickEvent event) {
        if (mc.screen instanceof InventoryScreen screen) {
            if (activateOnKey.isEnabled() && KeyUtils.isKeyNotPressed(activateKey.getKeyCode())) {
                reset();
                return;
            }

            Slot focusedSlot = ((AbstractContainerScreenAccessor) screen).getHoveredSlot();

            if (focusedSlot != null) {
                int slot = focusedSlot.getContainerSlot();

                if (slot <= 35) {
                    int totem = totemSlot.getIntValue() - 1;

                    if (isTotem(slot)) {
                        if (!isTotem(totem)) {
                            if (totemClock > 0) {
                                totemClock--;
                                return;
                            }

                            mc.gameMode.handleInventoryMouseClick(
                                    screen.getMenu().containerId,
                                    slot,
                                    totem,
                                    ClickType.SWAP,
                                    mc.player);

                            totemClock = delay.getIntValue();
                        } else if (!mc.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) {
                            if (totemClock > 0) {
                                totemClock--;
                                return;
                            }

                            mc.gameMode.handleInventoryMouseClick(
                                    screen.getMenu().containerId,
                                    slot,
                                    40,
                                    ClickType.SWAP,
                                    mc.player);

                            totemClock = delay.getIntValue();
                        }
                    }
                }

            }
        } else {
            reset();
        }

    }

    private boolean isTotem(int slot) {
        return mc.player.getInventory().getItem(slot).is(Items.TOTEM_OF_UNDYING);
    }
}
