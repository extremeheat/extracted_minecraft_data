package org.apache.logging.log4j.core.util;

import java.lang.reflect.Array;

public class ArrayUtils {
   public ArrayUtils() {
      super();
   }

   public static int getLength(Object var0) {
      return var0 == null ? 0 : Array.getLength(var0);
   }

   private static Object remove(Object var0, int var1) {
      int var2 = getLength(var0);
      if (var1 >= 0 && var1 < var2) {
         Object var3 = Array.newInstance(var0.getClass().getComponentType(), var2 - 1);
         System.arraycopy(var0, 0, var3, 0, var1);
         if (var1 < var2 - 1) {
            System.arraycopy(var0, var1 + 1, var3, var1, var2 - var1 - 1);
         }

         return var3;
      } else {
         throw new IndexOutOfBoundsException("Index: " + var1 + ", Length: " + var2);
      }
   }

   public static <T> T[] remove(T[] var0, int var1) {
      return (Object[])((Object[])remove((Object)var0, var1));
   }
}
