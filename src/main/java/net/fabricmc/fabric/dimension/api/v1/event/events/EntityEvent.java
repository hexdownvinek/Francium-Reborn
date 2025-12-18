package net.fabricmc.fabric.dimension.api.v1.event.events;

import net.minecraft.world.entity.Entity;

@SuppressWarnings("all")
public class EntityEvent {

    public static class Spawn extends EntityEvent {
        private static final Spawn INSTANCE = new Spawn();

        public Entity entity;

        public static Spawn get(Entity entity) {
            INSTANCE.entity = entity;
            return INSTANCE;
        }
    }

    public static class Despawn extends EntityEvent {
        private static final Despawn INSTANCE = new Despawn();

        public Entity entity;

        public static Despawn get(Entity entity) {
            INSTANCE.entity = entity;
            return INSTANCE;
        }
    }

}
