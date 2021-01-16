package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.j2objc.annotations.Weak;
import java.io.Serializable;
import java.util.Spliterator;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class ImmutableMapKeySet<K, V> extends ImmutableSet.Indexed<K> {
   @Weak
   private final ImmutableMap<K, V> map;

   ImmutableMapKeySet(ImmutableMap<K, V> var1) {
      super();
      this.map = var1;
   }

   public int size() {
      return this.map.size();
   }

   public UnmodifiableIterator<K> iterator() {
      return this.map.keyIterator();
   }

   public Spliterator<K> spliterator() {
      return this.map.keySpliterator();
   }

   public boolean contains(@Nullable Object var1) {
      return this.map.containsKey(var1);
   }

   K get(int var1) {
      return ((Entry)this.map.entrySet().asList().get(var1)).getKey();
   }

   public void forEach(Consumer<? super K> var1) {
      Preconditions.checkNotNull(var1);
      this.map.forEach((var1x, var2) -> {
         var1.accept(var1x);
      });
   }

   boolean isPartialView() {
      return true;
   }

   @GwtIncompatible
   Object writeReplace() {
      return new ImmutableMapKeySet.KeySetSerializedForm(this.map);
   }

   @GwtIncompatible
   private static class KeySetSerializedForm<K> implements Serializable {
      final ImmutableMap<K, ?> map;
      private static final long serialVersionUID = 0L;

      KeySetSerializedForm(ImmutableMap<K, ?> var1) {
         super();
         this.map = var1;
      }

      Object readResolve() {
         return this.map.keySet();
      }
   }
}
