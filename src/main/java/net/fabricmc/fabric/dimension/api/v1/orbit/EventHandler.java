package net.fabricmc.fabric.dimension.api.v1.orbit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark listeners.
 */
@SuppressWarnings("all")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {
    int priority() default EventPriority.MEDIUM;
}
