package net.fabricmc.fabric.dimension.api.v1.module.modules.combat;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.event.events.PlayerTickEvent;
import net.fabricmc.fabric.dimension.api.v1.module.Module;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.BooleanSetting;
import net.fabricmc.fabric.dimension.api.v1.module.setting.settings.NumberSetting;
import net.fabricmc.fabric.dimension.api.v1.orbit.EventHandler;
import net.fabricmc.fabric.dimension.api.v1.util.BlockUtils;
import net.fabricmc.fabric.dimension.api.v1.util.CrystalUtils;
import net.fabricmc.fabric.dimension.api.v1.util.DamageUtils;
import net.fabricmc.fabric.dimension.api.v1.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("all")
public class AutoDoubleHand extends Module {

    private final BooleanSetting dhandafterpop = new BooleanSetting("Double hand After Pop", this, true);
    private final BooleanSetting dhandAtHealth = new BooleanSetting("Double Hand At Health", this, false);
    private final NumberSetting dHandHealth = new NumberSetting("Double Hand Health", this, 1d, 1d, 10d, 1d);
    private final BooleanSetting checkPlayersAround = new BooleanSetting("Check Players", this, false);
    private final NumberSetting distance = new NumberSetting("Distance", this, 6d, 1d, 10d, 1d);
    private final BooleanSetting predictCrystals = new BooleanSetting("Predict Crystals", this, false);
    private final BooleanSetting checkAim = new BooleanSetting("Check Aim", this, false);
    private final BooleanSetting checkItems = new BooleanSetting("Check Items", this, false);
    private final NumberSetting height = new NumberSetting("Height", this, 1d, 1d, 10d, 1d);
    private boolean BelowHearts;
    private boolean noOffhandTotem;

    public AutoDoubleHand() {
     super("Auto DoubleHand", "Automatically Doublehands when needed", 0, Category.COMBAT);
     addSettings(dhandafterpop, dhandAtHealth, dHandHealth, checkPlayersAround, distance, predictCrystals, checkAim, checkItems, height);
    }

    public void reset() {
        BelowHearts = false;
        noOffhandTotem = false;
    }

    @Override
    public void onEnable() {
        reset();
    }

    private List<EndCrystal> getNearByCrystals() {
        BlockPos pos = mc.player.getOnPos();
        return mc.level.getEntitiesOfClass(EndCrystal.class, new AABB(pos.offset(-6, -6, -6), pos.offset(6, 6, 6)), a -> true);
    }

    @EventHandler
    public void onPlayerTick(PlayerTickEvent event) {
        Inventory inv = mc.player.getInventory();

        if (inv.offhand.get(0).getItem() != Items.TOTEM_OF_UNDYING && dhandafterpop.isEnabled() && !this.noOffhandTotem) {
            this.noOffhandTotem = true;
            InventoryUtils.selectItem(Items.TOTEM_OF_UNDYING);
            return;
        }

        if (inv.offhand.get(0).getItem() == Items.TOTEM_OF_UNDYING) {
            this.noOffhandTotem = false;
        }

        if (mc.player.getHealth() <= this.dHandHealth.getValue() && dhandAtHealth.isEnabled() && !this.BelowHearts) {
            this.BelowHearts = true;
            InventoryUtils.selectItem(Items.TOTEM_OF_UNDYING);
            return;

        }

        if (Client.mc.player.getHealth() > this.dHandHealth.getValue()) {
            this.BelowHearts = false;
        }

        if (Client.mc.player.getHealth() > 19.0f) {
            return;
        }

        double distanceSq = Math.pow(distance.getValue(), 2);
        if (this.checkPlayersAround.isEnabled() && mc.level.players().parallelStream().filter(e -> e != Client.mc.player).noneMatch(player -> Client.mc.player.distanceToSqr(player) <= distanceSq)) {
            return;
        }

        final double activatesAboveV = this.height.getValue();
        for (int f = (int)Math.floor(activatesAboveV), i = 1; i <= f; ++i) {
            if (BlockUtils.hasBlock(mc.player.getOnPos().offset(0, -i, 0)))
                return;
        }

        List<EndCrystal> crystals = this.getNearByCrystals();
        ArrayList<Vec3> crystalsPos = new ArrayList<>();
        crystals.forEach(e -> crystalsPos.add(e.getOnPos().getCenter()));

        if (this.predictCrystals.isEnabled()) {
            Stream<BlockPos> stream = BlockUtils.getAllInBoxStream(mc.player.getOnPos().offset(-6, -8, -6), mc.player.getOnPos().offset(6, 2, 6)).filter(e -> BlockUtils.isBlock(Blocks.OBSIDIAN, e) || BlockUtils.isBlock(Blocks.BEDROCK, e)).filter(CrystalUtils::canPlaceCrystalClient);
            if (this.checkAim.isEnabled()) {
                if (this.checkItems.isEnabled()) {
                    stream = stream.filter(this::arePeopleAimingAtBlockAndHoldingCrystals);
                }
                else {
                    stream = stream.filter(this::arePeopleAimingAtBlock);
                }
            }
            stream.forEachOrdered(e -> {
                Vec3 centeredPos = e.getCenter();
                crystalsPos.add(Vec3.atBottomCenterOf(new Vec3i((int) centeredPos.x, (int) centeredPos.y, (int) centeredPos.z)).add(0.0, 1.0, 0.0));
            });
        }

        for (Vec3 pos : crystalsPos) {
            double damage = DamageUtils.crystalDamage(mc.player, pos, true, null, false);
            if (damage >= mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                InventoryUtils.selectItem(Items.TOTEM_OF_UNDYING);
                break;
            }
        }
    }

    private boolean arePeopleAimingAtBlock(BlockPos block) {
        return mc.level.players().parallelStream().filter(e -> e != mc.player).anyMatch(e -> {
            Vec3 eyesPos = e.getEyePosition();
            BlockHitResult hitResult = mc.level.clip(new ClipContext(eyesPos, eyesPos.add(e.getEyePosition().multiply(4.5, 4.5, 4.5)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, e));
            return hitResult.getBlockPos().equals(block);
        });
    }

    private boolean arePeopleAimingAtBlockAndHoldingCrystals(BlockPos block) {
        return mc.level.players().parallelStream().filter(e -> e != mc.player).filter(e -> e.isHolding(Items.END_CRYSTAL)).anyMatch(e -> {
            Vec3 eyesPos = e.getEyePosition();
            BlockHitResult hitResult = mc.level.clip(new ClipContext(eyesPos, eyesPos.add(e.getEyePosition().multiply(4.5, 4.5, 4.5)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, e));
            return hitResult.getBlockPos().equals(block);
        });
    }
}