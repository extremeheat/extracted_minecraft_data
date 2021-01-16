package com.google.common.reflect;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

final class Types {
   private static final Function<Type, String> TYPE_NAME = new Function<Type, String>() {
      public String apply(Type var1) {
         return Types.JavaVersion.CURRENT.typeName(var1);
      }
   };
   private static final Joiner COMMA_JOINER = Joiner.on(", ").useForNull("null");

   static Type newArrayType(Type var0) {
      if (var0 instanceof WildcardType) {
         WildcardType var1 = (WildcardType)var0;
         Type[] var2 = var1.getLowerBounds();
         Preconditions.checkArgument(var2.length <= 1, "Wildcard cannot have more than one lower bounds.");
         if (var2.length == 1) {
            return supertypeOf(newArrayType(var2[0]));
         } else {
            Type[] var3 = var1.getUpperBounds();
            Preconditions.checkArgument(var3.length == 1, "Wildcard should have only one upper bound.");
            return subtypeOf(newArrayType(var3[0]));
         }
      } else {
         return Types.JavaVersion.CURRENT.newArrayType(var0);
      }
   }

   static ParameterizedType newParameterizedTypeWithOwner(@Nullable Type var0, Class<?> var1, Type... var2) {
      if (var0 == null) {
         return newParameterizedType(var1, var2);
      } else {
         Preconditions.checkNotNull(var2);
         Preconditions.checkArgument(var1.getEnclosingClass() != null, "Owner type for unenclosed %s", (Object)var1);
         return new Types.ParameterizedTypeImpl(var0, var1, var2);
      }
   }

   static ParameterizedType newParameterizedType(Class<?> var0, Type... var1) {
      return new Types.ParameterizedTypeImpl(Types.ClassOwnership.JVM_BEHAVIOR.getOwnerType(var0), var0, var1);
   }

   static <D extends GenericDeclaration> TypeVariable<D> newArtificialTypeVariable(D var0, String var1, Type... var2) {
      return newTypeVariableImpl(var0, var1, var2.length == 0 ? new Type[]{Object.class} : var2);
   }

   @VisibleForTesting
   static WildcardType subtypeOf(Type var0) {
      return new Types.WildcardTypeImpl(new Type[0], new Type[]{var0});
   }

   @VisibleForTesting
   static WildcardType supertypeOf(Type var0) {
      return new Types.WildcardTypeImpl(new Type[]{var0}, new Type[]{Object.class});
   }

   static String toString(Type var0) {
      return var0 instanceof Class ? ((Class)var0).getName() : var0.toString();
   }

   @Nullable
   static Type getComponentType(Type var0) {
      Preconditions.checkNotNull(var0);
      final AtomicReference var1 = new AtomicReference();
      (new TypeVisitor() {
         void visitTypeVariable(TypeVariable<?> var1x) {
            var1.set(Types.subtypeOfComponentType(var1x.getBounds()));
         }

         void visitWildcardType(WildcardType var1x) {
            var1.set(Types.subtypeOfComponentType(var1x.getUpperBounds()));
         }

         void visitGenericArrayType(GenericArrayType var1x) {
            var1.set(var1x.getGenericComponentType());
         }

         void visitClass(Class<?> var1x) {
            var1.set(var1x.getComponentType());
         }
      }).visit(new Type[]{var0});
      return (Type)var1.get();
   }

   @Nullable
   private static Type subtypeOfComponentType(Type[] var0) {
      Type[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Type var4 = var1[var3];
         Type var5 = getComponentType(var4);
         if (var5 != null) {
            if (var5 instanceof Class) {
               Class var6 = (Class)var5;
               if (var6.isPrimitive()) {
                  return var6;
               }
            }

            return subtypeOf(var5);
         }
      }

      return null;
   }

   private static <D extends GenericDeclaration> TypeVariable<D> newTypeVariableImpl(D var0, String var1, Type[] var2) {
      Types.TypeVariableImpl var3 = new Types.TypeVariableImpl(var0, var1, var2);
      TypeVariable var4 = (TypeVariable)Reflection.newProxy(TypeVariable.class, new Types.TypeVariableInvocationHandler(var3));
      return var4;
   }

