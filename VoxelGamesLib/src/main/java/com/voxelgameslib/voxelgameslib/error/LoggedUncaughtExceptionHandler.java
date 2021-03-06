package com.voxelgameslib.voxelgameslib.error;

import com.bugsnag.Bugsnag;
import com.bugsnag.Severity;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import javax.annotation.Nonnull;

public class LoggedUncaughtExceptionHandler implements UncaughtExceptionHandler {

    private static final Logger log = Logger.getLogger(LoggedUncaughtExceptionHandler.class.getName());
    private final UncaughtExceptionHandler originalHandler;
    private final WeakHashMap<Bugsnag, Boolean> clientMap = new WeakHashMap<Bugsnag, Boolean>();

    LoggedUncaughtExceptionHandler(@Nonnull UncaughtExceptionHandler originalHandler) {
        this.originalHandler = originalHandler;
    }

    static void enable(@Nonnull Bugsnag bugsnag) {
        UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();

        // Find or create the Bugsnag ExceptionHandler
        LoggedUncaughtExceptionHandler bugsnagHandler;
        if (currentHandler instanceof LoggedUncaughtExceptionHandler) {
            bugsnagHandler = (LoggedUncaughtExceptionHandler) currentHandler;
        } else {
            bugsnagHandler = new LoggedUncaughtExceptionHandler(currentHandler);
            Thread.setDefaultUncaughtExceptionHandler(bugsnagHandler);
        }

        // Subscribe this bugsnag to uncaught exceptions
        bugsnagHandler.clientMap.put(bugsnag, true);
    }

    static void disable(@Nonnull Bugsnag bugsnag) {
        // Find the Bugsnag ExceptionHandler
        UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHandler instanceof LoggedUncaughtExceptionHandler) {
            // Unsubscribe this bugsnag from uncaught exceptions
            LoggedUncaughtExceptionHandler bugsnagHandler = (LoggedUncaughtExceptionHandler) currentHandler;
            bugsnagHandler.clientMap.remove(bugsnag);

            // Remove the Bugsnag ExceptionHandler if no clients are subscribed
            if (bugsnagHandler.clientMap.size() == 0) {
                Thread.setDefaultUncaughtExceptionHandler(bugsnagHandler.originalHandler);
            }
        }
    }

    @Override
    public void uncaughtException(@Nonnull Thread thread, @Nonnull Throwable throwable) {
        // Notify any subscribed clients of the uncaught exception
        for (Bugsnag bugsnag : clientMap.keySet()) {
            bugsnag.notify(throwable, Severity.ERROR);
        }
        log.info("Caught exception");

        // Pass exception on to original exception handler
        if (originalHandler != null) {
            originalHandler.uncaughtException(thread, throwable);
        } else {
            // Emulate the java exception print style
            System.err.printf("Exception in thread \"%s\" ", thread.getName());
            throwable.printStackTrace(System.err);
        }
    }
}