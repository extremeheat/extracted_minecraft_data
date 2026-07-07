package com.mojang.renderpearl.backend.vulkan.init;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Pointer;
import org.lwjgl.system.Struct;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures2;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties2;

public record VulkanPNextStruct(Class<?> pNextStructClass, int sType, int structSize) {
   private static final int OFFSET_PNEXT;
   private static final int OFFSET_STYPE;

   public <T extends Struct<?>> VulkanPNextStruct(final Class<T> pNextStructClass) {
      String structClassName = pNextStructClass.getSimpleName();

      Method createFunction;
      Method sType$DefaultFunction;
      Field pNextOffsetField;
      Field sTypeOffsetField;
      try {
         createFunction = pNextStructClass.getMethod("calloc", MemoryStack.class);
         sType$DefaultFunction = pNextStructClass.getMethod("sType$Default");
         pNextOffsetField = pNextStructClass.getField("PNEXT");
         sTypeOffsetField = pNextStructClass.getField("STYPE");
      } catch (NoSuchMethodException | NoSuchFieldException e) {
         throw new IllegalArgumentException("Struct class " + structClassName + " does not have required member " + ((ReflectiveOperationException)e).getMessage());
      }

      int sType;
      int structSize;
      try {
         int pNextOffset = (Integer)pNextOffsetField.get((Object)null);
         if (pNextOffset != OFFSET_PNEXT) {
            throw new IllegalArgumentException("Invalid pNext offset on class " + structClassName);
         }

         int sTypeOffset = (Integer)sTypeOffsetField.get((Object)null);
         if (sTypeOffset != OFFSET_STYPE) {
            throw new IllegalArgumentException("Invalid sType offset on class " + structClassName);
         }

         MemoryStack stack = MemoryStack.stackPush();

         try {
            Struct<?> structInstance = (Struct)createFunction.invoke((Object)null, stack);
            sType$DefaultFunction.invoke(structInstance);
            sType = sType(structInstance.address());
            structSize = structInstance.sizeof();
         } catch (Throwable var16) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var14) {
                  var16.addSuppressed(var14);
               }
            }

            throw var16;
         }

         if (stack != null) {
            stack.close();
         }
      } catch (InvocationTargetException | IllegalAccessException e) {
         throw new IllegalArgumentException(e);
      }

      this(pNextStructClass, sType, structSize);
   }

   public VulkanPNextStruct {
      super();
   }

   private static long pNext(final long pointer) {
      return VkPhysicalDeviceProperties2.npNext(pointer);
   }

   private static void pNext(final long pointer, final long value) {
      VkPhysicalDeviceProperties2.npNext(pointer, value);
   }

   private static int sType(final long pointer) {
      return VkPhysicalDeviceProperties2.nsType(pointer);
   }

   private static void sType(final long pointer, final int value) {
      VkPhysicalDeviceProperties2.nsType(pointer, value);
   }

   public long fieldOffset(final String name) {
      Class<?> sourceClass;
      int sourceClassOffset;
      if (this.pNextStructClass == VkPhysicalDeviceFeatures2.class) {
         sourceClass = VkPhysicalDeviceFeatures.class;
         sourceClassOffset = VkPhysicalDeviceFeatures2.FEATURES;
      } else {
         sourceClass = this.pNextStructClass;
         sourceClassOffset = 0;
      }

      try {
         Method method = sourceClass.getMethod(name);
         if (method.getReturnType() != Boolean.TYPE) {
            throw new IllegalArgumentException("Only booleans are supported for struct methods");
         } else {
            Field offsetField = sourceClass.getField(name.toUpperCase(Locale.ROOT));
            return (long)(sourceClassOffset + (Integer)offsetField.get((Object)null));
         }
      } catch (NoSuchMethodException | NoSuchFieldException var6) {
         throw new IllegalArgumentException("Could not find field " + name + " in struct " + this.pNextStructClass.getSimpleName());
      } catch (IllegalAccessException e) {
         throw new IllegalArgumentException(e);
      }
   }

   public long findOrCreateStructInPNextChain(final VkPhysicalDeviceProperties2 properties2, final MemoryStack stack) {
      return this.findOrCreateStructInPNextChain(properties2.address(), stack);
   }

   public long findOrCreateStructInPNextChain(final VkPhysicalDeviceFeatures2 features2, final MemoryStack stack) {
      return this.findOrCreateStructInPNextChain(features2.address(), stack);
   }

   public long findOrCreateStructInPNextChain(final long pNextChain, final MemoryStack stack) {
      long foundStruct = findStructInPNextChain(pNextChain, this.sType);
      if (foundStruct != 0L) {
         return foundStruct;
      } else {
         long newStruct = stack.ncalloc(Pointer.POINTER_SIZE, 1, this.structSize);
         sType(newStruct, this.sType);
         pNext(newStruct, pNext(pNextChain));
         pNext(pNextChain, newStruct);
         return newStruct;
      }
   }

   public long findStructInPNextChain(final long pNextChain) {
      return findStructInPNextChain(pNextChain, this.sType);
   }

   private static long findStructInPNextChain(long pNextChain, final int sType) {
      while(pNextChain != 0L) {
         if (sType(pNextChain) == sType) {
            return pNextChain;
         }

         pNextChain = pNext(pNextChain);
      }

      return 0L;
   }

   public String toString() {
      return this.pNextStructClass.getSimpleName();
   }

   static {
      OFFSET_PNEXT = VkPhysicalDeviceProperties2.PNEXT;
      OFFSET_STYPE = VkPhysicalDeviceProperties2.STYPE;
   }
}