   private static Type[] toArray(Collection<Type> var0) {
      return (Type[])var0.toArray(new Type[var0.size()]);
   }

   private static Iterable<Type> filterUpperBounds(Iterable<Type> var0) {
      return Iterables.filter(var0, Predicates.not(Predicates.equalTo(Object.class)));
   }

   private static void disallowPrimitiveType(Type[] var0, String var1) {
      Type[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Type var5 = var2[var4];
         if (var5 instanceof Class) {
            Class var6 = (Class)var5;
            Preconditions.checkArgument(!var6.isPrimitive(), "Primitive type '%s' used as %s", var6, var1);
         }
      }

   }

   static Class<?> getArrayClass(Class<?> var0) {
      return Array.newInstance(var0, 0).getClass();
   }

   private Types() {
      super();
   }

   static final class NativeTypeVariableEquals<X> {
      static final boolean NATIVE_TYPE_VARIABLE_ONLY = !Types.NativeTypeVariableEquals.class.getTypeParameters()[0].equals(Types.newArtificialTypeVariable(Types.NativeTypeVariableEquals.class, "X"));

      NativeTypeVariableEquals() {
         super();
      }
   }

   static enum JavaVersion {
      JAVA6 {
         GenericArrayType newArrayType(Type var1) {
            return new Types.GenericArrayTypeImpl(var1);
         }

         Type usedInGenericType(Type var1) {
            Preconditions.checkNotNull(var1);
            if (var1 instanceof Class) {
               Class var2 = (Class)var1;
               if (var2.isArray()) {
                  return new Types.GenericArrayTypeImpl(var2.getComponentType());
               }
            }

            return var1;
         }
      },
      JAVA7 {
         Type newArrayType(Type var1) {
            return (Type)(var1 instanceof Class ? Types.getArrayClass((Class)var1) : new Types.GenericArrayTypeImpl(var1));
         }

         Type usedInGenericType(Type var1) {
            return (Type)Preconditions.checkNotNull(var1);
         }
      },
      JAVA8 {
         Type newArrayType(Type var1) {
            return JAVA7.newArrayType(var1);
         }

         Type usedInGenericType(Type var1) {
            return JAVA7.usedInGenericType(var1);
         }

         String typeName(Type var1) {
            try {
               Method var2 = Type.class.getMethod("getTypeName");
               return (String)var2.invoke(var1);
            } catch (NoSuchMethodException var3) {
               throw new AssertionError("Type.getTypeName should be available in Java 8");
            } catch (InvocationTargetException var4) {
               throw new RuntimeException(var4);
            } catch (IllegalAccessException var5) {
               throw new RuntimeException(var5);
            }
         }
      };

      static final Types.JavaVersion CURRENT;

      private JavaVersion() {
      }

      abstract Type newArrayType(Type var1);

      abstract Type usedInGenericType(Type var1);

      String typeName(Type var1) {
         return Types.toString(var1);
      }

      final ImmutableList<Type> usedInGenericType(Type[] var1) {
         ImmutableList.Builder var2 = ImmutableList.builder();
         Type[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Type var6 = var3[var5];
            var2.add((Object)this.usedInGenericType(var6));
         }

         return var2.build();
      }

      // $FF: synthetic method
      JavaVersion(Object var3) {
         this();
      }

      static {
         if (AnnotatedElement.class.isAssignableFrom(TypeVariable.class)) {
            CURRENT = JAVA8;
         } else if ((new TypeCapture<int[]>() {
         }).capture() instanceof Class) {
            CURRENT = JAVA7;
         } else {
            CURRENT = JAVA6;
         }

      }
   }

   static final class WildcardTypeImpl implements WildcardType, Serializable {
      private final ImmutableList<Type> lowerBounds;
      private final ImmutableList<Type> upperBounds;
      private static final long serialVersionUID = 0L;

      WildcardTypeImpl(Type[] var1, Type[] var2) {
         super();
         Types.disallowPrimitiveType(var1, "lower bound for wildcard");
         Types.disallowPrimitiveType(var2, "upper bound for wildcard");
         this.lowerBounds = Types.JavaVersion.CURRENT.usedInGenericType(var1);
         this.upperBounds = Types.JavaVersion.CURRENT.usedInGenericType(var2);
      }

