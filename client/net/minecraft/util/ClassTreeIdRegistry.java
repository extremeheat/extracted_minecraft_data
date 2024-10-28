package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;

public class ClassTreeIdRegistry {
   public static final int NO_ID_VALUE = -1;
   private final Object2IntMap<Class<?>> classToLastIdCache = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), (var0) -> {
      var0.defaultReturnValue(-1);
   });

   public ClassTreeIdRegistry() {
      super();
   }

   public int getLastIdFor(Class<?> var1) {
      int var2 = this.classToLastIdCache.getInt(var1);
      if (var2 != -1) {
         return var2;
      } else {
         Class var3 = var1;

         int var4;
         do {
            if ((var3 = var3.getSuperclass()) == Object.class) {
               return -1;
            }

            var4 = this.classToLastIdCache.getInt(var3);
         } while(var4 == -1);

         return var4;
      }
   }

   public int getCount(Class<?> var1) {
      return this.getLastIdFor(var1) + 1;
   }

   public int define(Class<?> var1) {
      int var2 = this.getLastIdFor(var1);
      int var3 = var2 == -1 ? 0 : var2 + 1;
      this.classToLastIdCache.put(var1, var3);
      return var3;
   }
}
