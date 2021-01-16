package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.j2objc.annotations.Weak;
import java.io.Serializable;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
abstract class ImmutableMapEntrySet<K, V> extends ImmutableSet<Entry<K, V>> {
   ImmutableMapEntrySet() {
      super();
   }

   abstract ImmutableMap<K, V> map();

   public int size() {
      return this.map().size();
   }

   public boolean contains(@Nullable Object var1) {
      if (!(var1 instanceof Entry)) {
         return false;
      } else {
         Entry var2 = (Entry)var1;
         Object var3 = this.map().get(var2.getKey());
         return var3 != null && var3.equals(var2.getValue());
      }
   }

   boolean isPartialView() {
      return this.map().isPartialView();
   }

   @GwtIncompatible
   boolean isHashCodeFast() {
      return this.map().isHashCodeFast();
   }

   public int hashCode() {
      return this.map().hashCode();
   }

   @GwtIncompatible
   Object writeReplace() {
      return new ImmutableMapEntrySet.EntrySetSerializedForm(this.map());
   }

   @GwtIncompatible
   private static class EntrySetSerializedForm<K, V> implements Serializable {
      final ImmutableMap<K, V> map;
      private static final long serialVersionUID = 0L;

      EntrySetSerializedForm(ImmutableMap<K, V> var1) {
         super();
         this.map = var1;
      }

      Object readResolve() {
         return this.map.entrySet();
      }
   }

   static final class RegularEntrySet<K, V> extends ImmutableMapEntrySet<K, V> {
      @Weak
      private final transient ImmutableMap<K, V> map;
      private final transient Entry<K, V>[] entries;

      RegularEntrySet(ImmutableMap<K, V> var1, Entry<K, V>[] var2) {
         super();
         this.map = var1;
         this.entries = var2;
      }

      ImmutableMap<K, V> map() {
         return this.map;
      }

      public UnmodifiableIterator<Entry<K, V>> iterator() {
         return Iterators.forArray(this.entries);
      }

      public Spliterator<Entry<K, V>> spliterator() {
         return Spliterators.spliterator(this.entries, 1297);
      }

      public void forEach(Consumer<? super Entry<K, V>> var1) {
         Preconditions.checkNotNull(var1);
         Entry[] var2 = this.entries;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Entry var5 = var2[var4];
            var1.accept(var5);
         }

      }

      ImmutableList<Entry<K, V>> createAsList() {
         return new RegularImmutableAsList(this, this.entries);
      }
   }
}
