package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("all")
@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Invoker("startAttack")
    boolean doAttack();

    @Invoker("startUseItem")
    void doItemUse();

    @Accessor
    MouseHandler getMouseHandler();

    @Accessor
    int getMissTime();

    @Accessor
    void setMissTime(int missTime);
}
