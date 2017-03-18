package org.minig.server.util;

/**
 * @author Kamill Sokol
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
        // prevent instantiation
    }

    @FunctionalInterface
    public interface SupplierWithExceptions<T, E extends Exception> {
        T get() throws E;
    }

    public static <R, E extends Exception> R rethrowCheckedAsUnchecked(SupplierWithExceptions<R, E> supplier) {
        try {
            return supplier.get();
        }
        catch (Exception exception) {
            throwAsUnchecked(exception);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
    }
}
