package net.fabricmc.fabric.dimension.api.v1.orbit;

/**
 * Thrown when an {@link IEventBus} can't find a registered lambda factory to use.
 */
@SuppressWarnings("all")
public class NoLambdaFactoryException extends RuntimeException {
    public NoLambdaFactoryException(Class<?> klass) {
        super("No registered lambda listener for '" + klass.getName() + "'.");
    }
}
