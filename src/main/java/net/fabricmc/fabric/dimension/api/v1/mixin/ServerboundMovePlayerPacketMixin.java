package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.fabricmc.fabric.dimension.api.v1.imixin.IServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("all")
@Mixin(ServerboundMovePlayerPacket.class)
public abstract class ServerboundMovePlayerPacketMixin implements IServerboundMovePlayerPacket {
    @Mutable @Shadow @Final protected float yRot;
    @Mutable @Shadow @Final protected float xRot;
    @Mutable @Shadow @Final protected boolean hasRot;

    @Override
    public void setYRot(float yRot) {
        this.yRot = yRot;
    }

    @Override
    public void setXRot(float xRot) {
        this.xRot = xRot;
    }

    @Override
    public void setHasRot(boolean hasRot) {
        this.hasRot = hasRot;
    }
}
