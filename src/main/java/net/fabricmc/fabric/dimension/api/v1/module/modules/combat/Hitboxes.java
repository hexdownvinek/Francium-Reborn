package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.ModeSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.util.MathUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;

public class Hitboxes extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", this, "Only Players", "All Entities", "Only Players");
    private final ModeSetting itemWhitelist = new ModeSetting("Item Whitelist", this, "Only Weapon", "All Items", "Only Weapon");
    private final NumberSetting expand = new NumberSetting("Expand", this, 0d, 0d, 5d, 0.1d);
    private final NumberSetting hitboxChance = new NumberSetting("Hitbox Chance", this, 100d, 0d, 100d, 1d);
    private final BooleanSetting renderExpandedHitboxes = new BooleanSetting("Render Expanded Hitboxes", this, true);

    public Hitboxes() {
        super("Hitboxes", "Extend hitboxes", 0, Category.COMBAT);
        addSettings(mode, itemWhitelist, expand, hitboxChance, renderExpandedHitboxes);
    }

    public float getHitboxSize(Entity entity) {
        switch (mode.getMode()) {
            case "Only Players" -> {
                if (!(entity instanceof Player))
                    return 0;
            }
        }

        switch (itemWhitelist.getMode()) {
            case "Only Weapon" -> {
                Item mainHandItem = mc.player.getMainHandItem().getItem();
                if (!(mainHandItem instanceof SwordItem || mainHandItem instanceof AxeItem))
                    return 0;
            }
        }

        int randomNum = MathUtils.getRandomInt(1, 100);

        if (randomNum <= hitboxChance.getIntValue())
            return expand.getFloatValue();

        return 0;
    }

    public double getRenderHitboxSize(Entity entity) {
        if (!renderExpandedHitboxes.isEnabled())
            return 0;

        switch (mode.getMode()) {
            case "Only Players" -> {
                if (!(entity instanceof Player))
                    return 0;
            }
        }

        return expand.getValue();
    }

}
