package io.netty.util;

import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class ReferenceCountUtil {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountUtil.class);

   public static <T> T retain(T var0) {
      return var0 instanceof ReferenceCounted ? ((ReferenceCounted)var0).retain() : var0;
   }

   public static <T> T retain(T var0, int var1) {
      return var0 instanceof ReferenceCounted ? ((ReferenceCounted)var0).retain(var1) : var0;
   }

   public static <T> T touch(T var0) {
      return var0 instanceof ReferenceCounted ? ((ReferenceCounted)var0).touch() : var0;
   }

   public static <T> T touch(T var0, Object var1) {
      return var0 instanceof ReferenceCounted ? ((ReferenceCounted)var0).touch(var1) : var0;
   }

   public static boolean release(Object var0) {
      return var0 instanceof ReferenceCounted ? ((ReferenceCounted)var0).release() : false;
   }

   public static boolean release(Object var0, int var1) {
      return var0 instanceof ReferenceCounted ? ((ReferenceCounted)var0).release(var1) : false;
   }

   public static void safeRelease(Object var0) {
      try {
         release(var0);
      } catch (Throwable var2) {
         logger.warn("Failed to release a message: {}", var0, var2);
      }

   }

   public static void safeRelease(Object var0, int var1) {
      try {
         release(var0, var1);
      } catch (Throwable var3) {
         if (logger.isWarnEnabled()) {
            logger.warn("Failed to release a message: {} (decrement: {})", var0, var1, var3);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public static <T> T releaseLater(T var0) {
      return releaseLater(var0, 1);
   }

   /** @deprecated */
   @Deprecated
   public static <T> T releaseLater(T var0, int var1) {
      if (var0 instanceof ReferenceCounted) {
         ThreadDeathWatcher.watch(Thread.currentThread(), new ReferenceCountUtil.ReleasingTask((ReferenceCounted)var0, var1));
      }

      return var0;
   }

   public static int refCnt(Object var0) {
      return var0 instanceof ReferenceCounted ? ((ReferenceCounted)var0).refCnt() : -1;
   }

   private ReferenceCountUtil() {
      super();
   }

   static {
      ResourceLeakDetector.addExclusions(ReferenceCountUtil.class, "touch");
   }

   private static final class ReleasingTask implements Runnable {
      private final ReferenceCounted obj;
      private final int decrement;

      ReleasingTask(ReferenceCounted var1, int var2) {
         super();
         this.obj = var1;
         this.decrement = var2;
      }

      public void run() {
         try {
            if (!this.obj.release(this.decrement)) {
               ReferenceCountUtil.logger.warn("Non-zero refCnt: {}", (Object)this);
            } else {
               ReferenceCountUtil.logger.debug("Released: {}", (Object)this);
            }
         } catch (Exception var2) {
            ReferenceCountUtil.logger.warn("Failed to release an object: {}", this.obj, var2);
         }

      }

      public String toString() {
         return StringUtil.simpleClassName((Object)this.obj) + ".release(" + this.decrement + ") refCnt: " + this.obj.refCnt();
      }
   }
}
