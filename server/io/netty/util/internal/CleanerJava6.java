package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

final class CleanerJava6 implements Cleaner {
   private static final long CLEANER_FIELD_OFFSET;
   private static final Method CLEAN_METHOD;
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CleanerJava6.class);

   CleanerJava6() {
      super();
   }

   static boolean isSupported() {
      return CLEANER_FIELD_OFFSET != -1L;
   }

   public void freeDirectBuffer(ByteBuffer var1) {
      if (var1.isDirect()) {
         try {
            Object var2 = PlatformDependent0.getObject(var1, CLEANER_FIELD_OFFSET);
            if (var2 != null) {
               CLEAN_METHOD.invoke(var2);
            }
         } catch (Throwable var3) {
            PlatformDependent0.throwException(var3);
         }

      }
   }

   static {
      long var0 = -1L;
      Method var2 = null;
      Object var3 = null;
      if (PlatformDependent0.hasUnsafe()) {
         ByteBuffer var4 = ByteBuffer.allocateDirect(1);

         try {
            Field var5 = var4.getClass().getDeclaredField("cleaner");
            var0 = PlatformDependent0.objectFieldOffset(var5);
            Object var6 = PlatformDependent0.getObject(var4, var0);
            var2 = var6.getClass().getDeclaredMethod("clean");
            var2.invoke(var6);
         } catch (Throwable var7) {
            var0 = -1L;
            var2 = null;
            var3 = var7;
         }
      } else {
         var3 = new UnsupportedOperationException("sun.misc.Unsafe unavailable");
      }

      if (var3 == null) {
         logger.debug("java.nio.ByteBuffer.cleaner(): available");
      } else {
         logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", (Throwable)var3);
      }

      CLEANER_FIELD_OFFSET = var0;
      CLEAN_METHOD = var2;
   }
}
