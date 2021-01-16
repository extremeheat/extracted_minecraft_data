package org.apache.logging.log4j.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TypeUtil {
   private TypeUtil() {
      super();
   }

   public static List<Field> getAllDeclaredFields(Class<?> var0) {
      ArrayList var1;
      for(var1 = new ArrayList(); var0 != null; var0 = var0.getSuperclass()) {
         Field[] var2 = var0.getDeclaredFields();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Field var5 = var2[var4];
            var1.add(var5);
         }
      }

      return var1;
   }

   public static boolean isAssignable(Type var0, Type var1) {
      Objects.requireNonNull(var0, "No left hand side type provided");
      Objects.requireNonNull(var1, "No right hand side type provided");
      if (var0.equals(var1)) {
         return true;
      } else if (Object.class.equals(var0)) {
         return true;
      } else {
         Type var3;
         Class var6;
         if (var0 instanceof Class) {
            Class var2 = (Class)var0;
            if (var1 instanceof Class) {
               var6 = (Class)var1;
               return var2.isAssignableFrom(var6);
            }

            if (var1 instanceof ParameterizedType) {
               var3 = ((ParameterizedType)var1).getRawType();
               if (var3 instanceof Class) {
                  return var2.isAssignableFrom((Class)var3);
               }
            }

            if (var2.isArray() && var1 instanceof GenericArrayType) {
               return isAssignable(var2.getComponentType(), ((GenericArrayType)var1).getGenericComponentType());
            }
         }

         if (var0 instanceof ParameterizedType) {
            ParameterizedType var4 = (ParameterizedType)var0;
            if (var1 instanceof Class) {
               var3 = var4.getRawType();
               if (var3 instanceof Class) {
                  return ((Class)var3).isAssignableFrom((Class)var1);
               }
            } else if (var1 instanceof ParameterizedType) {
               ParameterizedType var7 = (ParameterizedType)var1;
               return isParameterizedAssignable(var4, var7);
            }
         }

         if (var0 instanceof GenericArrayType) {
            Type var5 = ((GenericArrayType)var0).getGenericComponentType();
            if (var1 instanceof Class) {
               var6 = (Class)var1;
               if (var6.isArray()) {
                  return isAssignable(var5, var6.getComponentType());
               }
            } else if (var1 instanceof GenericArrayType) {
               return isAssignable(var5, ((GenericArrayType)var1).getGenericComponentType());
            }
         }

         return var0 instanceof WildcardType ? isWildcardAssignable((WildcardType)var0, var1) : false;
      }
   }

   private static boolean isParameterizedAssignable(ParameterizedType var0, ParameterizedType var1) {
      if (var0.equals(var1)) {
         return true;
      } else {
         Type[] var2 = var0.getActualTypeArguments();
         Type[] var3 = var1.getActualTypeArguments();
         int var4 = var2.length;
         if (var3.length != var4) {
            return false;
         } else {
            for(int var5 = 0; var5 < var4; ++var5) {
               Type var6 = var2[var5];
               Type var7 = var3[var5];
               if (!var6.equals(var7) && (!(var6 instanceof WildcardType) || !isWildcardAssignable((WildcardType)var6, var7))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private static boolean isWildcardAssignable(WildcardType var0, Type var1) {
      Type[] var2 = getEffectiveUpperBounds(var0);
      Type[] var3 = getEffectiveLowerBounds(var0);
      if (var1 instanceof WildcardType) {
         WildcardType var4 = (WildcardType)var1;
         Type[] var5 = getEffectiveUpperBounds(var4);
         Type[] var6 = getEffectiveLowerBounds(var4);
         Type[] var7 = var2;
         int var8 = var2.length;

         int var9;
         Type var10;
         Type[] var11;
         int var12;
         int var13;
         Type var14;
         for(var9 = 0; var9 < var8; ++var9) {
            var10 = var7[var9];
            var11 = var5;
            var12 = var5.length;

            for(var13 = 0; var13 < var12; ++var13) {
               var14 = var11[var13];
               if (!isBoundAssignable(var10, var14)) {
                  return false;
               }
            }

            var11 = var6;
            var12 = var6.length;

            for(var13 = 0; var13 < var12; ++var13) {
               var14 = var11[var13];
               if (!isBoundAssignable(var10, var14)) {
                  return false;
               }
            }
         }

         var7 = var3;
         var8 = var3.length;

         for(var9 = 0; var9 < var8; ++var9) {
            var10 = var7[var9];
            var11 = var5;
            var12 = var5.length;

            for(var13 = 0; var13 < var12; ++var13) {
               var14 = var11[var13];
               if (!isBoundAssignable(var14, var10)) {
                  return false;
               }
            }

            var11 = var6;
            var12 = var6.length;

            for(var13 = 0; var13 < var12; ++var13) {
               var14 = var11[var13];
               if (!isBoundAssignable(var14, var10)) {
                  return false;
               }
            }
         }
      } else {
         Type[] var15 = var2;
         int var16 = var2.length;

         int var17;
         Type var18;
         for(var17 = 0; var17 < var16; ++var17) {
            var18 = var15[var17];
            if (!isBoundAssignable(var18, var1)) {
               return false;
            }
         }

         var15 = var3;
         var16 = var3.length;

         for(var17 = 0; var17 < var16; ++var17) {
            var18 = var15[var17];
            if (!isBoundAssignable(var18, var1)) {
               return false;
            }
         }
      }

      return true;
   }

   private static Type[] getEffectiveUpperBounds(WildcardType var0) {
      Type[] var1 = var0.getUpperBounds();
      return var1.length == 0 ? new Type[]{Object.class} : var1;
   }

   private static Type[] getEffectiveLowerBounds(WildcardType var0) {
      Type[] var1 = var0.getLowerBounds();
      return var1.length == 0 ? new Type[]{null} : var1;
   }

   private static boolean isBoundAssignable(Type var0, Type var1) {
      return var1 == null || var0 != null && isAssignable(var0, var1);
   }
}
