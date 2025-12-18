package net.fabricmc.fabric.dimension.api.v1.mixin;

import net.fabricmc.fabric.dimension.api.v1.Client;
import net.fabricmc.fabric.dimension.api.v1.event.events.EntityEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
@SuppressWarnings("all")
public class ClientLevelMixin {

    @Inject(method = "addEntity", at = @At("TAIL"))
    private void onAddEntity(int entityId, Entity entity, CallbackInfo ci) {
        if (Client.EVENTBUS != null)
            if (entity != null) Client.EVENTBUS.post(EntityEvent.Spawn.get(entity));
    }

    @Inject(method = "removeEntity", at = @At("TAIL"))
    private void onRemoveEntity(int entityId, Entity.RemovalReason removalReason, CallbackInfo ci) {
        if (Client.EVENTBUS != null) {
            Entity entity = Client.mc.level.getEntity(entityId);
            if (entity != null)
                Client.EVENTBUS.post(EntityEvent.Spawn.get(entity));
        }
    }

}
