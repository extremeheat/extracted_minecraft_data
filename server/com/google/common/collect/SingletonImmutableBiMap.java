package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
final class SingletonImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {
   final transient K singleKey;
   final transient V singleValue;
   @LazyInit
   @RetainedWith
   transient ImmutableBiMap<V, K> inverse;

   SingletonImmutableBiMap(K var1, V var2) {
      super();
      CollectPreconditions.checkEntryNotNull(var1, var2);
      this.singleKey = var1;
      this.singleValue = var2;
   }

   private SingletonImmutableBiMap(K var1, V var2, ImmutableBiMap<V, K> var3) {
      super();
      this.singleKey = var1;
      this.singleValue = var2;
      this.inverse = var3;
   }

   public V get(@Nullable Object var1) {
      return this.singleKey.equals(var1) ? this.singleValue : null;
   }

   public int size() {
      return 1;
   }

   public void forEach(BiConsumer<? super K, ? super V> var1) {
      ((BiConsumer)Preconditions.checkNotNull(var1)).accept(this.singleKey, this.singleValue);
   }

   public boolean containsKey(@Nullable Object var1) {
      return this.singleKey.equals(var1);
   }

   public boolean containsValue(@Nullable Object var1) {
      return this.singleValue.equals(var1);
   }

   boolean isPartialView() {
      return false;
   }

   ImmutableSet<Entry<K, V>> createEntrySet() {
      return ImmutableSet.of(Maps.immutableEntry(this.singleKey, this.singleValue));
   }

   ImmutableSet<K> createKeySet() {
      return ImmutableSet.of(this.singleKey);
   }

   public ImmutableBiMap<V, K> inverse() {
      ImmutableBiMap var1 = this.inverse;
      return var1 == null ? (this.inverse = new SingletonImmutableBiMap(this.singleValue, this.singleKey, this)) : var1;
   }
}
