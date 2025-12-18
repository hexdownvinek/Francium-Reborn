package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.event.events.AttackEvent;
import net.fabricmc.fabric.dimension.api.v1.event.events.ItemUseEvent;
import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.KeybindSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventPriority;
import net.fabricmc.fabric.dimension.api.v1.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("all")
public class AutoHitCrystal extends Module {

    private final BooleanSetting stopOnKill = new BooleanSetting("Stop on Kill", this, true);
    private final BooleanSetting workWithTotem = new BooleanSetting("Work With Totem", this, true);
    private final NumberSetting placeDelay = new NumberSetting("Obsidian Place Delay", this, 0d, 0d, 10d, 1d);
    private final NumberSetting switchDelay = new NumberSetting("Switch Delay", this, 0d, 0d, 10d, 1d);
    private final BooleanSetting clickSimulate = new BooleanSetting("Click Simulate", this, true);
    private final KeybindSetting activateKey = new KeybindSetting("Activate Key", this, GLFW.GLFW_MOUSE_BUTTON_2);
    private int placeClock = 0;
    private int switchClock = 0;
    private boolean activated;
    private boolean crystalling;
    private boolean selectedCrystal;


    public AutoHitCrystal() {
        super("Auto Hit Crystal", "Automatically hit crystals when holding right click with a sword", 0, Category.COMBAT);
        addSettings(stopOnKill, placeDelay, switchDelay, workWithTotem, activateKey);
    }

    public void reset() {
        placeClock = placeDelay.getIntValue();
        switchClock = switchDelay.getIntValue();
        activated = false;
        crystalling = false;
        selectedCrystal = false;
    }

    @Override
    public void onEnable() {
        reset();
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerTick(PlayerTickEvent event) {
        if (KeyUtils.isKeyPressed(activateKey.getKeyCode())) {

            if (activateKey.getKeyCode() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                ItemStack mainHand = mc.player.getMainHandItem();

                if (!(mainHand.getItem() instanceof SwordItem || (workWithTotem.isEnabled() && mainHand.is(Items.TOTEM_OF_UNDYING))) && !activated)
                    return;

                activated = true;
            }

            if (!crystalling) {
                if (mc.hitResult instanceof BlockHitResult blockHit) {
                    if (blockHit.getType() == HitResult.Type.MISS)
                        return;

                    if (BlockUtils.getBlock(blockHit.getBlockPos()) instanceof SignBlock)
                        return;

                    if (!BlockUtils.isBlock(Blocks.OBSIDIAN, blockHit.getBlockPos())) {
                        mc.options.keyUse.setDown(false);

                        if (!mc.player.getMainHandItem().is(Items.OBSIDIAN)) {
                            if (switchClock > 0) {
                                switchClock--;
                                return;
                            }

                            InventoryUtils.selectItem(Items.OBSIDIAN);

                            switchClock = switchDelay.getIntValue();
                        }

                        if (mc.player.getMainHandItem().is(Items.OBSIDIAN)) {
                            if (placeClock > 0) {
                                placeClock--;
                                return;
                            }

                            if (clickSimulate.isEnabled())
                                MouseSimulation.mouseClick(GLFW.GLFW_MOUSE_BUTTON_2);

                            InteractionResult interactionResult = mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, blockHit);
                            if (interactionResult.consumesAction() && interactionResult.shouldSwing()) {
                                mc.player.swing(InteractionHand.MAIN_HAND);
                            }

                            placeClock = placeDelay.getIntValue();
                            crystalling = true;
                        }
                    }
                }
            }

            if (crystalling ||
                    ((mc.hitResult instanceof BlockHitResult blockHit && BlockUtils.isBlock(Blocks.OBSIDIAN, blockHit.getBlockPos()))
                        || mc.hitResult instanceof EntityHitResult entityHit && (entityHit.getEntity() instanceof EndCrystal || entityHit.getEntity() instanceof Slime))) {
                crystalling = true;

                if (!mc.player.getMainHandItem().is(Items.END_CRYSTAL) && !selectedCrystal) {
                    if (switchClock > 0) {
                        switchClock--;
                        return;
                    }

                    selectedCrystal = InventoryUtils.selectItem(Items.END_CRYSTAL);

                    switchClock = switchDelay.getIntValue();
                }

                if (mc.player.getMainHandItem().is(Items.END_CRYSTAL)) {
                    CwCrystal cwCrystal = Client.moduleManager().getModule(CwCrystal.class);

                    if (!cwCrystal.isEnabled())
                        cwCrystal.onPlayerTick(event);
                }
            }
        } else {
            reset();
        }
    }

    @EventHandler
    private void onItemUse(ItemUseEvent event) {
        ItemStack mainHandItem = mc.player.getMainHandItem();
        if ((mainHandItem.is(Items.END_CRYSTAL) || mainHandItem.is(Items.OBSIDIAN)) && KeyUtils.isKeyNotPressed(GLFW.GLFW_MOUSE_BUTTON_2))
            event.cancel();
    }

    @EventHandler
    private void onAttack(AttackEvent event) {
        if (mc.player.getMainHandItem().is(Items.END_CRYSTAL) && KeyUtils.isKeyNotPressed(GLFW.GLFW_MOUSE_BUTTON_1))
            event.cancel();
    }
}
