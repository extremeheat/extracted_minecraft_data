package org.apache.commons.lang3.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.ClassUtils;

abstract class MemberUtils {
   private static final int ACCESS_TEST = 7;
   private static final Class<?>[] ORDERED_PRIMITIVE_TYPES;

   MemberUtils() {
      super();
   }

   static boolean setAccessibleWorkaround(AccessibleObject var0) {
      if (var0 != null && !var0.isAccessible()) {
         Member var1 = (Member)var0;
         if (!var0.isAccessible() && Modifier.isPublic(var1.getModifiers()) && isPackageAccess(var1.getDeclaringClass().getModifiers())) {
            try {
               var0.setAccessible(true);
               return true;
            } catch (SecurityException var3) {
            }
         }

         return false;
      } else {
         return false;
      }
   }

   static boolean isPackageAccess(int var0) {
      return (var0 & 7) == 0;
   }

   static boolean isAccessible(Member var0) {
      return var0 != null && Modifier.isPublic(var0.getModifiers()) && !var0.isSynthetic();
   }

   static int compareConstructorFit(Constructor<?> var0, Constructor<?> var1, Class<?>[] var2) {
      return compareParameterTypes(MemberUtils.Executable.of(var0), MemberUtils.Executable.of(var1), var2);
   }

   static int compareMethodFit(Method var0, Method var1, Class<?>[] var2) {
      return compareParameterTypes(MemberUtils.Executable.of(var0), MemberUtils.Executable.of(var1), var2);
   }

   private static int compareParameterTypes(MemberUtils.Executable var0, MemberUtils.Executable var1, Class<?>[] var2) {
      float var3 = getTotalTransformationCost(var2, var0);
      float var4 = getTotalTransformationCost(var2, var1);
      return var3 < var4 ? -1 : (var4 < var3 ? 1 : 0);
   }

   private static float getTotalTransformationCost(Class<?>[] var0, MemberUtils.Executable var1) {
      Class[] var2 = var1.getParameterTypes();
      boolean var3 = var1.isVarArgs();
      float var4 = 0.0F;
      long var5 = var3 ? (long)(var2.length - 1) : (long)var2.length;
      if ((long)var0.length < var5) {
         return 3.4028235E38F;
      } else {
         for(int var7 = 0; (long)var7 < var5; ++var7) {
            var4 += getObjectTransformationCost(var0[var7], var2[var7]);
         }

         if (var3) {
            boolean var13 = var0.length < var2.length;
            boolean var8 = var0.length == var2.length && var0[var0.length - 1].isArray();
            float var9 = 0.001F;
            Class var10 = var2[var2.length - 1].getComponentType();
            if (var13) {
               var4 += getObjectTransformationCost(var10, Object.class) + 0.001F;
            } else if (var8) {
               Class var11 = var0[var0.length - 1].getComponentType();
               var4 += getObjectTransformationCost(var11, var10) + 0.001F;
            } else {
               for(int var14 = var2.length - 1; var14 < var0.length; ++var14) {
                  Class var12 = var0[var14];
                  var4 += getObjectTransformationCost(var12, var10) + 0.001F;
               }
            }
         }

         return var4;
      }
   }

   private static float getObjectTransformationCost(Class<?> var0, Class<?> var1) {
      if (var1.isPrimitive()) {
         return getPrimitivePromotionCost(var0, var1);
      } else {
         float var2;
         for(var2 = 0.0F; var0 != null && !var1.equals(var0); var0 = var0.getSuperclass()) {
            if (var1.isInterface() && ClassUtils.isAssignable(var0, var1)) {
               var2 += 0.25F;
               break;
            }

            ++var2;
         }

         if (var0 == null) {
            ++var2;
         }

         return var2;
      }
   }

   private static float getPrimitivePromotionCost(Class<?> var0, Class<?> var1) {
      float var2 = 0.0F;
      Class var3 = var0;
      if (!var0.isPrimitive()) {
         var2 += 0.1F;
         var3 = ClassUtils.wrapperToPrimitive(var0);
      }

      for(int var4 = 0; var3 != var1 && var4 < ORDERED_PRIMITIVE_TYPES.length; ++var4) {
         if (var3 == ORDERED_PRIMITIVE_TYPES[var4]) {
            var2 += 0.1F;
            if (var4 < ORDERED_PRIMITIVE_TYPES.length - 1) {
               var3 = ORDERED_PRIMITIVE_TYPES[var4 + 1];
            }
         }
      }

      return var2;
   }

   static boolean isMatchingMethod(Method var0, Class<?>[] var1) {
      return isMatchingExecutable(MemberUtils.Executable.of(var0), var1);
   }

   static boolean isMatchingConstructor(Constructor<?> var0, Class<?>[] var1) {
      return isMatchingExecutable(MemberUtils.Executable.of(var0), var1);
   }

   private static boolean isMatchingExecutable(MemberUtils.Executable var0, Class<?>[] var1) {
      Class[] var2 = var0.getParameterTypes();
      if (!var0.isVarArgs()) {
         return ClassUtils.isAssignable(var1, var2, true);
      } else {
         int var3;
         for(var3 = 0; var3 < var2.length - 1 && var3 < var1.length; ++var3) {
            if (!ClassUtils.isAssignable(var1[var3], var2[var3], true)) {
               return false;
            }
         }

         for(Class var4 = var2[var2.length - 1].getComponentType(); var3 < var1.length; ++var3) {
            if (!ClassUtils.isAssignable(var1[var3], var4, true)) {
               return false;
            }
         }

         return true;
      }
   }

   static {
      ORDERED_PRIMITIVE_TYPES = new Class[]{Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE};
   }

   private static final class Executable {
      private final Class<?>[] parameterTypes;
      private final boolean isVarArgs;

      private static MemberUtils.Executable of(Method var0) {
         return new MemberUtils.Executable(var0);
      }

      private static MemberUtils.Executable of(Constructor<?> var0) {
         return new MemberUtils.Executable(var0);
      }

      private Executable(Method var1) {
         super();
         this.parameterTypes = var1.getParameterTypes();
         this.isVarArgs = var1.isVarArgs();
      }

      private Executable(Constructor<?> var1) {
         super();
         this.parameterTypes = var1.getParameterTypes();
         this.isVarArgs = var1.isVarArgs();
      }

      public Class<?>[] getParameterTypes() {
         return this.parameterTypes;
      }

      public boolean isVarArgs() {
         return this.isVarArgs;
      }
   }
}
