package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.module.modules.render.NoBounce;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("all")
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract boolean is(Item item);

    @Inject(method = "getPopTime", at = @At("HEAD"), cancellable = true)
    private void onGetPopTime(CallbackInfoReturnable<Integer> cir) {
        if (Client.INSTANCE != null) {
            NoBounce noBounce = Client.moduleManager().getModule(NoBounce.class);
            if (noBounce.isEnabled()) {
                switch (noBounce.mode.getMode()) {
                    case "Only Crystals" -> {
                        if (this.is(Items.END_CRYSTAL))
                            cir.setReturnValue(0);
                    }
                    default -> {
                        cir.setReturnValue(0);
                    }
                }
            }
        }
    }
}

