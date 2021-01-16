package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.primitives.Primitives;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@Beta
public abstract class TypeToken<T> extends TypeCapture<T> implements Serializable {
   private final Type runtimeType;
   private transient TypeResolver typeResolver;

   protected TypeToken() {
      super();
      this.runtimeType = this.capture();
      Preconditions.checkState(!(this.runtimeType instanceof TypeVariable), "Cannot construct a TypeToken for a type variable.\nYou probably meant to call new TypeToken<%s>(getClass()) that can resolve the type variable for you.\nIf you do need to create a TypeToken of a type variable, please use TypeToken.of() instead.", (Object)this.runtimeType);
   }

   protected TypeToken(Class<?> var1) {
      super();
      Type var2 = super.capture();
      if (var2 instanceof Class) {
         this.runtimeType = var2;
      } else {
         this.runtimeType = of(var1).resolveType(var2).runtimeType;
      }

   }

   private TypeToken(Type var1) {
      super();
      this.runtimeType = (Type)Preconditions.checkNotNull(var1);
   }

   public static <T> TypeToken<T> of(Class<T> var0) {
      return new TypeToken.SimpleTypeToken(var0);
   }

   public static TypeToken<?> of(Type var0) {
      return new TypeToken.SimpleTypeToken(var0);
   }

   public final Class<? super T> getRawType() {
      Class var1 = (Class)this.getRawTypes().iterator().next();
      return var1;
   }

   public final Type getType() {
      return this.runtimeType;
   }

   public final <X> TypeToken<T> where(TypeParameter<X> var1, TypeToken<X> var2) {
      TypeResolver var3 = (new TypeResolver()).where(ImmutableMap.of(new TypeResolver.TypeVariableKey(var1.typeVariable), var2.runtimeType));
      return new TypeToken.SimpleTypeToken(var3.resolveType(this.runtimeType));
   }

   public final <X> TypeToken<T> where(TypeParameter<X> var1, Class<X> var2) {
      return this.where(var1, of(var2));
   }

   public final TypeToken<?> resolveType(Type var1) {
      Preconditions.checkNotNull(var1);
      TypeResolver var2 = this.typeResolver;
      if (var2 == null) {
         var2 = this.typeResolver = TypeResolver.accordingTo(this.runtimeType);
      }

      return of(var2.resolveType(var1));
   }

   private Type[] resolveInPlace(Type[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = this.resolveType(var1[var2]).getType();
      }

      return var1;
   }

   private TypeToken<?> resolveSupertype(Type var1) {
      TypeToken var2 = this.resolveType(var1);
      var2.typeResolver = this.typeResolver;
      return var2;
   }

   @Nullable
   final TypeToken<? super T> getGenericSuperclass() {
      if (this.runtimeType instanceof TypeVariable) {
         return this.boundAsSuperclass(((TypeVariable)this.runtimeType).getBounds()[0]);
      } else if (this.runtimeType instanceof WildcardType) {
         return this.boundAsSuperclass(((WildcardType)this.runtimeType).getUpperBounds()[0]);
      } else {
         Type var1 = this.getRawType().getGenericSuperclass();
         if (var1 == null) {
            return null;
         } else {
            TypeToken var2 = this.resolveSupertype(var1);
            return var2;
         }
      }
   }

   @Nullable
   private TypeToken<? super T> boundAsSuperclass(Type var1) {
      TypeToken var2 = of(var1);
      return var2.getRawType().isInterface() ? null : var2;
   }

