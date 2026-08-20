package com.mojang.blaze3d.platform;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class DebugMemoryUntracker {
   private static final @Nullable MethodHandle UNTRACK = (MethodHandle)Util.make(() -> {
      try {
         MethodHandles.Lookup lookup = MethodHandles.lookup();
         Class<?> debugAllocator = Class.forName("org.lwjgl.system.MemoryManage$DebugAllocator");
         Method reflectionUntrack = debugAllocator.getDeclaredMethod("untrack", Long.TYPE);
         reflectionUntrack.setAccessible(true);
         Field allocatorField = Class.forName("org.lwjgl.system.MemoryUtil$LazyInit").getDeclaredField("ALLOCATOR");
         allocatorField.setAccessible(true);
         Object allocator = allocatorField.get((Object)null);
         return debugAllocator.isInstance(allocator) ? lookup.unreflect(reflectionUntrack) : null;
      } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
         throw new RuntimeException(e);
      }
   });

   public DebugMemoryUntracker() {
      super();
   }

   public static void untrack(final long address) {
      if (UNTRACK != null) {
         try {
            UNTRACK.invoke(address);
         } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
         }
      }
   }
}
