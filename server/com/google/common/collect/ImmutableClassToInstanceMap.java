package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Primitives;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtIncompatible
public final class ImmutableClassToInstanceMap<B> extends ForwardingMap<Class<? extends B>, B> implements ClassToInstanceMap<B>, Serializable {
   private static final ImmutableClassToInstanceMap<Object> EMPTY = new ImmutableClassToInstanceMap(ImmutableMap.of());
   private final ImmutableMap<Class<? extends B>, B> delegate;

   public static <B> ImmutableClassToInstanceMap<B> of() {
      return EMPTY;
   }

   public static <B, T extends B> ImmutableClassToInstanceMap<B> of(Class<T> var0, T var1) {
      ImmutableMap var2 = ImmutableMap.of(var0, var1);
      return new ImmutableClassToInstanceMap(var2);
   }

   public static <B> ImmutableClassToInstanceMap.Builder<B> builder() {
      return new ImmutableClassToInstanceMap.Builder();
   }

   public static <B, S extends B> ImmutableClassToInstanceMap<B> copyOf(Map<? extends Class<? extends S>, ? extends S> var0) {
      if (var0 instanceof ImmutableClassToInstanceMap) {
         ImmutableClassToInstanceMap var1 = (ImmutableClassToInstanceMap)var0;
         return var1;
      } else {
         return (new ImmutableClassToInstanceMap.Builder()).putAll(var0).build();
      }
   }

   private ImmutableClassToInstanceMap(ImmutableMap<Class<? extends B>, B> var1) {
      super();
      this.delegate = var1;
   }

   protected Map<Class<? extends B>, B> delegate() {
      return this.delegate;
   }

   @Nullable
   public <T extends B> T getInstance(Class<T> var1) {
      return this.delegate.get(Preconditions.checkNotNull(var1));
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public <T extends B> T putInstance(Class<T> var1, T var2) {
      throw new UnsupportedOperationException();
   }

   Object readResolve() {
      return this.isEmpty() ? of() : this;
   }

   // $FF: synthetic method
   ImmutableClassToInstanceMap(ImmutableMap var1, Object var2) {
      this(var1);
   }

   public static final class Builder<B> {
      private final ImmutableMap.Builder<Class<? extends B>, B> mapBuilder = ImmutableMap.builder();

      public Builder() {
         super();
      }

      @CanIgnoreReturnValue
      public <T extends B> ImmutableClassToInstanceMap.Builder<B> put(Class<T> var1, T var2) {
         this.mapBuilder.put(var1, var2);
         return this;
      }

      @CanIgnoreReturnValue
      public <T extends B> ImmutableClassToInstanceMap.Builder<B> putAll(Map<? extends Class<? extends T>, ? extends T> var1) {
         Iterator var2 = var1.entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            Class var4 = (Class)var3.getKey();
            Object var5 = var3.getValue();
            this.mapBuilder.put(var4, cast(var4, var5));
         }

         return this;
      }

      private static <B, T extends B> T cast(Class<T> var0, B var1) {
         return Primitives.wrap(var0).cast(var1);
      }

      public ImmutableClassToInstanceMap<B> build() {
         ImmutableMap var1 = this.mapBuilder.build();
         return var1.isEmpty() ? ImmutableClassToInstanceMap.of() : new ImmutableClassToInstanceMap(var1);
      }
   }
}
