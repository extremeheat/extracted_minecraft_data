package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class PromiseNotifier<V, F extends Future<V>> implements GenericFutureListener<F> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PromiseNotifier.class);
   private final Promise<? super V>[] promises;
   private final boolean logNotifyFailure;

   @SafeVarargs
   public PromiseNotifier(Promise<? super V>... var1) {
      this(true, var1);
   }

   @SafeVarargs
   public PromiseNotifier(boolean var1, Promise<? super V>... var2) {
      super();
      ObjectUtil.checkNotNull(var2, "promises");
      Promise[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Promise var6 = var3[var5];
         if (var6 == null) {
            throw new IllegalArgumentException("promises contains null Promise");
         }
      }

      this.promises = (Promise[])var2.clone();
      this.logNotifyFailure = var1;
   }

   public void operationComplete(F var1) throws Exception {
      InternalLogger var2 = this.logNotifyFailure ? logger : null;
      Promise[] var4;
      int var5;
      int var6;
      Promise var7;
      if (var1.isSuccess()) {
         Object var3 = var1.get();
         var4 = this.promises;
         var5 = var4.length;

         for(var6 = 0; var6 < var5; ++var6) {
            var7 = var4[var6];
            PromiseNotificationUtil.trySuccess(var7, var3, var2);
         }
      } else if (var1.isCancelled()) {
         Promise[] var8 = this.promises;
         int var10 = var8.length;

         for(var5 = 0; var5 < var10; ++var5) {
            Promise var11 = var8[var5];
            PromiseNotificationUtil.tryCancel(var11, var2);
         }
      } else {
         Throwable var9 = var1.cause();
         var4 = this.promises;
         var5 = var4.length;

         for(var6 = 0; var6 < var5; ++var6) {
            var7 = var4[var6];
            PromiseNotificationUtil.tryFailure(var7, var9, var2);
         }
      }

   }
}
