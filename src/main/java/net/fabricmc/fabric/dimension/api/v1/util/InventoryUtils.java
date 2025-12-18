package net.fabricmc.fabric.dimension.api.v1.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class InventoryUtils {

    /**
     * Returns integer of a slot with splash potion of your specific potion effect
     *
     * @param  rawId  		You can get id of your specific effect from https://minecraft.fandom.com/el/wiki/Status_effect
     * @param  duration		Duration of potion effect
     * @param  amplifier	Multiplier of potion effect
     *
     * @return int
     *
     * @author pycat
     */
    public static int findSplash(int rawId, int duration, int amplifier) {
        Inventory playerInv = mc.player.getInventory();
        MobEffectInstance potion = new MobEffectInstance(MobEffect.byId(rawId), duration, amplifier);

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = playerInv.getItem(i);

            if (itemStack.getItem() instanceof SplashPotionItem
                    && PotionUtils.getPotion(itemStack).getEffects().toString()
                    .contains(potion.toString())) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns true if itemStack has the specific splash potion
     *
     * @param  rawId  			You can get id of your specific effect from https://minecraft.fandom.com/el/wiki/Status_effect
     * @param  duration			Duration of potion effect
     * @param  amplifier		Multiplier of potion effect
     * @param  itemStack		ItemStack to check
     *
     * @return boolean
     *
     * @author pycat
     */
    public static boolean isThatSplash(int rawId, int duration, int amplifier, ItemStack itemStack) {
        MobEffectInstance potion = new MobEffectInstance(MobEffect.byId(rawId), duration, amplifier);

        return itemStack.getItem() instanceof SplashPotionItem
                && PotionUtils.getPotion(itemStack).getEffects().toString().contains(potion.toString());
    }

    public static int getItemSlot(Item item) {
        Inventory playerInventory = mc.player.getInventory();

        for (int itemIndex = 0; itemIndex < 9; itemIndex++) {
            if (playerInventory.getItem(itemIndex).is(item))
                return itemIndex;
        }

        return -1;
    }

    public static int getSwordSlot() {
        Inventory playerInventory = mc.player.getInventory();

        for (int itemIndex = 0; itemIndex < 9; itemIndex++) {
            if (playerInventory.getItem(itemIndex).getItem() instanceof SwordItem)
                return itemIndex;
        }

        return -1;
    }

    public static int getAxeSlot() {
        Inventory playerInventory = mc.player.getInventory();

        for (int itemIndex = 0; itemIndex < 9; itemIndex++) {
            if (playerInventory.getItem(itemIndex).getItem() instanceof AxeItem)
                return itemIndex;
        }

        return -1;
    }

    public static boolean selectItem(Item item) {
        int itemIndex = getItemSlot(item);

        if (itemIndex != -1) {
            mc.player.getInventory().selected = itemIndex;
            return true;
        } else {
            return false;
        }
    }

    public static boolean selectAxe() {
        int itemIndex = getAxeSlot();

        if (itemIndex != -1) {
            mc.player.getInventory().selected = itemIndex;
            return true;
        } else {
            return false;
        }
    }

    public static boolean selectSword() {
        int itemIndex = getSwordSlot();

        if (itemIndex != -1) {
            mc.player.getInventory().selected = itemIndex;
            return true;
        } else {
            return false;
        }
    }

}
