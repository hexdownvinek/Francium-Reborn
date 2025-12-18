package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.module.modules.combat.Reach;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings("all")
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @ModifyConstant(method = "pick", constant = @Constant(doubleValue = 3))
    private double updateTargetedEntityModifySurvivalReach(double d) {
        if (Client.INSTANCE != null) {
            Reach reach = Client.INSTANCE.moduleManager.getModule(Reach.class);
            if (reach.isEnabled())
                return reach.getDistance();
        }

        return d;
    }

    @ModifyConstant(method = "pick", constant = @Constant(doubleValue = 9))
    private double updateTargetedEntityModifySquaredMaxReach(double d) {
        if (Client.INSTANCE != null) {
            Reach reach = Client.INSTANCE.moduleManager.getModule(Reach.class);
            if (reach.isEnabled())
                return Math.pow(reach.getDistance(), 2);
        }

        return d;
    }

}
