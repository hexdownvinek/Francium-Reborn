package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.module.modules.combat.CwCrystal;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("all")
@Mixin(EndCrystalItem.class)
public class EndCrystalItemMixin {
    @Redirect(method = "useOn",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V",
                       opcode = Opcodes.INVOKEVIRTUAL))
    private void onItemShrink(ItemStack instance, int i) {
        if (Client.INSTANCE != null) {
            if (!Client.moduleManager().getModule(CwCrystal.class).noItemShrink.isEnabled())
                instance.shrink(i);
        }
    }
}
