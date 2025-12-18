package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.mixin.AbstractContainerScreenAccessor;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.KeybindSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.util.KeyUtils;
import net.fabricmc.fabric.dimension.api.v1.util.MouseSimulation;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public class LegitLootYeeter extends Module {

    private final NumberSetting delay = new NumberSetting("delay", this, 0d, 0d, 10d, 1d);
    private final NumberSetting minTotems = new NumberSetting("Minimum totems", this, 5d, 0d, 20d, 1d);
    private final NumberSetting minPearls = new NumberSetting("Minimum Pearls", this, 0d, 0d, 10d, 1d);
    private final NumberSetting minCrystals = new NumberSetting("Minimum Crystals", this, 0d, 0d, 10d, 1d);
    private final BooleanSetting keepElytra = new BooleanSetting("Keep Elytra", this, true);
    private final BooleanSetting activateOnKey = new BooleanSetting("Activate On Key", this, true);
    private final KeybindSetting activateKey = new KeybindSetting("Activate Key", this, GLFW.GLFW_KEY_X);
    private int delayClock;



    public LegitLootYeeter() {
        super("Legit Loot Yeeter", "Automatically loots when you're holding keybind", 0, Category.COMBAT);
        addSettings(delay, minTotems, minPearls, minCrystals, keepElytra, activateOnKey, activateKey);
    }

    public void reset() {
        delayClock = delay.getIntValue();
    }
    
    public void onEnable(){
        reset();
    }

    private int countItemInInventory(Item item) {
        Inventory inv = mc.player.getInventory();

        int count = 0;

        for (int i = 9; i < 36; i++) {
            ItemStack itemStack = inv.getItem(i);
            if (itemStack.is(item))
                count += itemStack.getCount();
        }

        return count;
    }

    private boolean checkItem(ItemStack itemUnderMouse) {
        if (itemUnderMouse.is(Items.TOTEM_OF_UNDYING)) {
            if (countItemInInventory(Items.TOTEM_OF_UNDYING) <= minTotems.getValue())
                return false;
        } else if (itemUnderMouse.is(Items.END_CRYSTAL)) {
            if (countItemInInventory(Items.END_CRYSTAL) / 64 <= minCrystals.getValue())
                return false;
        } else if (itemUnderMouse.is(Items.ENDER_PEARL)) {
            if (countItemInInventory(Items.ENDER_PEARL) / 16 <= minPearls.getValue())
                return false;
        } else if (itemUnderMouse.is(Items.ELYTRA) && !keepElytra.isEnabled()) {
            return false;
        } else if (itemUnderMouse.getItem() instanceof TieredItem toolItem) {
            if (toolItem.getTier() == Tiers.NETHERITE || toolItem.getTier() == Tiers.DIAMOND)
                return false;
        } else if (itemUnderMouse.getItem() instanceof ArmorItem armorItem) {
            if (armorItem.getMaterial() == ArmorMaterials.NETHERITE ||
                    armorItem.getMaterial() == ArmorMaterials.DIAMOND)
                return false;
        }

        return true;

    }

    @EventHandler
    public void onPlayerTick(PlayerTickEvent event) {
        if (activateOnKey.isEnabled() && KeyUtils.isKeyNotPressed(activateKey.getKeyCode()))
            return;

        if (mc.screen instanceof InventoryScreen screen) {

            Slot focusedSlot = ((AbstractContainerScreenAccessor) screen).getHoveredSlot();

            if (focusedSlot != null) {
                int slot = focusedSlot.getContainerSlot();

                if (slot <= 35) {

                    if (delayClock > 0) {
                        delayClock++;
                        return;
                    }

                    ItemStack itemUnderMouse = mc.player.getInventory().getItem(slot);

                    if (checkItem(itemUnderMouse)) {
                        mc.gameMode.handleInventoryMouseClick(screen.getMenu().containerId,
                                slot,
                                1,
                                ClickType.THROW,
                                mc.player);
                        delayClock = delay.getIntValue();
                    }

                }
            }
        }
    }
}
