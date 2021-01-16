package org.apache.logging.log4j.core.util;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public interface ShutdownCallbackRegistry {
   String SHUTDOWN_CALLBACK_REGISTRY = "log4j.shutdownCallbackRegistry";
   String SHUTDOWN_HOOK_ENABLED = "log4j.shutdownHookEnabled";
   Marker SHUTDOWN_HOOK_MARKER = MarkerManager.getMarker("SHUTDOWN HOOK");

   Cancellable addShutdownCallback(Runnable var1);
}
