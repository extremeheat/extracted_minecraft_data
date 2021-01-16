package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

final class CleanerJava9 implements Cleaner {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CleanerJava9.class);
   private static final Method INVOKE_CLEANER;

   CleanerJava9() {
      super();
   }

   static boolean isSupported() {
      return INVOKE_CLEANER != null;
   }

   public void freeDirectBuffer(ByteBuffer var1) {
      try {
         INVOKE_CLEANER.invoke(PlatformDependent0.UNSAFE, var1);
      } catch (Throwable var3) {
         PlatformDependent0.throwException(var3);
      }

   }

   static {
      Method var0;
      Object var1;
      if (PlatformDependent0.hasUnsafe()) {
         ByteBuffer var2 = ByteBuffer.allocateDirect(1);

         Object var3;
         try {
            Method var4 = PlatformDependent0.UNSAFE.getClass().getDeclaredMethod("invokeCleaner", ByteBuffer.class);
            var4.invoke(PlatformDependent0.UNSAFE, var2);
            var3 = var4;
         } catch (NoSuchMethodException var5) {
            var3 = var5;
         } catch (InvocationTargetException var6) {
            var3 = var6;
         } catch (IllegalAccessException var7) {
            var3 = var7;
         }

         if (var3 instanceof Throwable) {
            var0 = null;
            var1 = (Throwable)var3;
         } else {
            var0 = (Method)var3;
            var1 = null;
         }
      } else {
         var0 = null;
         var1 = new UnsupportedOperationException("sun.misc.Unsafe unavailable");
      }

      if (var1 == null) {
         logger.debug("java.nio.ByteBuffer.cleaner(): available");
      } else {
         logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", (Throwable)var1);
      }

      INVOKE_CLEANER = var0;
   }
}
