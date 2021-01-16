package io.netty.util.internal.shaded.org.jctools.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class UnsafeAccess {
   public static final boolean SUPPORTS_GET_AND_SET;
   public static final Unsafe UNSAFE;

   public UnsafeAccess() {
      super();
   }

   static {
      Unsafe var0;
      try {
         Field var1 = Unsafe.class.getDeclaredField("theUnsafe");
         var1.setAccessible(true);
         var0 = (Unsafe)var1.get((Object)null);
      } catch (Exception var5) {
         try {
            Constructor var2 = Unsafe.class.getDeclaredConstructor();
            var2.setAccessible(true);
            var0 = (Unsafe)var2.newInstance();
         } catch (Exception var4) {
            SUPPORTS_GET_AND_SET = false;
            throw new RuntimeException(var4);
         }
      }

      boolean var6 = false;

      try {
         Unsafe.class.getMethod("getAndSetObject", Object.class, Long.TYPE, Object.class);
         var6 = true;
      } catch (Exception var3) {
      }

      UNSAFE = var0;
      SUPPORTS_GET_AND_SET = var6;
   }
}
