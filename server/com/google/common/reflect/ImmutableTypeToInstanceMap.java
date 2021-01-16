package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Map;

@Beta
public final class ImmutableTypeToInstanceMap<B> extends ForwardingMap<TypeToken<? extends B>, B> implements TypeToInstanceMap<B> {
   private final ImmutableMap<TypeToken<? extends B>, B> delegate;

   public static <B> ImmutableTypeToInstanceMap<B> of() {
      return new ImmutableTypeToInstanceMap(ImmutableMap.of());
   }

   public static <B> ImmutableTypeToInstanceMap.Builder<B> builder() {
      return new ImmutableTypeToInstanceMap.Builder();
   }

   private ImmutableTypeToInstanceMap(ImmutableMap<TypeToken<? extends B>, B> var1) {
      super();
      this.delegate = var1;
   }

   public <T extends B> T getInstance(TypeToken<T> var1) {
      return this.trustedGet(var1.rejectTypeVariables());
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public <T extends B> T putInstance(TypeToken<T> var1, T var2) {
      throw new UnsupportedOperationException();
   }

   public <T extends B> T getInstance(Class<T> var1) {
      return this.trustedGet(TypeToken.of(var1));
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public <T extends B> T putInstance(Class<T> var1, T var2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public B put(TypeToken<? extends B> var1, B var2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public void putAll(Map<? extends TypeToken<? extends B>, ? extends B> var1) {
      throw new UnsupportedOperationException();
   }

   protected Map<TypeToken<? extends B>, B> delegate() {
      return this.delegate;
   }

   private <T extends B> T trustedGet(TypeToken<T> var1) {
      return this.delegate.get(var1);
   }

   // $FF: synthetic method
   ImmutableTypeToInstanceMap(ImmutableMap var1, Object var2) {
      this(var1);
   }

   @Beta
   public static final class Builder<B> {
      private final ImmutableMap.Builder<TypeToken<? extends B>, B> mapBuilder;

      private Builder() {
         super();
         this.mapBuilder = ImmutableMap.builder();
      }

      @CanIgnoreReturnValue
      public <T extends B> ImmutableTypeToInstanceMap.Builder<B> put(Class<T> var1, T var2) {
         this.mapBuilder.put(TypeToken.of(var1), var2);
         return this;
      }

      @CanIgnoreReturnValue
      public <T extends B> ImmutableTypeToInstanceMap.Builder<B> put(TypeToken<T> var1, T var2) {
         this.mapBuilder.put(var1.rejectTypeVariables(), var2);
         return this;
      }

      public ImmutableTypeToInstanceMap<B> build() {
         return new ImmutableTypeToInstanceMap(this.mapBuilder.build());
      }

      // $FF: synthetic method
      Builder(Object var1) {
         this();
      }
   }
}
