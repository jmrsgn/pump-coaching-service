package com.johnmartin.coaching.utilities;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggerUtility {

    private LoggerUtility() {
    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    private static Object[] maskArgs(Object... args) {
        if (args == null || args.length == 0)
            return args;

        Object[] masked = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];

            if (arg == null || arg instanceof String || arg instanceof Number || arg instanceof Boolean
                || arg instanceof Enum || arg.getClass().isPrimitive()) {
                masked[i] = arg;
            } else {
                masked[i] = LogMaskUtility.mask(arg);
            }
        }

        return masked;
    }

    public static void i(Class<?> clazz, String message, Object... args) {
        Logger log = getLogger(clazz);
        if (log.isInfoEnabled()) {
            log.info(message, maskArgs(args));
        }
    }

    public static void d(Class<?> clazz, String message, Object... args) {
        Logger log = getLogger(clazz);
        if (log.isDebugEnabled()) {
            log.debug(message, maskArgs(args));
        }
    }

    public static void w(Class<?> clazz, String message, Object... args) {
        Logger log = getLogger(clazz);
        if (log.isWarnEnabled()) {
            log.warn(message, maskArgs(args));
        }
    }

    public static void e(Class<?> clazz, String message, Throwable throwable) {
        Logger log = getLogger(clazz);
        if (log.isErrorEnabled()) {
            log.error(message, throwable);
        }
    }

    public static void t(Class<?> clazz, String message, Object... args) {
        Logger log = getLogger(clazz);
        if (log.isTraceEnabled()) {
            log.trace(message, maskArgs(args));
        }
    }

    // Helper methods
    public static <T> void logItemSize(Class<?> clazz, String itemType, List<T> items) {
        d(clazz, String.format("[%s] size: [%s]", itemType, items.size()));
    }
}
