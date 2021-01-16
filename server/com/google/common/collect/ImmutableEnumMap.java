package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Spliterator;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
final class ImmutableEnumMap<K extends Enum<K>, V> extends ImmutableMap.IteratorBasedImmutableMap<K, V> {
   private final transient EnumMap<K, V> delegate;

   static <K extends Enum<K>, V> ImmutableMap<K, V> asImmutable(EnumMap<K, V> var0) {
      switch(var0.size()) {
      case 0:
         return ImmutableMap.of();
      case 1:
         Entry var1 = (Entry)Iterables.getOnlyElement(var0.entrySet());
         return ImmutableMap.of(var1.getKey(), var1.getValue());
      default:
         return new ImmutableEnumMap(var0);
      }
   }

   private ImmutableEnumMap(EnumMap<K, V> var1) {
      super();
      this.delegate = var1;
      Preconditions.checkArgument(!var1.isEmpty());
   }

   UnmodifiableIterator<K> keyIterator() {
      return Iterators.unmodifiableIterator(this.delegate.keySet().iterator());
   }

   Spliterator<K> keySpliterator() {
      return this.delegate.keySet().spliterator();
   }

   public int size() {
      return this.delegate.size();
   }

   public boolean containsKey(@Nullable Object var1) {
      return this.delegate.containsKey(var1);
   }

   public V get(Object var1) {
      return this.delegate.get(var1);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         if (var1 instanceof ImmutableEnumMap) {
            var1 = ((ImmutableEnumMap)var1).delegate;
         }

         return this.delegate.equals(var1);
      }
   }

   UnmodifiableIterator<Entry<K, V>> entryIterator() {
      return Maps.unmodifiableEntryIterator(this.delegate.entrySet().iterator());
   }

   Spliterator<Entry<K, V>> entrySpliterator() {
      return CollectSpliterators.map(this.delegate.entrySet().spliterator(), Maps::unmodifiableEntry);
   }

   public void forEach(BiConsumer<? super K, ? super V> var1) {
      this.delegate.forEach(var1);
   }

   boolean isPartialView() {
      return false;
   }

   Object writeReplace() {
      return new ImmutableEnumMap.EnumSerializedForm(this.delegate);
   }

   // $FF: synthetic method
   ImmutableEnumMap(EnumMap var1, Object var2) {
      this(var1);
   }

   private static class EnumSerializedForm<K extends Enum<K>, V> implements Serializable {
      final EnumMap<K, V> delegate;
      private static final long serialVersionUID = 0L;

      EnumSerializedForm(EnumMap<K, V> var1) {
         super();
         this.delegate = var1;
      }

      Object readResolve() {
         return new ImmutableEnumMap(this.delegate);
      }
   }
}
