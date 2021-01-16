package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class UnaryPromiseNotifier<T> implements FutureListener<T> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(UnaryPromiseNotifier.class);
   private final Promise<? super T> promise;

   public UnaryPromiseNotifier(Promise<? super T> var1) {
      super();
      this.promise = (Promise)ObjectUtil.checkNotNull(var1, "promise");
   }

   public void operationComplete(Future<T> var1) throws Exception {
      cascadeTo(var1, this.promise);
   }

   public static <X> void cascadeTo(Future<X> var0, Promise<? super X> var1) {
      if (var0.isSuccess()) {
         if (!var1.trySuccess(var0.getNow())) {
            logger.warn("Failed to mark a promise as success because it is done already: {}", (Object)var1);
         }
      } else if (var0.isCancelled()) {
         if (!var1.cancel(false)) {
            logger.warn("Failed to cancel a promise because it is done already: {}", (Object)var1);
         }
      } else if (!var1.tryFailure(var0.cause())) {
         logger.warn("Failed to mark a promise as failure because it's done already: {}", var1, var0.cause());
      }

   }
}
