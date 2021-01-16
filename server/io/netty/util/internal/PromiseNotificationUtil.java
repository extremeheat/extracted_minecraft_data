package io.netty.util.internal;

import io.netty.util.concurrent.Promise;
import io.netty.util.internal.logging.InternalLogger;

public final class PromiseNotificationUtil {
   private PromiseNotificationUtil() {
      super();
   }

   public static void tryCancel(Promise<?> var0, InternalLogger var1) {
      if (!var0.cancel(false) && var1 != null) {
         Throwable var2 = var0.cause();
         if (var2 == null) {
            var1.warn("Failed to cancel promise because it has succeeded already: {}", (Object)var0);
         } else {
            var1.warn("Failed to cancel promise because it has failed already: {}, unnotified cause:", var0, var2);
         }
      }

   }

   public static <V> void trySuccess(Promise<? super V> var0, V var1, InternalLogger var2) {
      if (!var0.trySuccess(var1) && var2 != null) {
         Throwable var3 = var0.cause();
         if (var3 == null) {
            var2.warn("Failed to mark a promise as success because it has succeeded already: {}", (Object)var0);
         } else {
            var2.warn("Failed to mark a promise as success because it has failed already: {}, unnotified cause:", var0, var3);
         }
      }

   }

   public static void tryFailure(Promise<?> var0, Throwable var1, InternalLogger var2) {
      if (!var0.tryFailure(var1) && var2 != null) {
         Throwable var3 = var0.cause();
         if (var3 == null) {
            var2.warn("Failed to mark a promise as failure because it has succeeded already: {}", var0, var1);
         } else {
            var2.warn("Failed to mark a promise as failure because it has failed already: {}, unnotified cause: {}", var0, ThrowableUtil.stackTraceToString(var3), var1);
         }
      }

   }
}
