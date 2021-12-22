package com.mojang.blaze3d.platform;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import org.lwjgl.system.Pointer;

public class DebugMemoryUntracker {
   @Nullable
   private static final MethodHandle UNTRACK = (MethodHandle)GLX.make(() -> {
      try {
         Lookup var0 = MethodHandles.lookup();
         Class var1 = Class.forName("org.lwjgl.system.MemoryManage$DebugAllocator");
         Method var2 = var1.getDeclaredMethod("untrack", Long.TYPE);
         var2.setAccessible(true);
         Field var3 = Class.forName("org.lwjgl.system.MemoryUtil$LazyInit").getDeclaredField("ALLOCATOR");
         var3.setAccessible(true);
         Object var4 = var3.get((Object)null);
         return var1.isInstance(var4) ? var0.unreflect(var2) : null;
      } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | ClassNotFoundException var5) {
         throw new RuntimeException(var5);
      }
   });

   public DebugMemoryUntracker() {
      super();
   }

   public static void untrack(long var0) {
      if (UNTRACK != null) {
         try {
            UNTRACK.invoke(var0);
         } catch (Throwable var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   public static void untrack(Pointer var0) {
      untrack(var0.address());
   }
}
