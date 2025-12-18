package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import io.netty.util.internal.MathUtil;
import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerRemoveEvent;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventPriority;
import net.fabricmc.fabric.dimension.api.v1.util.*;
import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;
import javax.swing.text.html.parser.Entity;

@SuppressWarnings("all")
public class CwCrystal extends Module {
    public final BooleanSetting noItemShrink = new BooleanSetting("No Item Shrink", this, true);
    private final BooleanSetting activatesOnRightClick = new BooleanSetting("Activates on RMB", this, true);
    private final NumberSetting placeChance = new NumberSetting("Place Chance", this, 100d, 0d, 100d, 1d);
    private final NumberSetting breakChance = new NumberSetting("Break Chance", this, 100d, 0d, 100d, 1d);
    private final BooleanSetting clickSimulate = new BooleanSetting("Click Simulate", this, true);
    private final NumberSetting clickChance = new NumberSetting("Click Chance", this, 80d, 0d, 100d, 1d);
    private final BooleanSetting stopOnKill = new BooleanSetting("Stop on Kill", this, true);
    private final NumberSetting placeDelay = new NumberSetting("Place Delay", this, 0d, 0d, 10d, 0.1d);
    private final NumberSetting breakDelay = new NumberSetting("Break Delay", this, 0d, 0d, 10d, 0.1d);

    private int crystalPlaceClock;
    private int crystalBreakClock;
    private boolean foundDeadPlayer;

    public CwCrystal() {
        super("Cw Crystal", "Automatically break and place crystal", 0, Category.COMBAT);
        addSettings(noItemShrink, activatesOnRightClick, placeChance, breakChance, clickSimulate, clickChance, stopOnKill, placeDelay, breakDelay);
    }

    public void reset() {
        crystalBreakClock = breakDelay.getIntValue();
        crystalPlaceClock = placeDelay.getIntValue();
        foundDeadPlayer = false;

        if (MouseSimulation.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_2)) {
            MouseSimulation.mouseRelease(GLFW.GLFW_MOUSE_BUTTON_2);
        }
        if (MouseSimulation.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
            MouseSimulation.mouseRelease(GLFW.GLFW_MOUSE_BUTTON_1);
        }
    }

    @Override
    public void onEnable() {
        reset();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTick(PlayerTickEvent event) {
        if (activatesOnRightClick.isEnabled() && KeyUtils.isKeyNotPressed(GLFW.GLFW_MOUSE_BUTTON_2)) {
            reset();
            return;
        }

        ItemStack mainHand = mc.player.getMainHandItem();

        if (mainHand.is(Items.END_CRYSTAL)) {
            mc.options.keyUse.setDown(false);

            if (stopOnKill.isEnabled()) {
                if (foundDeadPlayer) return;
            }

            boolean dontPlaceCrystal = crystalPlaceClock > 0;
            boolean dontBreakCrystal = crystalBreakClock > 0;

            if (dontPlaceCrystal) crystalPlaceClock--;
            if (dontBreakCrystal) crystalBreakClock--;

            int randomNum = MathUtils.getRandomInt(1, 100);

            if (randomNum <= placeChance.getIntValue()) {
                if (mc.hitResult instanceof BlockHitResult blockHit
                        && CrystalUtils.canPlaceCrystalClient(blockHit.getBlockPos())
                        && !dontPlaceCrystal) {
                    if (blockHit.getType() == HitResult.Type.MISS)
                        return;

                    if (clickSimulate.isEnabled()) {
                        randomNum = MathUtils.getRandomInt(1, 100);

                        if (randomNum <= clickChance.getIntValue()) {
                            MouseSimulation.mouseClick(GLFW.GLFW_MOUSE_BUTTON_2);
                        }
                    }

                    InteractionResult interactionResult = mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, blockHit);
                    if (interactionResult.consumesAction() && interactionResult.shouldSwing()) {
                        mc.player.swing(InteractionHand.MAIN_HAND);
                    }

                    crystalPlaceClock = placeDelay.getIntValue();
                }
            }

            randomNum = MathUtils.getRandomInt(1, 100);

            if (randomNum <= breakChance.getIntValue()) {
                if (mc.hitResult instanceof EntityHitResult entityHit
                        && (entityHit.getEntity() instanceof EndCrystal || entityHit.getEntity() instanceof Slime)
                        && !dontBreakCrystal) {
                    if (!CrystalUtils.isReplacedCrystal(entityHit.getEntity()))
                        return;

                    if (clickSimulate.isEnabled()) {
                        randomNum = MathUtils.getRandomInt(1, 100);

                        if (randomNum <= clickChance.getIntValue()) {
                            MouseSimulation.mouseClick(GLFW.GLFW_MOUSE_BUTTON_1);
                        }
                    }

                    mc.gameMode.attack(mc.player, entityHit.getEntity());
                    mc.player.swing(InteractionHand.MAIN_HAND);

                    crystalBreakClock = breakDelay.getIntValue();
                }
            }
        } else {
            reset();
        }
    }

    @EventHandler
    private void onPlayerRemove(PlayerRemoveEvent event) {
        RemotePlayer player = event.player;

        if (player.distanceTo(mc.player) <= 30)
            foundDeadPlayer = true;
    }

}
