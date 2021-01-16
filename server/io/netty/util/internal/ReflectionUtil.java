package io.netty.util.internal;

import java.lang.reflect.AccessibleObject;

public final class ReflectionUtil {
   private ReflectionUtil() {
      super();
   }

   public static Throwable trySetAccessible(AccessibleObject var0, boolean var1) {
      if (var1 && !PlatformDependent0.isExplicitTryReflectionSetAccessible()) {
         return new UnsupportedOperationException("Reflective setAccessible(true) disabled");
      } else {
         try {
            var0.setAccessible(true);
            return null;
         } catch (SecurityException var3) {
            return var3;
         } catch (RuntimeException var4) {
            return handleInaccessibleObjectException(var4);
         }
      }
   }

   private static RuntimeException handleInaccessibleObjectException(RuntimeException var0) {
      if ("java.lang.reflect.InaccessibleObjectException".equals(var0.getClass().getName())) {
         return var0;
      } else {
         throw var0;
      }
   }
}
