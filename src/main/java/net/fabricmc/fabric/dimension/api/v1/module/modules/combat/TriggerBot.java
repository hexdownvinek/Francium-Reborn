package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.event.events.AttackEvent;
import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.mixin.MinecraftAccessor;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.ModeSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventPriority;
import net.fabricmc.fabric.dimension.api.v1.util.KeyUtils;
import net.fabricmc.fabric.dimension.api.v1.util.MathUtils;
import net.fabricmc.fabric.dimension.api.v1.util.MouseSimulation;
import net.fabricmc.fabric.dimension.api.v1.util.PlayerUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.EntityHitResult;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public class TriggerBot extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "Only Weapon", "All Items", "Only Weapon");
    private final NumberSetting hitChance = new NumberSetting("Hit Chance", this, 100d, 0d, 100d, 1d);
    private final NumberSetting minRange = new NumberSetting("Min Range", this, 2.8d, 0.1d, 5d, 0.1d);
    private final NumberSetting maxRange = new NumberSetting("Max Range", this, 3d, 0.1d, 5d, 0.1d);
    private final NumberSetting delay = new NumberSetting("Delay", this, 0d, 0d, 10d, 1d);
    private final NumberSetting cooldown = new NumberSetting("Cooldown", this, 1d, 0d, 1d, 0.1d);
    private final BooleanSetting clickSimulate = new BooleanSetting("Click Simulate", this, true);
    private final BooleanSetting attackMobs = new BooleanSetting("Attack Mobs", this, false);
    private final BooleanSetting attackOnCrit = new BooleanSetting("Attack On Crit", this, false);

    private TriggerStatus triggerStatus;
    private int delayClock;
    private double currentRange;

    enum TriggerStatus {
        IDLE,
        ATTACKING
    }

    public TriggerBot() {
        super("Trigger Bot", "Automatically hits player when aiming at them", 0, Category.COMBAT);
        addSettings(mode, hitChance, minRange, maxRange, delay, cooldown, clickSimulate, attackMobs, attackOnCrit);
    }

    public void reset() {
        triggerStatus = TriggerStatus.IDLE;
        delayClock = delay.getIntValue();
        currentRange = 0;
    }

    @Override
    public void onEnable() {
        reset();
    }

    private double getRandomRange() {
        return MathUtils.getRandomDouble(minRange.getValue(), maxRange.getValue());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerTick(PlayerTickEvent event) {
        if (mc.screen == null && !mc.player.isUsingItem() && !mc.player.isBlocking()) {
            if (mode.getMode().equals("Only Weapon") && !(mc.player.getMainHandItem().getItem() instanceof SwordItem || mc.player.getMainHandItem().getItem() instanceof AxeItem))
                return;

            if (mc.hitResult instanceof EntityHitResult entityHit) {
                if (mc.player.getAttackStrengthScale(0.0f) >= this.cooldown.getValue()) {
                    Entity target = entityHit.getEntity();

                    if (!(target instanceof Player) && !attackMobs.isEnabled())
                        return;

                    if (!(target instanceof EndCrystal) && target.isAlive()) {
                        if (triggerStatus == TriggerStatus.IDLE) {
                            if (delayClock > 0) {
                                delayClock--;
                                return;
                            }
                        }

                        if (attackOnCrit.isEnabled() && !PlayerUtils.isCrit(target))
                            return;

                        int randomNum = MathUtils.getRandomInt(1, 100);

                        if (randomNum <= hitChance.getIntValue()) {
                            if (currentRange == 0)
                                currentRange = getRandomRange();

                            if (target.getBoundingBox().distanceToSqr(mc.player.position()) <= Math.pow(currentRange, 2)) {
                                if (clickSimulate.isEnabled()) {
                                    MouseSimulation.mouseClick(GLFW.GLFW_MOUSE_BUTTON_1);
                                }

                                mc.gameMode.attack(mc.player, target);
                                mc.player.swing(InteractionHand.MAIN_HAND);

                                delayClock = delay.getIntValue();
                                triggerStatus = TriggerStatus.ATTACKING;
                                currentRange = 0;
                            }
                        } else {
                            if (mc.gameMode.hasMissTime()) {
                                ((MinecraftAccessor) mc).setMissTime(10);
                            }

                            mc.player.resetAttackStrengthTicker();
                            mc.player.swing(InteractionHand.MAIN_HAND);
                        }
                    }
                }
            } else {
                triggerStatus = TriggerStatus.IDLE;
            }
        }
    }

    @EventHandler
    private void onAttack(AttackEvent event) {
        if (mode.getMode().equals("Only Weapon") && !(mc.player.getMainHandItem().getItem() instanceof SwordItem || mc.player.getMainHandItem().getItem() instanceof AxeItem))
            return;

        if (KeyUtils.isKeyNotPressed(GLFW.GLFW_MOUSE_BUTTON_1))
            event.cancel();
    }
}
