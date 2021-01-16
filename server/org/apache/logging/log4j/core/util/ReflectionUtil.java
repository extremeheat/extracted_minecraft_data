package org.apache.logging.log4j.core.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Objects;

public final class ReflectionUtil {
   private ReflectionUtil() {
      super();
   }

   public static <T extends AccessibleObject & Member> boolean isAccessible(T var0) {
      Objects.requireNonNull(var0, "No member provided");
      return Modifier.isPublic(((Member)var0).getModifiers()) && Modifier.isPublic(((Member)var0).getDeclaringClass().getModifiers());
   }

   public static <T extends AccessibleObject & Member> void makeAccessible(T var0) {
      if (!isAccessible(var0) && !var0.isAccessible()) {
         var0.setAccessible(true);
      }

   }

   public static void makeAccessible(Field var0) {
      Objects.requireNonNull(var0, "No field provided");
      if ((!isAccessible(var0) || Modifier.isFinal(var0.getModifiers())) && !var0.isAccessible()) {
         var0.setAccessible(true);
      }

   }

   public static Object getFieldValue(Field var0, Object var1) {
      makeAccessible(var0);
      if (!Modifier.isStatic(var0.getModifiers())) {
         Objects.requireNonNull(var1, "No instance given for non-static field");
      }

      try {
         return var0.get(var1);
      } catch (IllegalAccessException var3) {
         throw new UnsupportedOperationException(var3);
      }
   }

   public static Object getStaticFieldValue(Field var0) {
      return getFieldValue(var0, (Object)null);
   }

   public static void setFieldValue(Field var0, Object var1, Object var2) {
      makeAccessible(var0);
      if (!Modifier.isStatic(var0.getModifiers())) {
         Objects.requireNonNull(var1, "No instance given for non-static field");
      }

      try {
         var0.set(var1, var2);
      } catch (IllegalAccessException var4) {
         throw new UnsupportedOperationException(var4);
      }
   }

   public static void setStaticFieldValue(Field var0, Object var1) {
      setFieldValue(var0, (Object)null, var1);
   }

   public static <T> Constructor<T> getDefaultConstructor(Class<T> var0) {
      Objects.requireNonNull(var0, "No class provided");

      try {
         Constructor var1 = var0.getDeclaredConstructor();
         makeAccessible((AccessibleObject)var1);
         return var1;
      } catch (NoSuchMethodException var4) {
         try {
            Constructor var2 = var0.getConstructor();
            makeAccessible((AccessibleObject)var2);
            return var2;
         } catch (NoSuchMethodException var3) {
            throw new IllegalStateException(var3);
         }
      }
   }

   public static <T> T instantiate(Class<T> var0) {
      Objects.requireNonNull(var0, "No class provided");
      Constructor var1 = getDefaultConstructor(var0);

      try {
         return var1.newInstance();
      } catch (InstantiationException | LinkageError var3) {
         throw new IllegalArgumentException(var3);
      } catch (IllegalAccessException var4) {
         throw new IllegalStateException(var4);
      } catch (InvocationTargetException var5) {
         Throwables.rethrow(var5.getCause());
         throw new InternalError("Unreachable");
      }
   }
}
