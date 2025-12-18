package net.fabricmc.fabric.dimension.api.v1.module.modules.player;

import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.util.KeyUtils;
import net.fabricmc.fabric.dimension.api.v1.util.MathUtils;
import net.fabricmc.fabric.dimension.api.v1.util.MouseSimulation;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Method;

@SuppressWarnings("all")
public class FastPlace extends Module {

    private final BooleanSetting activatesOnRightClick = new BooleanSetting("Activate On Right Click", this, true);
    private final BooleanSetting doPlaceInGui = new BooleanSetting("Place in GUI", this, false);
    private final NumberSetting delay = new NumberSetting("Delay", this, 0d, 0d, 10d, 1d);
    private final NumberSetting useChance = new NumberSetting("Use Chance", this, 80d, 0d, 100d, 1d);
    private final BooleanSetting clickSimulate = new BooleanSetting("Click Simulate", this, true);
    private final NumberSetting clickChance = new NumberSetting("Click Chance", this, 100d, 0d, 100d, 1d);
    private int placeClock = 0;

    public FastPlace() {
        super("Fast Place", "Automatically places shit", 0, Category.PLAYER);
        addSettings(activatesOnRightClick, doPlaceInGui, delay, useChance, clickSimulate, clickChance);
    }

    public void reset() {
        placeClock = delay.getIntValue();
    }

    @Override
    public void onEnable() {
        reset();
    }

    @EventHandler
    public void onPlayerTick(PlayerTickEvent event) {


        if (mc.screen != null && !this.doPlaceInGui.isEnabled())
            return;

        if (this.activatesOnRightClick.isEnabled() && KeyUtils.isKeyNotPressed(GLFW.GLFW_MOUSE_BUTTON_2))
            return;

        if (mc.player.getMainHandItem().getItem().getFoodProperties() != null)
            return;

        if (placeClock > 0) {
            placeClock--;
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
            InteractionResult interactionResult;
            if (mc.hitResult instanceof BlockHitResult blockHit && blockHit.getType() != HitResult.Type.MISS) {
                interactionResult = mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, blockHit);

                if (interactionResult == InteractionResult.PASS)
                    interactionResult = mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
            } else {
                interactionResult = mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND);
            }

            if (interactionResult.consumesAction() && interactionResult.shouldSwing()) {
                mc.player.swing(InteractionHand.MAIN_HAND);
            }

            placeClock = delay.getIntValue();
        }
    }
}
