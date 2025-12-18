package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.util.AnchorBlockUtils;
import net.fabricmc.fabric.dimension.api.v1.util.BlockUtils;
import net.fabricmc.fabric.dimension.api.v1.util.InventoryUtils;
import net.fabricmc.fabric.dimension.api.v1.util.KeyUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public class AnchorMacro extends Module {

    private final NumberSetting switchDelay = new NumberSetting("Switch Delay", this, 0, 0, 10, 1);
    private final NumberSetting glowstoneDelay = new NumberSetting("Glowstone Delay", this, 0, 0, 10, 1);
    private final NumberSetting explodeDelay = new NumberSetting("Explode Delay", this, 0, 0, 10, 1);
    private final NumberSetting explodeSlot = new NumberSetting("Explode Slot", this, 1, 1, 9, 1);
    private int switchClock;
    private int glowstoneClock;
    private int explodeClock;

    public AnchorMacro() {
        super("Anchor Macro", "Automatically explodes Anchors you place", 0, Category.COMBAT);
        addSettings(switchDelay, glowstoneDelay, explodeDelay, explodeSlot);
    }

    public void reset() {
        switchClock = 0;
        glowstoneClock = 0;
        explodeClock = 0;
    }

    @Override
    public void onEnable() {
        reset();
    }

    private int getSlot() {
        return explodeSlot.getIntValue() - 1;
    }

    @EventHandler
    public void onPlayerTick(PlayerTickEvent event) {
        if (KeyUtils.isKeyPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
            if (mc.hitResult instanceof BlockHitResult hit && BlockUtils.isBlock(Blocks.RESPAWN_ANCHOR, hit.getBlockPos())) {
                if (hit.getType() == HitResult.Type.MISS)
                    return;

                mc.options.keyUse.setDown(false);

                if (!AnchorBlockUtils.isAnchorCharged(hit.getBlockPos())) {
                    if (!mc.player.getMainHandItem().is(Items.GLOWSTONE)) {
                        if (switchClock != switchDelay.getIntValue()) {
                            switchClock++;
                            return;
                        }

                        switchClock = 0;

                        InventoryUtils.selectItem(Items.GLOWSTONE);
                    }

                    if (mc.player.getMainHandItem().is(Items.GLOWSTONE)) {
                        if (glowstoneClock != glowstoneDelay.getIntValue()) {
                            glowstoneClock++;
                            return;
                        }

                        glowstoneClock = 0;

                        InteractionResult result = mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit);
                        if (result.consumesAction() && result.shouldSwing())
                            mc.player.swing(InteractionHand.MAIN_HAND);
                    }
                }

                if (AnchorBlockUtils.isAnchorCharged(hit.getBlockPos())) {
                    int slot = getSlot();

                    if (mc.player.getInventory().selected != slot) {
                        if (switchClock != switchDelay.getIntValue()) {
                            switchClock++;
                            return;
                        }

                        switchClock = 0;

                        mc.player.getInventory().selected = slot;
                    }

                    if (mc.player.getInventory().selected == slot) {
                        if (explodeClock != explodeDelay.getIntValue()) {
                            explodeClock++;
                            return;
                        }

                        explodeClock = 0;

                        InteractionResult result = mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit);
                        if (result.consumesAction() && result.shouldSwing())
                            mc.player.swing(InteractionHand.MAIN_HAND);
                    }
                }
            }
        }
    }
}