      public Type[] getLowerBounds() {
         return Types.toArray(this.lowerBounds);
      }

      public Type[] getUpperBounds() {
         return Types.toArray(this.upperBounds);
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof WildcardType)) {
            return false;
         } else {
            WildcardType var2 = (WildcardType)var1;
            return this.lowerBounds.equals(Arrays.asList(var2.getLowerBounds())) && this.upperBounds.equals(Arrays.asList(var2.getUpperBounds()));
         }
      }

      public int hashCode() {
         return this.lowerBounds.hashCode() ^ this.upperBounds.hashCode();
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder("?");
         UnmodifiableIterator var2 = this.lowerBounds.iterator();

         Type var3;
         while(var2.hasNext()) {
            var3 = (Type)var2.next();
            var1.append(" super ").append(Types.JavaVersion.CURRENT.typeName(var3));
         }

         Iterator var4 = Types.filterUpperBounds(this.upperBounds).iterator();

         while(var4.hasNext()) {
            var3 = (Type)var4.next();
            var1.append(" extends ").append(Types.JavaVersion.CURRENT.typeName(var3));
         }

         return var1.toString();
      }
   }

   private static final class TypeVariableImpl<D extends GenericDeclaration> {
      private final D genericDeclaration;
      private final String name;
      private final ImmutableList<Type> bounds;

      TypeVariableImpl(D var1, String var2, Type[] var3) {
         super();
         Types.disallowPrimitiveType(var3, "bound for type variable");
         this.genericDeclaration = (GenericDeclaration)Preconditions.checkNotNull(var1);
         this.name = (String)Preconditions.checkNotNull(var2);
         this.bounds = ImmutableList.copyOf((Object[])var3);
      }

      public Type[] getBounds() {
         return Types.toArray(this.bounds);
      }

      public D getGenericDeclaration() {
         return this.genericDeclaration;
      }

      public String getName() {
         return this.name;
      }

      public String getTypeName() {
         return this.name;
      }

      public String toString() {
         return this.name;
      }

      public int hashCode() {
         return this.genericDeclaration.hashCode() ^ this.name.hashCode();
      }

      public boolean equals(Object var1) {
         if (Types.NativeTypeVariableEquals.NATIVE_TYPE_VARIABLE_ONLY) {
            if (var1 != null && Proxy.isProxyClass(var1.getClass()) && Proxy.getInvocationHandler(var1) instanceof Types.TypeVariableInvocationHandler) {
               Types.TypeVariableInvocationHandler var4 = (Types.TypeVariableInvocationHandler)Proxy.getInvocationHandler(var1);
               Types.TypeVariableImpl var3 = var4.typeVariableImpl;
               return this.name.equals(var3.getName()) && this.genericDeclaration.equals(var3.getGenericDeclaration()) && this.bounds.equals(var3.bounds);
            } else {
               return false;
            }
         } else if (!(var1 instanceof TypeVariable)) {
            return false;
         } else {
            TypeVariable var2 = (TypeVariable)var1;
            return this.name.equals(var2.getName()) && this.genericDeclaration.equals(var2.getGenericDeclaration());
         }
      }
   }

   private static final class TypeVariableInvocationHandler implements InvocationHandler {
      private static final ImmutableMap<String, Method> typeVariableMethods;
      private final Types.TypeVariableImpl<?> typeVariableImpl;

      TypeVariableInvocationHandler(Types.TypeVariableImpl<?> var1) {
         super();
         this.typeVariableImpl = var1;
      }

      public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
         String var4 = var2.getName();
         Method var5 = (Method)typeVariableMethods.get(var4);
         if (var5 == null) {
            throw new UnsupportedOperationException(var4);
         } else {
            try {
               return var5.invoke(this.typeVariableImpl, var3);
            } catch (InvocationTargetException var7) {
               throw var7.getCause();
            }
         }
      }

      static {
         ImmutableMap.Builder var0 = ImmutableMap.builder();
         Method[] var1 = Types.TypeVariableImpl.class.getMethods();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Method var4 = var1[var3];
            if (var4.getDeclaringClass().equals(Types.TypeVariableImpl.class)) {
               try {
                  var4.setAccessible(true);
               } catch (AccessControlException var6) {
               }

               var0.put(var4.getName(), var4);
            }
         }

         typeVariableMethods = var0.build();
      }
   }

   private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {
      private final Type ownerType;
      private final ImmutableList<Type> argumentsList;
      private final Class<?> rawType;
      private static final long serialVersionUID = 0L;

      ParameterizedTypeImpl(@Nullable Type var1, Class<?> var2, Type[] var3) {
         super();
         Preconditions.checkNotNull(var2);
         Preconditions.checkArgument(var3.length == var2.getTypeParameters().length);
         Types.disallowPrimitiveType(var3, "type parameter");
         this.ownerType = var1;
         this.rawType = var2;
         this.argumentsList = Types.JavaVersion.CURRENT.usedInGenericType(var3);
      }

      public Type[] getActualTypeArguments() {
         return Types.toArray(this.argumentsList);
      }

      public Type getRawType() {
         return this.rawType;
      }

      public Type getOwnerType() {
         return this.ownerType;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         if (this.ownerType != null) {
            var1.append(Types.JavaVersion.CURRENT.typeName(this.ownerType)).append('.');
         }

         return var1.append(this.rawType.getName()).append('<').append(Types.COMMA_JOINER.join(Iterables.transform(this.argumentsList, Types.TYPE_NAME))).append('>').toString();
      }

      public int hashCode() {
         return (this.ownerType == null ? 0 : this.ownerType.hashCode()) ^ this.argumentsList.hashCode() ^ this.rawType.hashCode();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof ParameterizedType)) {
            return false;
         } else {
            ParameterizedType var2 = (ParameterizedType)var1;
            return this.getRawType().equals(var2.getRawType()) && Objects.equal(this.getOwnerType(), var2.getOwnerType()) && Arrays.equals(this.getActualTypeArguments(), var2.getActualTypeArguments());
         }
      }
   }

   private static final class GenericArrayTypeImpl implements GenericArrayType, Serializable {
      private final Type componentType;
      private static final long serialVersionUID = 0L;

      GenericArrayTypeImpl(Type var1) {
         super();
         this.componentType = Types.JavaVersion.CURRENT.usedInGenericType(var1);
      }

      public Type getGenericComponentType() {
         return this.componentType;
      }

      public String toString() {
         return Types.toString(this.componentType) + "[]";
      }

      public int hashCode() {
         return this.componentType.hashCode();
      }

      public boolean equals(Object var1) {
         if (var1 instanceof GenericArrayType) {
            GenericArrayType var2 = (GenericArrayType)var1;
            return Objects.equal(this.getGenericComponentType(), var2.getGenericComponentType());
         } else {
            return false;
         }
      }
   }

   private static enum ClassOwnership {
      OWNED_BY_ENCLOSING_CLASS {
         @Nullable
         Class<?> getOwnerType(Class<?> var1) {
            return var1.getEnclosingClass();
         }
      },
      LOCAL_CLASS_HAS_NO_OWNER {
         @Nullable
         Class<?> getOwnerType(Class<?> var1) {
            return var1.isLocalClass() ? null : var1.getEnclosingClass();
         }
      };

      static final Types.ClassOwnership JVM_BEHAVIOR = detectJvmBehavior();

      private ClassOwnership() {
      }

      @Nullable
      abstract Class<?> getOwnerType(Class<?> var1);

      private static Types.ClassOwnership detectJvmBehavior() {
         class 1LocalClass<T> {
            _LocalClass/* $FF was: 1LocalClass*/() {
               super();
            }
         }

         Class var0 = (new 1LocalClass<String>() {
         }).getClass();
         ParameterizedType var1 = (ParameterizedType)var0.getGenericSuperclass();
         Types.ClassOwnership[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Types.ClassOwnership var5 = var2[var4];
            if (var5.getOwnerType(1LocalClass.class) == var1.getOwnerType()) {
               return var5;
            }
         }

         throw new AssertionError();
      }

      // $FF: synthetic method
      ClassOwnership(Object var3) {
         this();
      }
   }
}
