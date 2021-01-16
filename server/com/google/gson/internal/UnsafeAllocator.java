package com.google.gson.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class UnsafeAllocator {
   public UnsafeAllocator() {
      super();
   }

   public abstract <T> T newInstance(Class<T> var1) throws Exception;

   public static UnsafeAllocator create() {
      try {
         Class var7 = Class.forName("sun.misc.Unsafe");
         Field var8 = var7.getDeclaredField("theUnsafe");
         var8.setAccessible(true);
         final Object var9 = var8.get((Object)null);
         final Method var3 = var7.getMethod("allocateInstance", Class.class);
         return new UnsafeAllocator() {
            public <T> T newInstance(Class<T> var1) throws Exception {
               UnsafeAllocator.assertInstantiable(var1);
               return var3.invoke(var9, var1);
            }
         };
      } catch (Exception var6) {
         final Method var0;
         try {
            var0 = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
            var0.setAccessible(true);
            final int var1 = (Integer)var0.invoke((Object)null, Object.class);
            final Method var2 = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, Integer.TYPE);
            var2.setAccessible(true);
            return new UnsafeAllocator() {
               public <T> T newInstance(Class<T> var1x) throws Exception {
                  UnsafeAllocator.assertInstantiable(var1x);
                  return var2.invoke((Object)null, var1x, var1);
               }
            };
         } catch (Exception var5) {
            try {
               var0 = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
               var0.setAccessible(true);
               return new UnsafeAllocator() {
                  public <T> T newInstance(Class<T> var1) throws Exception {
                     UnsafeAllocator.assertInstantiable(var1);
                     return var0.invoke((Object)null, var1, Object.class);
                  }
               };
            } catch (Exception var4) {
               return new UnsafeAllocator() {
                  public <T> T newInstance(Class<T> var1) {
                     throw new UnsupportedOperationException("Cannot allocate " + var1);
                  }
               };
            }
         }
      }
   }

   private static void assertInstantiable(Class<?> var0) {
      int var1 = var0.getModifiers();
      if (Modifier.isInterface(var1)) {
         throw new UnsupportedOperationException("Interface can't be instantiated! Interface name: " + var0.getName());
      } else if (Modifier.isAbstract(var1)) {
         throw new UnsupportedOperationException("Abstract class can't be instantiated! Class name: " + var0.getName());
      }
   }
}
