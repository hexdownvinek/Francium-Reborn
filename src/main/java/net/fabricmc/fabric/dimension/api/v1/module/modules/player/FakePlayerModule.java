package net.fabricmc.fabric.dimension.api.v1.module.modules.player;

import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.util.FakePlayer;

@SuppressWarnings("all")
public class FakePlayerModule extends Module {
    public BooleanSetting infiniteHealth = new BooleanSetting("Infinite Health", this, true);
    public NumberSetting health = new NumberSetting("Health", this, 20d, 1d, 36d, 0.5d);
    public BooleanSetting copyInventory = new BooleanSetting("Copy Inventory", this, true);

    public FakePlayer fakePlayer;

    public FakePlayerModule() {
        super("Fake Player", "creates fake player", 0, Category.PLAYER);
        addSettings(infiniteHealth, health, copyInventory);
    }

    @Override
    public void onEnable() {
        fakePlayer = new FakePlayer(mc.player, infiniteHealth.isEnabled(), health.getFloatValue(), copyInventory.isEnabled());
        fakePlayer.spawn();
    }

    @Override
    public void onDisable() {
        fakePlayer.despawn();
    }
}
