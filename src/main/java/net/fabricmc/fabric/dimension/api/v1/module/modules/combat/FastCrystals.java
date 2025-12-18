package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.event.events.AttackEvent;
import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.ModeSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.util.KeyUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("unused")
public class FastCrystals extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", this, "Vanilla", "Vanilla", "RAGE");
    private final NumberSetting expand = new NumberSetting("Expand", this, 0d, 0d, 5d, 0.1d);
    private final BooleanSetting breakk = new BooleanSetting("Break", this, true);
    private final BooleanSetting place = new BooleanSetting("Place", this, true);
    private final BooleanSetting esentials = new BooleanSetting("Esentials", this, true);
    Entity target;
    BlockHitResult blockHit;

    public FastCrystals() {
        super("FastCrystals", "Fast crystals", 0, Category.COMBAT);
        addSettings(mode, expand, breakk, place, esentials);
    }

    @EventHandler
    private void onPlayerTick(PlayerTickEvent event) {

        //if (mc.hitResult instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof EndCrystal){

        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) mc.hitResult;
            Entity targetedEntity = entityHitResult.getEntity();

            if (targetedEntity instanceof EndCrystal) {
                assert mc.player != null;
                ItemStack mainHandItem = mc.player.getMainHandItem();

                if (mainHandItem.getItem() == Items.END_CRYSTAL) {
                    //remove(targetedEntity); // Assuming this should be targetedEntity instead of target
                }
            }
        }
    }


    @EventHandler
    private void onAttack(AttackEvent event) {
        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) mc.hitResult;
            Entity targetedEntity = entityHitResult.getEntity();

            if (KeyUtils.isKeyNotPressed(GLFW.GLFW_MOUSE_BUTTON_1) && targetedEntity instanceof EndCrystal)
                remove(targetedEntity);
                System.out.println("KILLED");
        }
    }


        public void remove(Entity entity){
            entity.remove(Entity.RemovalReason.KILLED);
            entity.setRemoved(Entity.RemovalReason.KILLED);
            entity.onClientRemoval();
            entity.kill();

    }
}

