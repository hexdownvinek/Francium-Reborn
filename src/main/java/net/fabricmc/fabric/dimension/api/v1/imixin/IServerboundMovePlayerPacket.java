package net.fabricmc.fabric.dimension.api.v1.imixin;

@SuppressWarnings("all")
public interface IServerboundMovePlayerPacket {
    public void setYRot(float yRot);
    public void setXRot(float xRot);
    public void setHasRot(boolean hasRot);
}
