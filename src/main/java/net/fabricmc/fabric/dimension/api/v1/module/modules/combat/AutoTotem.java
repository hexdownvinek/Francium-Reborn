package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.mixin.AbstractContainerScreenAccessor;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.KeybindSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public class AutoTotem extends Module {

    private final NumberSetting delay = new NumberSetting("Delay", this, 0d, 0d, 10d, 1d);
    private final NumberSetting totemSlot = new NumberSetting("Totem Slot", this, 9d, 0d, 9d, 1d);
    private final BooleanSetting activateOnKey = new BooleanSetting("Activate On Key", this, false);
    private final KeybindSetting activateKey = new KeybindSetting("Keybind", this, GLFW.GLFW_KEY_X);
    private int totemClock;
    private boolean placedTotem;

    public AutoTotem() {
        super("AutoTotem", "Hover over a totem and it will put it in your offhand", 0, Category.COMBAT);
        addSettings(delay, totemSlot, activateOnKey, activateKey);
    }

    public void reset() {
        totemClock = delay.getIntValue();
        placedTotem = false;
    }

    public void onEnable() {
        reset();
    }

    public void onPlayerTick(PlayerTickEvent event) {
        if (mc.player == null) return;

        Screen currentScreen = mc.screen;
        if (!(currentScreen instanceof InventoryScreen)) return;

        if (!placedTotem) {
            if (mc.player.getMainHandItem().isEmpty()) {
                if (totemClock > 0) {
                    totemClock--;
                } else {
                    int dirtSlotIndex = findDirtSlot(mc.player);
                    if (dirtSlotIndex != -1) {
                        mc.gameMode.handleInventoryMouseClick(0, findDirtSlot(mc.player), 0, ClickType.PICKUP, mc.player);
                        mc.gameMode.handleInventoryMouseClick(0, findDirtSlot(mc.player), 45, ClickType.PICKUP, mc.player);
                        placedTotem = true;
                    }
                }
            }
        } else {
            reset();
        }
    }

    public static int findDirtSlot(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == Items.DIRT) {
                return i;
            }
        }
        return -1; // Dirt block not found in the inventory
    }
    }