   final ImmutableList<TypeToken<? super T>> getGenericInterfaces() {
      if (this.runtimeType instanceof TypeVariable) {
         return this.boundsAsInterfaces(((TypeVariable)this.runtimeType).getBounds());
      } else if (this.runtimeType instanceof WildcardType) {
         return this.boundsAsInterfaces(((WildcardType)this.runtimeType).getUpperBounds());
      } else {
         ImmutableList.Builder var1 = ImmutableList.builder();
         Type[] var2 = this.getRawType().getGenericInterfaces();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Type var5 = var2[var4];
            TypeToken var6 = this.resolveSupertype(var5);
            var1.add((Object)var6);
         }

         return var1.build();
      }
   }

   private ImmutableList<TypeToken<? super T>> boundsAsInterfaces(Type[] var1) {
      ImmutableList.Builder var2 = ImmutableList.builder();
      Type[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Type var6 = var3[var5];
         TypeToken var7 = of(var6);
         if (var7.getRawType().isInterface()) {
            var2.add((Object)var7);
         }
      }

      return var2.build();
   }

   public final TypeToken<T>.TypeSet getTypes() {
      return new TypeToken.TypeSet();
   }

   public final TypeToken<? super T> getSupertype(Class<? super T> var1) {
      Preconditions.checkArgument(this.someRawTypeIsSubclassOf(var1), "%s is not a super class of %s", var1, this);
      if (this.runtimeType instanceof TypeVariable) {
         return this.getSupertypeFromUpperBounds(var1, ((TypeVariable)this.runtimeType).getBounds());
      } else if (this.runtimeType instanceof WildcardType) {
         return this.getSupertypeFromUpperBounds(var1, ((WildcardType)this.runtimeType).getUpperBounds());
      } else if (var1.isArray()) {
         return this.getArraySupertype(var1);
      } else {
         TypeToken var2 = this.resolveSupertype(toGenericType(var1).runtimeType);
         return var2;
      }
   }

   public final TypeToken<? extends T> getSubtype(Class<?> var1) {
      Preconditions.checkArgument(!(this.runtimeType instanceof TypeVariable), "Cannot get subtype of type variable <%s>", (Object)this);
      if (this.runtimeType instanceof WildcardType) {
         return this.getSubtypeFromLowerBounds(var1, ((WildcardType)this.runtimeType).getLowerBounds());
      } else if (this.isArray()) {
         return this.getArraySubtype(var1);
      } else {
         Preconditions.checkArgument(this.getRawType().isAssignableFrom(var1), "%s isn't a subclass of %s", var1, this);
         Type var2 = this.resolveTypeArgsForSubclass(var1);
         TypeToken var3 = of(var2);
         return var3;
      }
   }

   public final boolean isSupertypeOf(TypeToken<?> var1) {
      return var1.isSubtypeOf(this.getType());
   }

   public final boolean isSupertypeOf(Type var1) {
      return of(var1).isSubtypeOf(this.getType());
   }

   public final boolean isSubtypeOf(TypeToken<?> var1) {
      return this.isSubtypeOf(var1.getType());
   }

   public final boolean isSubtypeOf(Type var1) {
      Preconditions.checkNotNull(var1);
      if (var1 instanceof WildcardType) {
         return any(((WildcardType)var1).getLowerBounds()).isSupertypeOf(this.runtimeType);
      } else if (this.runtimeType instanceof WildcardType) {
         return any(((WildcardType)this.runtimeType).getUpperBounds()).isSubtypeOf(var1);
      } else if (!(this.runtimeType instanceof TypeVariable)) {
         if (this.runtimeType instanceof GenericArrayType) {
            return of(var1).isSupertypeOfArray((GenericArrayType)this.runtimeType);
         } else if (var1 instanceof Class) {
            return this.someRawTypeIsSubclassOf((Class)var1);
         } else if (var1 instanceof ParameterizedType) {
            return this.isSubtypeOfParameterizedType((ParameterizedType)var1);
         } else {
            return var1 instanceof GenericArrayType ? this.isSubtypeOfArrayType((GenericArrayType)var1) : false;
         }
      } else {
         return this.runtimeType.equals(var1) || any(((TypeVariable)this.runtimeType).getBounds()).isSubtypeOf(var1);
      }
   }

   public final boolean isArray() {
      return this.getComponentType() != null;
   }

   public final boolean isPrimitive() {
      return this.runtimeType instanceof Class && ((Class)this.runtimeType).isPrimitive();
   }

   public final TypeToken<T> wrap() {
      if (this.isPrimitive()) {
         Class var1 = (Class)this.runtimeType;
         return of(Primitives.wrap(var1));
      } else {
         return this;
      }
   }

   private boolean isWrapper() {
      return Primitives.allWrapperTypes().contains(this.runtimeType);
   }

   public final TypeToken<T> unwrap() {
      if (this.isWrapper()) {
         Class var1 = (Class)this.runtimeType;
         return of(Primitives.unwrap(var1));
      } else {
         return this;
      }
   }

   @Nullable
   public final TypeToken<?> getComponentType() {
      Type var1 = Types.getComponentType(this.runtimeType);
      return var1 == null ? null : of(var1);
   }

   public final Invokable<T, Object> method(Method var1) {
      Preconditions.checkArgument(this.someRawTypeIsSubclassOf(var1.getDeclaringClass()), "%s not declared by %s", var1, this);
      return new Invokable.MethodInvokable<T>(var1) {
         Type getGenericReturnType() {
            return TypeToken.this.resolveType(super.getGenericReturnType()).getType();
         }

         Type[] getGenericParameterTypes() {
            return TypeToken.this.resolveInPlace(super.getGenericParameterTypes());
         }

         Type[] getGenericExceptionTypes() {
            return TypeToken.this.resolveInPlace(super.getGenericExceptionTypes());
         }

         public TypeToken<T> getOwnerType() {
            return TypeToken.this;
         }

         public String toString() {
            return this.getOwnerType() + "." + super.toString();
         }
      };
   }

   public final Invokable<T, T> constructor(Constructor<?> var1) {
      Preconditions.checkArgument(var1.getDeclaringClass() == this.getRawType(), "%s not declared by %s", var1, this.getRawType());
      return new Invokable.ConstructorInvokable<T>(var1) {
         Type getGenericReturnType() {
            return TypeToken.this.resolveType(super.getGenericReturnType()).getType();
         }

         Type[] getGenericParameterTypes() {
            return TypeToken.this.resolveInPlace(super.getGenericParameterTypes());
         }

         Type[] getGenericExceptionTypes() {
            return TypeToken.this.resolveInPlace(super.getGenericExceptionTypes());
         }

         public TypeToken<T> getOwnerType() {
            return TypeToken.this;
         }

         public String toString() {
            return this.getOwnerType() + "(" + Joiner.on(", ").join((Object[])this.getGenericParameterTypes()) + ")";
         }
      };
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 instanceof TypeToken) {
         TypeToken var2 = (TypeToken)var1;
         return this.runtimeType.equals(var2.runtimeType);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.runtimeType.hashCode();
   }

   public String toString() {
      return Types.toString(this.runtimeType);
   }

   protected Object writeReplace() {
      return of((new TypeResolver()).resolveType(this.runtimeType));
   }

   @CanIgnoreReturnValue
   final TypeToken<T> rejectTypeVariables() {
      (new TypeVisitor() {
         void visitTypeVariable(TypeVariable<?> var1) {
            throw new IllegalArgumentException(TypeToken.this.runtimeType + "contains a type variable and is not safe for the operation");
         }

         void visitWildcardType(WildcardType var1) {
            this.visit(var1.getLowerBounds());
            this.visit(var1.getUpperBounds());
         }

         void visitParameterizedType(ParameterizedType var1) {
            this.visit(var1.getActualTypeArguments());
            this.visit(new Type[]{var1.getOwnerType()});
         }

         void visitGenericArrayType(GenericArrayType var1) {
            this.visit(new Type[]{var1.getGenericComponentType()});
         }
      }).visit(new Type[]{this.runtimeType});
      return this;
   }

   private boolean someRawTypeIsSubclassOf(Class<?> var1) {
      UnmodifiableIterator var2 = this.getRawTypes().iterator();

      Class var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Class)var2.next();
      } while(!var1.isAssignableFrom(var3));

      return true;
   }

   private boolean isSubtypeOfParameterizedType(ParameterizedType var1) {
      Class var2 = of((Type)var1).getRawType();
      if (!this.someRawTypeIsSubclassOf(var2)) {
         return false;
      } else {
         TypeVariable[] var3 = var2.getTypeParameters();
         Type[] var4 = var1.getActualTypeArguments();

         for(int var5 = 0; var5 < var3.length; ++var5) {
            if (!this.resolveType(var3[var5]).is(var4[var5])) {
               return false;
            }
         }

         return Modifier.isStatic(((Class)var1.getRawType()).getModifiers()) || var1.getOwnerType() == null || this.isOwnedBySubtypeOf(var1.getOwnerType());
      }
   }

   private boolean isSubtypeOfArrayType(GenericArrayType var1) {
      if (this.runtimeType instanceof Class) {
         Class var3 = (Class)this.runtimeType;
         return !var3.isArray() ? false : of(var3.getComponentType()).isSubtypeOf(var1.getGenericComponentType());
      } else if (this.runtimeType instanceof GenericArrayType) {
         GenericArrayType var2 = (GenericArrayType)this.runtimeType;
         return of(var2.getGenericComponentType()).isSubtypeOf(var1.getGenericComponentType());
      } else {
         return false;
      }
   }

   private boolean isSupertypeOfArray(GenericArrayType var1) {
      if (this.runtimeType instanceof Class) {
         Class var2 = (Class)this.runtimeType;
         return !var2.isArray() ? var2.isAssignableFrom(Object[].class) : of(var1.getGenericComponentType()).isSubtypeOf((Type)var2.getComponentType());
      } else {
         return this.runtimeType instanceof GenericArrayType ? of(var1.getGenericComponentType()).isSubtypeOf(((GenericArrayType)this.runtimeType).getGenericComponentType()) : false;
      }
   }

   private boolean is(Type var1) {
      if (this.runtimeType.equals(var1)) {
         return true;
      } else if (!(var1 instanceof WildcardType)) {
         return false;
      } else {
         return every(((WildcardType)var1).getUpperBounds()).isSupertypeOf(this.runtimeType) && every(((WildcardType)var1).getLowerBounds()).isSubtypeOf(this.runtimeType);
      }
   }

   private static TypeToken.Bounds every(Type[] var0) {
      return new TypeToken.Bounds(var0, false);
   }

   private static TypeToken.Bounds any(Type[] var0) {
      return new TypeToken.Bounds(var0, true);
   }

   private ImmutableSet<Class<? super T>> getRawTypes() {
      final ImmutableSet.Builder var1 = ImmutableSet.builder();
      (new TypeVisitor() {
         void visitTypeVariable(TypeVariable<?> var1x) {
            this.visit(var1x.getBounds());
         }

         void visitWildcardType(WildcardType var1x) {
            this.visit(var1x.getUpperBounds());
         }

         void visitParameterizedType(ParameterizedType var1x) {
            var1.add((Object)((Class)var1x.getRawType()));
         }

         void visitClass(Class<?> var1x) {
            var1.add((Object)var1x);
         }

         void visitGenericArrayType(GenericArrayType var1x) {
            var1.add((Object)Types.getArrayClass(TypeToken.of(var1x.getGenericComponentType()).getRawType()));
         }
      }).visit(new Type[]{this.runtimeType});
      ImmutableSet var2 = var1.build();
      return var2;
   }

   private boolean isOwnedBySubtypeOf(Type var1) {
      Iterator var2 = this.getTypes().iterator();

      Type var4;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         TypeToken var3 = (TypeToken)var2.next();
         var4 = var3.getOwnerTypeIfPresent();
      } while(var4 == null || !of(var4).isSubtypeOf(var1));

      return true;
   }

   @Nullable
   private Type getOwnerTypeIfPresent() {
      if (this.runtimeType instanceof ParameterizedType) {
         return ((ParameterizedType)this.runtimeType).getOwnerType();
      } else {
         return this.runtimeType instanceof Class ? ((Class)this.runtimeType).getEnclosingClass() : null;
      }
   }

   @VisibleForTesting
   static <T> TypeToken<? extends T> toGenericType(Class<T> var0) {
      if (var0.isArray()) {
         Type var4 = Types.newArrayType(toGenericType(var0.getComponentType()).runtimeType);
         TypeToken var5 = of(var4);
         return var5;
      } else {
         TypeVariable[] var1 = var0.getTypeParameters();
         Type var2 = var0.isMemberClass() && !Modifier.isStatic(var0.getModifiers()) ? toGenericType(var0.getEnclosingClass()).runtimeType : null;
         if (var1.length <= 0 && (var2 == null || var2 == var0.getEnclosingClass())) {
            return of(var0);
         } else {
            TypeToken var3 = of((Type)Types.newParameterizedTypeWithOwner(var2, var0, var1));
            return var3;
         }
      }
   }

   private TypeToken<? super T> getSupertypeFromUpperBounds(Class<? super T> var1, Type[] var2) {
      Type[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Type var6 = var3[var5];
         TypeToken var7 = of(var6);
         if (var7.isSubtypeOf((Type)var1)) {
            TypeToken var8 = var7.getSupertype(var1);
            return var8;
         }
      }

      throw new IllegalArgumentException(var1 + " isn't a super type of " + this);
   }

   private TypeToken<? extends T> getSubtypeFromLowerBounds(Class<?> var1, Type[] var2) {
      int var4 = var2.length;
      byte var5 = 0;
      if (var5 < var4) {
         Type var6 = var2[var5];
         TypeToken var7 = of(var6);
         return var7.getSubtype(var1);
      } else {
         throw new IllegalArgumentException(var1 + " isn't a subclass of " + this);
      }
   }

   private TypeToken<? super T> getArraySupertype(Class<? super T> var1) {
      TypeToken var2 = (TypeToken)Preconditions.checkNotNull(this.getComponentType(), "%s isn't a super type of %s", var1, this);
      TypeToken var3 = var2.getSupertype(var1.getComponentType());
      TypeToken var4 = of(newArrayClassOrGenericArrayType(var3.runtimeType));
      return var4;
   }

   private TypeToken<? extends T> getArraySubtype(Class<?> var1) {
      TypeToken var2 = this.getComponentType().getSubtype(var1.getComponentType());
      TypeToken var3 = of(newArrayClassOrGenericArrayType(var2.runtimeType));
      return var3;
   }

   private Type resolveTypeArgsForSubclass(Class<?> var1) {
      if (!(this.runtimeType instanceof Class) || var1.getTypeParameters().length != 0 && this.getRawType().getTypeParameters().length == 0) {
         TypeToken var2 = toGenericType(var1);
         Type var3 = var2.getSupertype(this.getRawType()).runtimeType;
         return (new TypeResolver()).where(var3, this.runtimeType).resolveType(var2.runtimeType);
      } else {
         return var1;
      }
   }

   private static Type newArrayClassOrGenericArrayType(Type var0) {
      return Types.JavaVersion.JAVA7.newArrayType(var0);
   }

   // $FF: synthetic method
   TypeToken(Type var1, Object var2) {
      this(var1);
   }

   private abstract static class TypeCollector<K> {
      static final TypeToken.TypeCollector<TypeToken<?>> FOR_GENERIC_TYPE = new TypeToken.TypeCollector<TypeToken<?>>() {
         Class<?> getRawType(TypeToken<?> var1) {
            return var1.getRawType();
         }

         Iterable<? extends TypeToken<?>> getInterfaces(TypeToken<?> var1) {
            return var1.getGenericInterfaces();
         }

         @Nullable
         TypeToken<?> getSuperclass(TypeToken<?> var1) {
            return var1.getGenericSuperclass();
         }
      };
      static final TypeToken.TypeCollector<Class<?>> FOR_RAW_TYPE = new TypeToken.TypeCollector<Class<?>>() {
         Class<?> getRawType(Class<?> var1) {
            return var1;
         }

         Iterable<? extends Class<?>> getInterfaces(Class<?> var1) {
            return Arrays.asList(var1.getInterfaces());
         }

         @Nullable
         Class<?> getSuperclass(Class<?> var1) {
            return var1.getSuperclass();
         }
      };

      private TypeCollector() {
         super();
      }

      final TypeToken.TypeCollector<K> classesOnly() {
         return new TypeToken.TypeCollector.ForwardingTypeCollector<K>(this) {
            Iterable<? extends K> getInterfaces(K var1) {
               return ImmutableSet.of();
            }

            ImmutableList<K> collectTypes(Iterable<? extends K> var1) {
               ImmutableList.Builder var2 = ImmutableList.builder();
               Iterator var3 = var1.iterator();

               while(var3.hasNext()) {
                  Object var4 = var3.next();
                  if (!this.getRawType(var4).isInterface()) {
                     var2.add(var4);
                  }
               }

               return super.collectTypes(var2.build());
            }
         };
      }

      final ImmutableList<K> collectTypes(K var1) {
         return this.collectTypes((Iterable)ImmutableList.of(var1));
      }

      ImmutableList<K> collectTypes(Iterable<? extends K> var1) {
         HashMap var2 = Maps.newHashMap();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            this.collectTypes(var4, var2);
         }

         return sortKeysByValue(var2, Ordering.natural().reverse());
      }

      @CanIgnoreReturnValue
      private int collectTypes(K var1, Map<? super K, Integer> var2) {
         Integer var3 = (Integer)var2.get(var1);
         if (var3 != null) {
            return var3;
         } else {
            int var4 = this.getRawType(var1).isInterface() ? 1 : 0;

            Object var6;
            for(Iterator var5 = this.getInterfaces(var1).iterator(); var5.hasNext(); var4 = Math.max(var4, this.collectTypes(var6, var2))) {
               var6 = var5.next();
            }

            Object var7 = this.getSuperclass(var1);
            if (var7 != null) {
               var4 = Math.max(var4, this.collectTypes(var7, var2));
            }

            var2.put(var1, var4 + 1);
            return var4 + 1;
         }
      }

      private static <K, V> ImmutableList<K> sortKeysByValue(final Map<K, V> var0, final Comparator<? super V> var1) {
         Ordering var2 = new Ordering<K>() {
            public int compare(K var1x, K var2) {
               return var1.compare(var0.get(var1x), var0.get(var2));
            }
         };
         return var2.immutableSortedCopy(var0.keySet());
      }

      abstract Class<?> getRawType(K var1);

      abstract Iterable<? extends K> getInterfaces(K var1);

      @Nullable
      abstract K getSuperclass(K var1);

      // $FF: synthetic method
      TypeCollector(Object var1) {
         this();
      }

      private static class ForwardingTypeCollector<K> extends TypeToken.TypeCollector<K> {
         private final TypeToken.TypeCollector<K> delegate;

         ForwardingTypeCollector(TypeToken.TypeCollector<K> var1) {
            super(null);
            this.delegate = var1;
         }

         Class<?> getRawType(K var1) {
            return this.delegate.getRawType(var1);
         }

         Iterable<? extends K> getInterfaces(K var1) {
            return this.delegate.getInterfaces(var1);
         }

         K getSuperclass(K var1) {
            return this.delegate.getSuperclass(var1);
         }
      }
   }

   private static final class SimpleTypeToken<T> extends TypeToken<T> {
      private static final long serialVersionUID = 0L;

      SimpleTypeToken(Type var1) {
         super(var1, null);
      }
   }

   private static class Bounds {
      private final Type[] bounds;
      private final boolean target;

      Bounds(Type[] var1, boolean var2) {
         super();
         this.bounds = var1;
         this.target = var2;
      }

      boolean isSubtypeOf(Type var1) {
         Type[] var2 = this.bounds;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Type var5 = var2[var4];
            if (TypeToken.of(var5).isSubtypeOf(var1) == this.target) {
               return this.target;
            }
         }

         return !this.target;
      }

      boolean isSupertypeOf(Type var1) {
         TypeToken var2 = TypeToken.of(var1);
         Type[] var3 = this.bounds;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Type var6 = var3[var5];
            if (var2.isSubtypeOf(var6) == this.target) {
               return this.target;
            }
         }

         return !this.target;
      }
   }

   private static enum TypeFilter implements Predicate<TypeToken<?>> {
      IGNORE_TYPE_VARIABLE_OR_WILDCARD {
         public boolean apply(TypeToken<?> var1) {
            return !(var1.runtimeType instanceof TypeVariable) && !(var1.runtimeType instanceof WildcardType);
         }
      },
      INTERFACE_ONLY {
         public boolean apply(TypeToken<?> var1) {
            return var1.getRawType().isInterface();
         }
      };

      private TypeFilter() {
      }

      // $FF: synthetic method
      TypeFilter(Object var3) {
         this();
      }
   }

   private final class ClassSet extends TypeToken<T>.TypeSet {
      private transient ImmutableSet<TypeToken<? super T>> classes;
      private static final long serialVersionUID = 0L;

      private ClassSet() {
         super();
      }

      protected Set<TypeToken<? super T>> delegate() {
         ImmutableSet var1 = this.classes;
         if (var1 == null) {
            ImmutableList var2 = TypeToken.TypeCollector.FOR_GENERIC_TYPE.classesOnly().collectTypes((Object)TypeToken.this);
            return this.classes = FluentIterable.from((Iterable)var2).filter((Predicate)TypeToken.TypeFilter.IGNORE_TYPE_VARIABLE_OR_WILDCARD).toSet();
         } else {
            return var1;
         }
      }

      public TypeToken<T>.TypeSet classes() {
         return this;
      }

      public Set<Class<? super T>> rawTypes() {
         ImmutableList var1 = TypeToken.TypeCollector.FOR_RAW_TYPE.classesOnly().collectTypes((Iterable)TypeToken.this.getRawTypes());
         return ImmutableSet.copyOf((Collection)var1);
      }

      public TypeToken<T>.TypeSet interfaces() {
         throw new UnsupportedOperationException("classes().interfaces() not supported.");
      }

      private Object readResolve() {
         return TypeToken.this.getTypes().classes();
      }

      // $FF: synthetic method
      ClassSet(Object var2) {
         this();
      }
   }

   private final class InterfaceSet extends TypeToken<T>.TypeSet {
      private final transient TypeToken<T>.TypeSet allTypes;
      private transient ImmutableSet<TypeToken<? super T>> interfaces;
      private static final long serialVersionUID = 0L;

      InterfaceSet(TypeToken<T>.TypeSet var2) {
         super();
         this.allTypes = var2;
      }

      protected Set<TypeToken<? super T>> delegate() {
         ImmutableSet var1 = this.interfaces;
         return var1 == null ? (this.interfaces = FluentIterable.from((Iterable)this.allTypes).filter((Predicate)TypeToken.TypeFilter.INTERFACE_ONLY).toSet()) : var1;
      }

      public TypeToken<T>.TypeSet interfaces() {
         return this;
      }

      public Set<Class<? super T>> rawTypes() {
         ImmutableList var1 = TypeToken.TypeCollector.FOR_RAW_TYPE.collectTypes((Iterable)TypeToken.this.getRawTypes());
         return FluentIterable.from((Iterable)var1).filter(new Predicate<Class<?>>() {
            public boolean apply(Class<?> var1) {
               return var1.isInterface();
            }
         }).toSet();
      }

      public TypeToken<T>.TypeSet classes() {
         throw new UnsupportedOperationException("interfaces().classes() not supported.");
      }

      private Object readResolve() {
         return TypeToken.this.getTypes().interfaces();
      }
   }

   public class TypeSet extends ForwardingSet<TypeToken<? super T>> implements Serializable {
      private transient ImmutableSet<TypeToken<? super T>> types;
      private static final long serialVersionUID = 0L;

      TypeSet() {
         super();
      }

      public TypeToken<T>.TypeSet interfaces() {
         return TypeToken.this.new InterfaceSet(this);
      }

      public TypeToken<T>.TypeSet classes() {
         return TypeToken.this.new ClassSet();
      }

      protected Set<TypeToken<? super T>> delegate() {
         ImmutableSet var1 = this.types;
         if (var1 == null) {
            ImmutableList var2 = TypeToken.TypeCollector.FOR_GENERIC_TYPE.collectTypes((Object)TypeToken.this);
            return this.types = FluentIterable.from((Iterable)var2).filter((Predicate)TypeToken.TypeFilter.IGNORE_TYPE_VARIABLE_OR_WILDCARD).toSet();
         } else {
            return var1;
         }
      }

      public Set<Class<? super T>> rawTypes() {
         ImmutableList var1 = TypeToken.TypeCollector.FOR_RAW_TYPE.collectTypes((Iterable)TypeToken.this.getRawTypes());
         return ImmutableSet.copyOf((Collection)var1);
      }
   }
}
