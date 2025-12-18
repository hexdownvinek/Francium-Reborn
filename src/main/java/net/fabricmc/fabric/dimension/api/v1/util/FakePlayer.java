package net.fabricmc.fabric.dimension.api.v1.util;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.module.modules.player.FakePlayerModule;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.UUID;

import static net.fabricmc.fabric.dimension.api.v1.Client.mc;

@SuppressWarnings("all")
public class FakePlayer extends RemotePlayer {

    private final boolean infHealth;

    public FakePlayer(Player player, boolean infHealth, float health, boolean copyInventory) {
        super(mc.level, new GameProfile(UUID.randomUUID(), Client.name));
        this.setInvulnerable(false);
        this.setNoGravity(false);
        this.setOnGround(true);
        this.noPhysics = false;
        this.infHealth = infHealth;

        copyPosition(player);
        setOldPosAndRot();

        this.yBodyRot = player.yBodyRot;
        this.yBodyRotO = player.yBodyRotO;
        this.yHeadRot = player.yHeadRot;
        this.yHeadRotO = player.yHeadRot;

        this.xCloak = getX();
        this.xCloakO = getX();
        this.yCloak = getY();
        this.yCloakO = getY();
        this.zCloak = getZ();
        this.zCloakO = getZ();

        Byte playerModel = player.getEntityData().get(Player.DATA_PLAYER_MODE_CUSTOMISATION);
        entityData.set(Player.DATA_PLAYER_MODE_CUSTOMISATION, playerModel);

        getAttributes().assignValues(player.getAttributes());
        setPose(player.getPose());

        setHealth(health);

        if (health > 20)
            setAbsorptionAmount(health - 20);

        if (copyInventory)
            getInventory().replaceWith(player.getInventory());
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (damageSource.is(DamageTypeTags.IS_FIRE) && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return false;
        } else {
            if (this.isSleeping() && !this.level.isClientSide) {
                this.stopSleeping();
            }

            this.noActionTime = 0;
            float g = f;
            boolean bl = false;
            float h = 0.0F;
            if (f > 0.0F && this.isDamageSourceBlocked(damageSource)) {
                this.hurtCurrentlyUsedShield(f);
                h = f;
                f = 0.0F;
                if (!damageSource.is(DamageTypeTags.IS_PROJECTILE)) {
                    Entity entity = damageSource.getDirectEntity();
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity)entity;
                        this.blockUsingShield(livingEntity);
                    }
                }

                bl = true;
            }

            if (damageSource.is(DamageTypeTags.IS_FREEZING) && this.getType().is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                f *= 5.0F;
            }

            this.walkAnimation.setSpeed(1.5F);
            boolean bl2 = true;
            if ((float)this.invulnerableTime > 10.0F && !damageSource.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
                if (f <= this.lastHurt) {
                    return false;
                }
                this.lastHurt = f;
                bl2 = false;
            } else {
                this.lastHurt = f;
                this.invulnerableTime = 20;
                if (!infHealth) this.actuallyHurt(damageSource, f);
                this.hurtDuration = 10;
                this.hurtTime = this.hurtDuration;
            }

            if (damageSource.is(DamageTypeTags.DAMAGES_HELMET) && !this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                this.hurtHelmet(damageSource, f);
                f *= 0.75F;
            }

            Entity entity2 = damageSource.getEntity();
            if (entity2 != null) {
                if (entity2 instanceof LivingEntity) {
                    LivingEntity livingEntity2 = (LivingEntity)entity2;
                    if (!damageSource.is(DamageTypeTags.NO_ANGER)) {
                        this.setLastHurtByMob(livingEntity2);
                    }
                }

                if (entity2 instanceof Player) {
                    Player player = (Player)entity2;
                    this.lastHurtByPlayerTime = 100;
                    this.lastHurtByPlayer = player;
                }
            }

            if (bl2) {
                if (bl) {
                    this.level.broadcastEntityEvent(this, (byte)29);
                } else {
                    this.level.broadcastDamageEvent(this, damageSource);
                }

                if (!damageSource.is(DamageTypeTags.NO_IMPACT) && (!bl || f > 0.0F)) {
                    this.markHurt();
                }

                if (entity2 != null && !damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
                    double d = entity2.getX() - this.getX();

                    double e;
                    for (e = entity2.getZ() - this.getZ(); d * d + e * e < 1.0E-4; e = (Math.random() - Math.random()) * 0.01) {
                        d = (Math.random() - Math.random()) * 0.01;
                    }

                    this.knockback(0.4000000059604645, d, e);
                    if (!bl) {
                        this.indicateDamage(d, e);
                    }
                }
            }

            if (this.isDeadOrDying()) {
                if (!checkTotemDeathProtection(damageSource)) {
                    SoundEvent soundEvent = this.getDeathSound();
                    if (bl2 && soundEvent != null) {
                        this.playSound(soundEvent, this.getSoundVolume(), this.getVoicePitch());
                    }

                    Client.moduleManager().getModule(FakePlayerModule.class).disable();
                }
            } else if (bl2) {
                this.playHurtSound(damageSource);
            }

            return !bl || f > 0.0F;
        }
    }

    private boolean checkTotemDeathProtection(DamageSource damageSource) {
        if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        } else {
            ItemStack itemStack = null;
            InteractionHand[] var4 = InteractionHand.values();
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                InteractionHand interactionHand = var4[var6];
                ItemStack itemStack2 = this.getItemInHand(interactionHand);
                if (itemStack2.is(Items.TOTEM_OF_UNDYING)) {
                    itemStack = itemStack2.copy();
                    break;
                }
            }

            if (itemStack != null) {
                this.setHealth(1.0F);
                this.removeAllEffects();
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                this.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                this.level.broadcastEntityEvent(this, (byte) 35);
            }

            return itemStack != null;
        }
    }

    public void spawn() {
        unsetRemoved();
        mc.level.addPlayer(getId(), this);
    }

    public void despawn() {
        mc.level.removeEntity(getId(), RemovalReason.DISCARDED);
        setRemoved(RemovalReason.DISCARDED);
    }
}
