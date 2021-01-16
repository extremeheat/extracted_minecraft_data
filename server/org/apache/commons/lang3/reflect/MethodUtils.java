package org.apache.commons.lang3.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

public class MethodUtils {
   public MethodUtils() {
      super();
   }

   public static Object invokeMethod(Object var0, String var1) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      return invokeMethod(var0, var1, ArrayUtils.EMPTY_OBJECT_ARRAY, (Class[])null);
   }

   public static Object invokeMethod(Object var0, boolean var1, String var2) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      return invokeMethod(var0, var1, var2, ArrayUtils.EMPTY_OBJECT_ARRAY, (Class[])null);
   }

   public static Object invokeMethod(Object var0, String var1, Object... var2) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var2 = ArrayUtils.nullToEmpty(var2);
      Class[] var3 = ClassUtils.toClass(var2);
      return invokeMethod(var0, var1, var2, var3);
   }

   public static Object invokeMethod(Object var0, boolean var1, String var2, Object... var3) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var3 = ArrayUtils.nullToEmpty(var3);
      Class[] var4 = ClassUtils.toClass(var3);
      return invokeMethod(var0, var1, var2, var3, var4);
   }

   public static Object invokeMethod(Object var0, boolean var1, String var2, Object[] var3, Class<?>[] var4) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var4 = ArrayUtils.nullToEmpty(var4);
      var3 = ArrayUtils.nullToEmpty(var3);
      Method var6 = null;
      boolean var7 = false;
      Object var8 = null;

      try {
         String var5;
         if (var1) {
            var5 = "No such method: ";
            var6 = getMatchingMethod(var0.getClass(), var2, var4);
            if (var6 != null) {
               var7 = var6.isAccessible();
               if (!var7) {
                  var6.setAccessible(true);
               }
            }
         } else {
            var5 = "No such accessible method: ";
            var6 = getMatchingAccessibleMethod(var0.getClass(), var2, var4);
         }

         if (var6 == null) {
            throw new NoSuchMethodException(var5 + var2 + "() on object: " + var0.getClass().getName());
         }

         var3 = toVarArgs(var6, var3);
         var8 = var6.invoke(var0, var3);
      } finally {
         if (var6 != null && var1 && var6.isAccessible() != var7) {
            var6.setAccessible(var7);
         }

      }

      return var8;
   }

   public static Object invokeMethod(Object var0, String var1, Object[] var2, Class<?>[] var3) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      return invokeMethod(var0, false, var1, var2, var3);
   }

   public static Object invokeExactMethod(Object var0, String var1) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      return invokeExactMethod(var0, var1, ArrayUtils.EMPTY_OBJECT_ARRAY, (Class[])null);
   }

   public static Object invokeExactMethod(Object var0, String var1, Object... var2) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var2 = ArrayUtils.nullToEmpty(var2);
      Class[] var3 = ClassUtils.toClass(var2);
      return invokeExactMethod(var0, var1, var2, var3);
   }

   public static Object invokeExactMethod(Object var0, String var1, Object[] var2, Class<?>[] var3) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var2 = ArrayUtils.nullToEmpty(var2);
      var3 = ArrayUtils.nullToEmpty(var3);
      Method var4 = getAccessibleMethod(var0.getClass(), var1, var3);
      if (var4 == null) {
         throw new NoSuchMethodException("No such accessible method: " + var1 + "() on object: " + var0.getClass().getName());
      } else {
         return var4.invoke(var0, var2);
      }
   }

   public static Object invokeExactStaticMethod(Class<?> var0, String var1, Object[] var2, Class<?>[] var3) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var2 = ArrayUtils.nullToEmpty(var2);
      var3 = ArrayUtils.nullToEmpty(var3);
      Method var4 = getAccessibleMethod(var0, var1, var3);
      if (var4 == null) {
         throw new NoSuchMethodException("No such accessible method: " + var1 + "() on class: " + var0.getName());
      } else {
         return var4.invoke((Object)null, var2);
      }
   }

   public static Object invokeStaticMethod(Class<?> var0, String var1, Object... var2) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var2 = ArrayUtils.nullToEmpty(var2);
      Class[] var3 = ClassUtils.toClass(var2);
      return invokeStaticMethod(var0, var1, var2, var3);
   }

   public static Object invokeStaticMethod(Class<?> var0, String var1, Object[] var2, Class<?>[] var3) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var2 = ArrayUtils.nullToEmpty(var2);
      var3 = ArrayUtils.nullToEmpty(var3);
      Method var4 = getMatchingAccessibleMethod(var0, var1, var3);
      if (var4 == null) {
         throw new NoSuchMethodException("No such accessible method: " + var1 + "() on class: " + var0.getName());
      } else {
         var2 = toVarArgs(var4, var2);
         return var4.invoke((Object)null, var2);
      }
   }

   private static Object[] toVarArgs(Method var0, Object[] var1) {
      if (var0.isVarArgs()) {
         Class[] var2 = var0.getParameterTypes();
         var1 = getVarArgs(var1, var2);
      }

      return var1;
   }

   static Object[] getVarArgs(Object[] var0, Class<?>[] var1) {
      if (var0.length == var1.length && var0[var0.length - 1].getClass().equals(var1[var1.length - 1])) {
         return var0;
      } else {
         Object[] var2 = new Object[var1.length];
         System.arraycopy(var0, 0, var2, 0, var1.length - 1);
         Class var3 = var1[var1.length - 1].getComponentType();
         int var4 = var0.length - var1.length + 1;
         Object var5 = Array.newInstance(ClassUtils.primitiveToWrapper(var3), var4);
         System.arraycopy(var0, var1.length - 1, var5, 0, var4);
         if (var3.isPrimitive()) {
            var5 = ArrayUtils.toPrimitive(var5);
         }

         var2[var1.length - 1] = var5;
         return var2;
      }
   }

   public static Object invokeExactStaticMethod(Class<?> var0, String var1, Object... var2) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      var2 = ArrayUtils.nullToEmpty(var2);
      Class[] var3 = ClassUtils.toClass(var2);
      return invokeExactStaticMethod(var0, var1, var2, var3);
   }

   public static Method getAccessibleMethod(Class<?> var0, String var1, Class<?>... var2) {
      try {
         return getAccessibleMethod(var0.getMethod(var1, var2));
      } catch (NoSuchMethodException var4) {
         return null;
      }
   }

   public static Method getAccessibleMethod(Method var0) {
      if (!MemberUtils.isAccessible(var0)) {
         return null;
      } else {
         Class var1 = var0.getDeclaringClass();
         if (Modifier.isPublic(var1.getModifiers())) {
            return var0;
         } else {
            String var2 = var0.getName();
            Class[] var3 = var0.getParameterTypes();
            var0 = getAccessibleMethodFromInterfaceNest(var1, var2, var3);
            if (var0 == null) {
               var0 = getAccessibleMethodFromSuperclass(var1, var2, var3);
            }

            return var0;
         }
      }
   }

   private static Method getAccessibleMethodFromSuperclass(Class<?> var0, String var1, Class<?>... var2) {
      for(Class var3 = var0.getSuperclass(); var3 != null; var3 = var3.getSuperclass()) {
         if (Modifier.isPublic(var3.getModifiers())) {
            try {
               return var3.getMethod(var1, var2);
            } catch (NoSuchMethodException var5) {
               return null;
            }
         }
      }

      return null;
   }

   private static Method getAccessibleMethodFromInterfaceNest(Class<?> var0, String var1, Class<?>... var2) {
      while(var0 != null) {
         Class[] var3 = var0.getInterfaces();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (Modifier.isPublic(var3[var4].getModifiers())) {
               try {
                  return var3[var4].getDeclaredMethod(var1, var2);
               } catch (NoSuchMethodException var6) {
                  Method var5 = getAccessibleMethodFromInterfaceNest(var3[var4], var1, var2);
                  if (var5 != null) {
                     return var5;
                  }
               }
            }
         }

         var0 = var0.getSuperclass();
      }

      return null;
   }

   public static Method getMatchingAccessibleMethod(Class<?> var0, String var1, Class<?>... var2) {
      Method var3;
      try {
         var3 = var0.getMethod(var1, var2);
         MemberUtils.setAccessibleWorkaround(var3);
         return var3;
      } catch (NoSuchMethodException var10) {
         var3 = null;
         Method[] var4 = var0.getMethods();
         Method[] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Method var8 = var5[var7];
            if (var8.getName().equals(var1) && MemberUtils.isMatchingMethod(var8, var2)) {
               Method var9 = getAccessibleMethod(var8);
               if (var9 != null && (var3 == null || MemberUtils.compareMethodFit(var9, var3, var2) < 0)) {
                  var3 = var9;
               }
            }
         }

         if (var3 != null) {
            MemberUtils.setAccessibleWorkaround(var3);
         }

         return var3;
      }
   }

   public static Method getMatchingMethod(Class<?> var0, String var1, Class<?>... var2) {
      Validate.notNull(var0, "Null class not allowed.");
      Validate.notEmpty((CharSequence)var1, "Null or blank methodName not allowed.");
      Method[] var3 = var0.getDeclaredMethods();
      List var4 = ClassUtils.getAllSuperclasses(var0);

      Class var6;
      for(Iterator var5 = var4.iterator(); var5.hasNext(); var3 = (Method[])ArrayUtils.addAll((Object[])var3, (Object[])var6.getDeclaredMethods())) {
         var6 = (Class)var5.next();
      }

      Method var10 = null;
      Method[] var11 = var3;
      int var7 = var3.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Method var9 = var11[var8];
         if (var1.equals(var9.getName()) && ArrayUtils.isEquals(var2, var9.getParameterTypes())) {
            return var9;
         }

         if (var1.equals(var9.getName()) && ClassUtils.isAssignable(var2, var9.getParameterTypes(), true)) {
            if (var10 == null) {
               var10 = var9;
            } else if (distance(var2, var9.getParameterTypes()) < distance(var2, var10.getParameterTypes())) {
               var10 = var9;
            }
         }
      }

      return var10;
   }

   private static int distance(Class<?>[] var0, Class<?>[] var1) {
      int var2 = 0;
      if (!ClassUtils.isAssignable(var0, var1, true)) {
         return -1;
      } else {
         for(int var3 = 0; var3 < var0.length; ++var3) {
            if (!var0[var3].equals(var1[var3])) {
               if (ClassUtils.isAssignable(var0[var3], var1[var3], true) && !ClassUtils.isAssignable(var0[var3], var1[var3], false)) {
                  ++var2;
               } else {
                  var2 += 2;
               }
            }
         }

         return var2;
      }
   }

   public static Set<Method> getOverrideHierarchy(Method var0, ClassUtils.Interfaces var1) {
      Validate.notNull(var0);
      LinkedHashSet var2 = new LinkedHashSet();
      var2.add(var0);
      Class[] var3 = var0.getParameterTypes();
      Class var4 = var0.getDeclaringClass();
      Iterator var5 = ClassUtils.hierarchy(var4, var1).iterator();
      var5.next();

      while(true) {
         label32:
         while(true) {
            Method var7;
            do {
               if (!var5.hasNext()) {
                  return var2;
               }

               Class var6 = (Class)var5.next();
               var7 = getMatchingAccessibleMethod(var6, var0.getName(), var3);
            } while(var7 == null);

            if (Arrays.equals(var7.getParameterTypes(), var3)) {
               var2.add(var7);
            } else {
               Map var8 = TypeUtils.getTypeArguments(var4, var7.getDeclaringClass());

               for(int var9 = 0; var9 < var3.length; ++var9) {
                  Type var10 = TypeUtils.unrollVariables(var8, var0.getGenericParameterTypes()[var9]);
                  Type var11 = TypeUtils.unrollVariables(var8, var7.getGenericParameterTypes()[var9]);
                  if (!TypeUtils.equals(var10, var11)) {
                     continue label32;
                  }
               }

               var2.add(var7);
            }
         }
      }
   }

   public static Method[] getMethodsWithAnnotation(Class<?> var0, Class<? extends Annotation> var1) {
      List var2 = getMethodsListWithAnnotation(var0, var1);
      return (Method[])var2.toArray(new Method[var2.size()]);
   }

   public static List<Method> getMethodsListWithAnnotation(Class<?> var0, Class<? extends Annotation> var1) {
      Validate.isTrue(var0 != null, "The class must not be null");
      Validate.isTrue(var1 != null, "The annotation class must not be null");
      Method[] var2 = var0.getMethods();
      ArrayList var3 = new ArrayList();
      Method[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Method var7 = var4[var6];
         if (var7.getAnnotation(var1) != null) {
            var3.add(var7);
         }
      }

      return var3;
   }
}
