package net.fabricmc.fabric.dimension.api.v1.module.modules.player;

import net.fabricmc.fabric.dimension.api.v1.event.events.ItemUseEvent;
import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.util.KeyUtils;
import net.fabricmc.fabric.dimension.api.v1.util.MathUtils;
import net.fabricmc.fabric.dimension.api.v1.util.MouseSimulation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public class AutoXP extends Module {

    private final BooleanSetting activatesOnRightClick = new BooleanSetting("Activate On Right Click", this, true);
    private final BooleanSetting onlyMainScreen = new BooleanSetting("MainList Screen Only", this, true);
    private final NumberSetting delay = new NumberSetting("Delay", this, 0d, 0d, 10d, 1d);
    private final NumberSetting useChance = new NumberSetting("Use Chance", this, 65d, 0d, 100d, 1d);
    private final BooleanSetting clickSimulate = new BooleanSetting("Click Simulate", this, true);
    private final NumberSetting clickChance = new NumberSetting("Click Chance", this, 100d, 0d, 100d, 1d);
    private int dropClock = 0;

    public AutoXP() {
        super("Auto Xp", "Automatically Breaks enchanting bottles idk i forgot the name", 0, Category.PLAYER);
        addSettings(activatesOnRightClick, onlyMainScreen, delay, useChance, clickSimulate, clickChance);
    }

    public void reset() {
        dropClock = delay.getIntValue();
    }

    @Override
    public void onEnable() {
        reset();
    }

    @EventHandler
    public void onPlayerTick(PlayerTickEvent event) {
        if (mc.screen != null && this.onlyMainScreen.isEnabled())
            return;

        if (mc.player.getMainHandItem().is(Items.EXPERIENCE_BOTTLE)) {
            if (this.activatesOnRightClick.isEnabled() && KeyUtils.isKeyNotPressed(GLFW.GLFW_MOUSE_BUTTON_2)) {
                return;
            } else if (this.activatesOnRightClick.isEnabled()) {
                mc.options.keyUse.setDown(false);
            }

            if (dropClock > 0) {
                dropClock--;
                return;
            }

            int randomNum = MathUtils.getRandomInt(1, 100);

            if (randomNum <= useChance.getIntValue()) {
                if (clickSimulate.isEnabled()) {
                    randomNum = MathUtils.getRandomInt(1, 100);

                    if (randomNum <= clickChance.getIntValue()) {
                        MouseSimulation.mouseClick(GLFW.GLFW_MOUSE_BUTTON_2);
                    }
                }

                InteractionResult interactionResult = mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
                if (interactionResult.consumesAction() && interactionResult.shouldSwing()) {
                    mc.player.swing(InteractionHand.MAIN_HAND);
                }

                dropClock = delay.getIntValue();
            }
        }
    }

    @EventHandler
    private void onItemUse(ItemUseEvent event) {
        if (mc.player.getMainHandItem().is(Items.EXPERIENCE_BOTTLE))
            event.cancel();
    }
}
