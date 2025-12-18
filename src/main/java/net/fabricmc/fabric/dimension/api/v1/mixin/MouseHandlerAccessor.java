package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("all")
@Mixin(MouseHandler.class)
public interface MouseHandlerAccessor {
    @Invoker("onPress")
    void press(long windowHandle, int keyCode, int action, int mods);
}
