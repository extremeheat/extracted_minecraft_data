package com.google.gson.reflect;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.$Gson$Types;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public class TypeToken<T> {
   final Class<? super T> rawType;
   final Type type;
   final int hashCode;

   protected TypeToken() {
      super();
      this.type = getSuperclassTypeParameter(this.getClass());
      this.rawType = $Gson$Types.getRawType(this.type);
      this.hashCode = this.type.hashCode();
   }

   TypeToken(Type var1) {
      super();
      this.type = $Gson$Types.canonicalize((Type)$Gson$Preconditions.checkNotNull(var1));
      this.rawType = $Gson$Types.getRawType(this.type);
      this.hashCode = this.type.hashCode();
   }

   static Type getSuperclassTypeParameter(Class<?> var0) {
      Type var1 = var0.getGenericSuperclass();
      if (var1 instanceof Class) {
         throw new RuntimeException("Missing type parameter.");
      } else {
         ParameterizedType var2 = (ParameterizedType)var1;
         return $Gson$Types.canonicalize(var2.getActualTypeArguments()[0]);
      }
   }

   public final Class<? super T> getRawType() {
      return this.rawType;
   }

   public final Type getType() {
      return this.type;
   }

   /** @deprecated */
   @Deprecated
   public boolean isAssignableFrom(Class<?> var1) {
      return this.isAssignableFrom((Type)var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean isAssignableFrom(Type var1) {
      if (var1 == null) {
         return false;
      } else if (this.type.equals(var1)) {
         return true;
      } else if (this.type instanceof Class) {
         return this.rawType.isAssignableFrom($Gson$Types.getRawType(var1));
      } else if (this.type instanceof ParameterizedType) {
         return isAssignableFrom(var1, (ParameterizedType)this.type, new HashMap());
      } else if (!(this.type instanceof GenericArrayType)) {
         throw buildUnexpectedTypeError(this.type, Class.class, ParameterizedType.class, GenericArrayType.class);
      } else {
         return this.rawType.isAssignableFrom($Gson$Types.getRawType(var1)) && isAssignableFrom(var1, (GenericArrayType)this.type);
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean isAssignableFrom(TypeToken<?> var1) {
      return this.isAssignableFrom(var1.getType());
   }

   private static boolean isAssignableFrom(Type var0, GenericArrayType var1) {
      Type var2 = var1.getGenericComponentType();
      if (!(var2 instanceof ParameterizedType)) {
         return true;
      } else {
         Object var3 = var0;
         if (var0 instanceof GenericArrayType) {
            var3 = ((GenericArrayType)var0).getGenericComponentType();
         } else if (var0 instanceof Class) {
            Class var4;
            for(var4 = (Class)var0; var4.isArray(); var4 = var4.getComponentType()) {
            }

            var3 = var4;
         }

         return isAssignableFrom((Type)var3, (ParameterizedType)var2, new HashMap());
      }
   }

   private static boolean isAssignableFrom(Type var0, ParameterizedType var1, Map<String, Type> var2) {
      if (var0 == null) {
         return false;
      } else if (var1.equals(var0)) {
         return true;
      } else {
         Class var3 = $Gson$Types.getRawType(var0);
         ParameterizedType var4 = null;
         if (var0 instanceof ParameterizedType) {
            var4 = (ParameterizedType)var0;
         }

         Type[] var5;
         int var7;
         Type var8;
         if (var4 != null) {
            var5 = var4.getActualTypeArguments();
            TypeVariable[] var6 = var3.getTypeParameters();

            for(var7 = 0; var7 < var5.length; ++var7) {
               var8 = var5[var7];

               TypeVariable var9;
               TypeVariable var10;
               for(var9 = var6[var7]; var8 instanceof TypeVariable; var8 = (Type)var2.get(var10.getName())) {
                  var10 = (TypeVariable)var8;
               }

               var2.put(var9.getName(), var8);
            }

            if (typeEquals(var4, var1, var2)) {
               return true;
            }
         }

         var5 = var3.getGenericInterfaces();
         int var12 = var5.length;

         for(var7 = 0; var7 < var12; ++var7) {
            var8 = var5[var7];
            if (isAssignableFrom(var8, var1, new HashMap(var2))) {
               return true;
            }
         }

         Type var11 = var3.getGenericSuperclass();
         return isAssignableFrom(var11, var1, new HashMap(var2));
      }
   }

   private static boolean typeEquals(ParameterizedType var0, ParameterizedType var1, Map<String, Type> var2) {
      if (var0.getRawType().equals(var1.getRawType())) {
         Type[] var3 = var0.getActualTypeArguments();
         Type[] var4 = var1.getActualTypeArguments();

         for(int var5 = 0; var5 < var3.length; ++var5) {
            if (!matches(var3[var5], var4[var5], var2)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static AssertionError buildUnexpectedTypeError(Type var0, Class<?>... var1) {
      StringBuilder var2 = new StringBuilder("Unexpected type. Expected one of: ");
      Class[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Class var6 = var3[var5];
         var2.append(var6.getName()).append(", ");
      }

      var2.append("but got: ").append(var0.getClass().getName()).append(", for type token: ").append(var0.toString()).append('.');
      return new AssertionError(var2.toString());
   }

   private static boolean matches(Type var0, Type var1, Map<String, Type> var2) {
      return var1.equals(var0) || var0 instanceof TypeVariable && var1.equals(var2.get(((TypeVariable)var0).getName()));
   }

   public final int hashCode() {
      return this.hashCode;
   }

   public final boolean equals(Object var1) {
      return var1 instanceof TypeToken && $Gson$Types.equals(this.type, ((TypeToken)var1).type);
   }

   public final String toString() {
      return $Gson$Types.typeToString(this.type);
   }

   public static TypeToken<?> get(Type var0) {
      return new TypeToken(var0);
   }

   public static <T> TypeToken<T> get(Class<T> var0) {
      return new TypeToken(var0);
   }

   public static TypeToken<?> getParameterized(Type var0, Type... var1) {
      return new TypeToken($Gson$Types.newParameterizedTypeWithOwner((Type)null, var0, var1));
   }

   public static TypeToken<?> getArray(Type var0) {
      return new TypeToken($Gson$Types.arrayOf(var0));
   }
}
