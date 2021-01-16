package org.apache.commons.lang3;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AnnotationUtils {
   private static final ToStringStyle TO_STRING_STYLE = new ToStringStyle() {
      private static final long serialVersionUID = 1L;

      {
         this.setDefaultFullDetail(true);
         this.setArrayContentDetail(true);
         this.setUseClassName(true);
         this.setUseShortClassName(true);
         this.setUseIdentityHashCode(false);
         this.setContentStart("(");
         this.setContentEnd(")");
         this.setFieldSeparator(", ");
         this.setArrayStart("[");
         this.setArrayEnd("]");
      }

      protected String getShortClassName(Class<?> var1) {
         Class var2 = null;
         Iterator var3 = ClassUtils.getAllInterfaces(var1).iterator();

         while(var3.hasNext()) {
            Class var4 = (Class)var3.next();
            if (Annotation.class.isAssignableFrom(var4)) {
               var2 = var4;
               break;
            }
         }

         return (new StringBuilder(var2 == null ? "" : var2.getName())).insert(0, '@').toString();
      }

      protected void appendDetail(StringBuffer var1, String var2, Object var3) {
         if (var3 instanceof Annotation) {
            var3 = AnnotationUtils.toString((Annotation)var3);
         }

         super.appendDetail(var1, var2, var3);
      }
   };

   public AnnotationUtils() {
      super();
   }

   public static boolean equals(Annotation var0, Annotation var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         Class var2 = var0.annotationType();
         Class var3 = var1.annotationType();
         Validate.notNull(var2, "Annotation %s with null annotationType()", var0);
         Validate.notNull(var3, "Annotation %s with null annotationType()", var1);
         if (!var2.equals(var3)) {
            return false;
         } else {
            try {
               Method[] var4 = var2.getDeclaredMethods();
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  Method var7 = var4[var6];
                  if (var7.getParameterTypes().length == 0 && isValidAnnotationMemberType(var7.getReturnType())) {
                     Object var8 = var7.invoke(var0);
                     Object var9 = var7.invoke(var1);
                     if (!memberEquals(var7.getReturnType(), var8, var9)) {
                        return false;
                     }
                  }
               }

               return true;
            } catch (IllegalAccessException var10) {
               return false;
            } catch (InvocationTargetException var11) {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public static int hashCode(Annotation var0) {
      int var1 = 0;
      Class var2 = var0.annotationType();
      Method[] var3 = var2.getDeclaredMethods();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Method var6 = var3[var5];

         try {
            Object var7 = var6.invoke(var0);
            if (var7 == null) {
               throw new IllegalStateException(String.format("Annotation method %s returned null", var6));
            }

            var1 += hashMember(var6.getName(), var7);
         } catch (RuntimeException var8) {
            throw var8;
         } catch (Exception var9) {
            throw new RuntimeException(var9);
         }
      }

      return var1;
   }

   public static String toString(Annotation var0) {
      ToStringBuilder var1 = new ToStringBuilder(var0, TO_STRING_STYLE);
      Method[] var2 = var0.annotationType().getDeclaredMethods();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Method var5 = var2[var4];
         if (var5.getParameterTypes().length <= 0) {
            try {
               var1.append(var5.getName(), var5.invoke(var0));
            } catch (RuntimeException var7) {
               throw var7;
            } catch (Exception var8) {
               throw new RuntimeException(var8);
            }
         }
      }

      return var1.build();
   }

   public static boolean isValidAnnotationMemberType(Class<?> var0) {
      if (var0 == null) {
         return false;
      } else {
         if (var0.isArray()) {
            var0 = var0.getComponentType();
         }

         return var0.isPrimitive() || var0.isEnum() || var0.isAnnotation() || String.class.equals(var0) || Class.class.equals(var0);
      }
   }

   private static int hashMember(String var0, Object var1) {
      int var2 = var0.hashCode() * 127;
      if (var1.getClass().isArray()) {
         return var2 ^ arrayMemberHash(var1.getClass().getComponentType(), var1);
      } else {
         return var1 instanceof Annotation ? var2 ^ hashCode((Annotation)var1) : var2 ^ var1.hashCode();
      }
   }

   private static boolean memberEquals(Class<?> var0, Object var1, Object var2) {
      if (var1 == var2) {
         return true;
      } else if (var1 != null && var2 != null) {
         if (var0.isArray()) {
            return arrayMemberEquals(var0.getComponentType(), var1, var2);
         } else {
            return var0.isAnnotation() ? equals((Annotation)var1, (Annotation)var2) : var1.equals(var2);
         }
      } else {
         return false;
      }
   }

   private static boolean arrayMemberEquals(Class<?> var0, Object var1, Object var2) {
      if (var0.isAnnotation()) {
         return annotationArrayMemberEquals((Annotation[])((Annotation[])var1), (Annotation[])((Annotation[])var2));
      } else if (var0.equals(Byte.TYPE)) {
         return Arrays.equals((byte[])((byte[])var1), (byte[])((byte[])var2));
      } else if (var0.equals(Short.TYPE)) {
         return Arrays.equals((short[])((short[])var1), (short[])((short[])var2));
      } else if (var0.equals(Integer.TYPE)) {
         return Arrays.equals((int[])((int[])var1), (int[])((int[])var2));
      } else if (var0.equals(Character.TYPE)) {
         return Arrays.equals((char[])((char[])var1), (char[])((char[])var2));
      } else if (var0.equals(Long.TYPE)) {
         return Arrays.equals((long[])((long[])var1), (long[])((long[])var2));
      } else if (var0.equals(Float.TYPE)) {
         return Arrays.equals((float[])((float[])var1), (float[])((float[])var2));
      } else if (var0.equals(Double.TYPE)) {
         return Arrays.equals((double[])((double[])var1), (double[])((double[])var2));
      } else {
         return var0.equals(Boolean.TYPE) ? Arrays.equals((boolean[])((boolean[])var1), (boolean[])((boolean[])var2)) : Arrays.equals((Object[])((Object[])var1), (Object[])((Object[])var2));
      }
   }

   private static boolean annotationArrayMemberEquals(Annotation[] var0, Annotation[] var1) {
      if (var0.length != var1.length) {
         return false;
      } else {
         for(int var2 = 0; var2 < var0.length; ++var2) {
            if (!equals(var0[var2], var1[var2])) {
               return false;
            }
         }

         return true;
      }
   }

   private static int arrayMemberHash(Class<?> var0, Object var1) {
      if (var0.equals(Byte.TYPE)) {
         return Arrays.hashCode((byte[])((byte[])var1));
      } else if (var0.equals(Short.TYPE)) {
         return Arrays.hashCode((short[])((short[])var1));
      } else if (var0.equals(Integer.TYPE)) {
         return Arrays.hashCode((int[])((int[])var1));
      } else if (var0.equals(Character.TYPE)) {
         return Arrays.hashCode((char[])((char[])var1));
      } else if (var0.equals(Long.TYPE)) {
         return Arrays.hashCode((long[])((long[])var1));
      } else if (var0.equals(Float.TYPE)) {
         return Arrays.hashCode((float[])((float[])var1));
      } else if (var0.equals(Double.TYPE)) {
         return Arrays.hashCode((double[])((double[])var1));
      } else {
         return var0.equals(Boolean.TYPE) ? Arrays.hashCode((boolean[])((boolean[])var1)) : Arrays.hashCode((Object[])((Object[])var1));
      }
   }
}
