package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.j2objc.annotations.Weak;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public abstract class ImmutableMultimap<K, V> extends AbstractMultimap<K, V> implements Serializable {
   final transient ImmutableMap<K, ? extends ImmutableCollection<V>> map;
   final transient int size;
   private static final long serialVersionUID = 0L;

   public static <K, V> ImmutableMultimap<K, V> of() {
      return ImmutableListMultimap.of();
   }

   public static <K, V> ImmutableMultimap<K, V> of(K var0, V var1) {
      return ImmutableListMultimap.of(var0, var1);
   }

   public static <K, V> ImmutableMultimap<K, V> of(K var0, V var1, K var2, V var3) {
      return ImmutableListMultimap.of(var0, var1, var2, var3);
   }

   public static <K, V> ImmutableMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5) {
      return ImmutableListMultimap.of(var0, var1, var2, var3, var4, var5);
   }

   public static <K, V> ImmutableMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7) {
      return ImmutableListMultimap.of(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public static <K, V> ImmutableMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9) {
      return ImmutableListMultimap.of(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public static <K, V> ImmutableMultimap.Builder<K, V> builder() {
      return new ImmutableMultimap.Builder();
   }

   public static <K, V> ImmutableMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> var0) {
      if (var0 instanceof ImmutableMultimap) {
         ImmutableMultimap var1 = (ImmutableMultimap)var0;
         if (!var1.isPartialView()) {
            return var1;
         }
      }

      return ImmutableListMultimap.copyOf(var0);
   }

   @Beta
   public static <K, V> ImmutableMultimap<K, V> copyOf(Iterable<? extends Entry<? extends K, ? extends V>> var0) {
      return ImmutableListMultimap.copyOf(var0);
   }

   ImmutableMultimap(ImmutableMap<K, ? extends ImmutableCollection<V>> var1, int var2) {
      super();
      this.map = var1;
      this.size = var2;
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public ImmutableCollection<V> removeAll(Object var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public ImmutableCollection<V> replaceValues(K var1, Iterable<? extends V> var2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public void clear() {
      throw new UnsupportedOperationException();
   }

   public abstract ImmutableCollection<V> get(K var1);

   public abstract ImmutableMultimap<V, K> inverse();

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public boolean put(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public boolean putAll(K var1, Iterable<? extends V> var2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public boolean putAll(Multimap<? extends K, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public boolean remove(Object var1, Object var2) {
      throw new UnsupportedOperationException();
   }

   boolean isPartialView() {
      return this.map.isPartialView();
   }

   public boolean containsKey(@Nullable Object var1) {
      return this.map.containsKey(var1);
   }

   public boolean containsValue(@Nullable Object var1) {
      return var1 != null && super.containsValue(var1);
   }

   public int size() {
      return this.size;
   }

   public ImmutableSet<K> keySet() {
      return this.map.keySet();
   }

   public ImmutableMap<K, Collection<V>> asMap() {
      return this.map;
   }

   Map<K, Collection<V>> createAsMap() {
      throw new AssertionError("should never be called");
   }

   public ImmutableCollection<Entry<K, V>> entries() {
      return (ImmutableCollection)super.entries();
   }

   ImmutableCollection<Entry<K, V>> createEntries() {
      return new ImmutableMultimap.EntryCollection(this);
   }

   UnmodifiableIterator<Entry<K, V>> entryIterator() {
      return new ImmutableMultimap<K, V>.Itr<Entry<K, V>>() {
         Entry<K, V> output(K var1, V var2) {
            return Maps.immutableEntry(var1, var2);
         }
      };
   }

   Spliterator<Entry<K, V>> entrySpliterator() {
      return CollectSpliterators.flatMap(this.asMap().entrySet().spliterator(), (var0) -> {
         Object var1 = var0.getKey();
         Collection var2 = (Collection)var0.getValue();
         return CollectSpliterators.map(var2.spliterator(), (var1x) -> {
            return Maps.immutableEntry(var1, var1x);
         });
      }, 64 | (this instanceof SetMultimap ? 1 : 0), (long)this.size());
   }

   public void forEach(BiConsumer<? super K, ? super V> var1) {
      Preconditions.checkNotNull(var1);
      this.asMap().forEach((var1x, var2) -> {
         var2.forEach((var2x) -> {
            var1.accept(var1x, var2x);
         });
      });
   }

   public ImmutableMultiset<K> keys() {
      return (ImmutableMultiset)super.keys();
   }

   ImmutableMultiset<K> createKeys() {
      return new ImmutableMultimap.Keys();
   }

   public ImmutableCollection<V> values() {
      return (ImmutableCollection)super.values();
   }

   ImmutableCollection<V> createValues() {
      return new ImmutableMultimap.Values(this);
   }

   UnmodifiableIterator<V> valueIterator() {
      return new ImmutableMultimap<K, V>.Itr<V>() {
         V output(K var1, V var2) {
            return var2;
         }
      };
   }

   private static final class Values<K, V> extends ImmutableCollection<V> {
      @Weak
      private final transient ImmutableMultimap<K, V> multimap;
      private static final long serialVersionUID = 0L;

      Values(ImmutableMultimap<K, V> var1) {
         super();
         this.multimap = var1;
      }

      public boolean contains(@Nullable Object var1) {
         return this.multimap.containsValue(var1);
      }

      public UnmodifiableIterator<V> iterator() {
         return this.multimap.valueIterator();
      }

      @GwtIncompatible
      int copyIntoArray(Object[] var1, int var2) {
         ImmutableCollection var4;
         for(UnmodifiableIterator var3 = this.multimap.map.values().iterator(); var3.hasNext(); var2 = var4.copyIntoArray(var1, var2)) {
            var4 = (ImmutableCollection)var3.next();
         }

         return var2;
      }

      public int size() {
         return this.multimap.size();
      }

      boolean isPartialView() {
         return true;
      }
   }

   class Keys extends ImmutableMultiset<K> {
      Keys() {
         super();
      }

      public boolean contains(@Nullable Object var1) {
         return ImmutableMultimap.this.containsKey(var1);
      }

      public int count(@Nullable Object var1) {
         Collection var2 = (Collection)ImmutableMultimap.this.map.get(var1);
         return var2 == null ? 0 : var2.size();
      }

      public ImmutableSet<K> elementSet() {
         return ImmutableMultimap.this.keySet();
      }

      public int size() {
         return ImmutableMultimap.this.size();
      }

      Multiset.Entry<K> getEntry(int var1) {
         Entry var2 = (Entry)ImmutableMultimap.this.map.entrySet().asList().get(var1);
         return Multisets.immutableEntry(var2.getKey(), ((Collection)var2.getValue()).size());
      }

      boolean isPartialView() {
         return true;
      }
   }

   private abstract class Itr<T> extends UnmodifiableIterator<T> {
      final Iterator<Entry<K, Collection<V>>> mapIterator;
      K key;
      Iterator<V> valueIterator;

      private Itr() {
         super();
         this.mapIterator = ImmutableMultimap.this.asMap().entrySet().iterator();
         this.key = null;
         this.valueIterator = Iterators.emptyIterator();
      }

      abstract T output(K var1, V var2);

      public boolean hasNext() {
         return this.mapIterator.hasNext() || this.valueIterator.hasNext();
      }

      public T next() {
         if (!this.valueIterator.hasNext()) {
            Entry var1 = (Entry)this.mapIterator.next();
            this.key = var1.getKey();
            this.valueIterator = ((Collection)var1.getValue()).iterator();
         }

         return this.output(this.key, this.valueIterator.next());
      }

      // $FF: synthetic method
      Itr(Object var2) {
         this();
      }
   }

   private static class EntryCollection<K, V> extends ImmutableCollection<Entry<K, V>> {
      @Weak
      final ImmutableMultimap<K, V> multimap;
      private static final long serialVersionUID = 0L;

      EntryCollection(ImmutableMultimap<K, V> var1) {
         super();
         this.multimap = var1;
      }

      public UnmodifiableIterator<Entry<K, V>> iterator() {
         return this.multimap.entryIterator();
      }

      boolean isPartialView() {
         return this.multimap.isPartialView();
      }

      public int size() {
         return this.multimap.size();
      }

      public boolean contains(Object var1) {
         if (var1 instanceof Entry) {
            Entry var2 = (Entry)var1;
            return this.multimap.containsEntry(var2.getKey(), var2.getValue());
         } else {
            return false;
         }
      }
   }

   @GwtIncompatible
   static class FieldSettersHolder {
      static final Serialization.FieldSetter<ImmutableMultimap> MAP_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "map");
      static final Serialization.FieldSetter<ImmutableMultimap> SIZE_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "size");
      static final Serialization.FieldSetter<ImmutableSetMultimap> EMPTY_SET_FIELD_SETTER = Serialization.getFieldSetter(ImmutableSetMultimap.class, "emptySet");

      FieldSettersHolder() {
         super();
      }
   }

   public static class Builder<K, V> {
      Multimap<K, V> builderMultimap;
      Comparator<? super K> keyComparator;
      Comparator<? super V> valueComparator;

      public Builder() {
         this(MultimapBuilder.linkedHashKeys().arrayListValues().build());
      }

      Builder(Multimap<K, V> var1) {
         super();
         this.builderMultimap = var1;
      }

      @CanIgnoreReturnValue
      public ImmutableMultimap.Builder<K, V> put(K var1, V var2) {
         CollectPreconditions.checkEntryNotNull(var1, var2);
         this.builderMultimap.put(var1, var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMultimap.Builder<K, V> put(Entry<? extends K, ? extends V> var1) {
         return this.put(var1.getKey(), var1.getValue());
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableMultimap.Builder<K, V> putAll(Iterable<? extends Entry<? extends K, ? extends V>> var1) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            this.put(var3);
         }

         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMultimap.Builder<K, V> putAll(K var1, Iterable<? extends V> var2) {
         if (var1 == null) {
            throw new NullPointerException("null key in entry: null=" + Iterables.toString(var2));
         } else {
            Collection var3 = this.builderMultimap.get(var1);
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               Object var5 = var4.next();
               CollectPreconditions.checkEntryNotNull(var1, var5);
               var3.add(var5);
            }

            return this;
         }
      }

      @CanIgnoreReturnValue
      public ImmutableMultimap.Builder<K, V> putAll(K var1, V... var2) {
         return this.putAll(var1, (Iterable)Arrays.asList(var2));
      }

      @CanIgnoreReturnValue
      public ImmutableMultimap.Builder<K, V> putAll(Multimap<? extends K, ? extends V> var1) {
         Iterator var2 = var1.asMap().entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            this.putAll(var3.getKey(), (Iterable)var3.getValue());
         }

         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMultimap.Builder<K, V> orderKeysBy(Comparator<? super K> var1) {
         this.keyComparator = (Comparator)Preconditions.checkNotNull(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMultimap.Builder<K, V> orderValuesBy(Comparator<? super V> var1) {
         this.valueComparator = (Comparator)Preconditions.checkNotNull(var1);
         return this;
      }

      @CanIgnoreReturnValue
      ImmutableMultimap.Builder<K, V> combine(ImmutableMultimap.Builder<K, V> var1) {
         this.putAll(var1.builderMultimap);
         return this;
      }

      public ImmutableMultimap<K, V> build() {
         if (this.valueComparator != null) {
            Iterator var1 = this.builderMultimap.asMap().values().iterator();

            while(var1.hasNext()) {
               Collection var2 = (Collection)var1.next();
               List var3 = (List)var2;
               Collections.sort(var3, this.valueComparator);
            }
         }

         if (this.keyComparator != null) {
            ListMultimap var5 = MultimapBuilder.linkedHashKeys().arrayListValues().build();
            ImmutableList var6 = Ordering.from(this.keyComparator).onKeys().immutableSortedCopy(this.builderMultimap.asMap().entrySet());
            Iterator var7 = var6.iterator();

            while(var7.hasNext()) {
               Entry var4 = (Entry)var7.next();
               var5.putAll(var4.getKey(), (Iterable)var4.getValue());
            }

            this.builderMultimap = var5;
         }

         return ImmutableMultimap.copyOf(this.builderMultimap);
      }
   }
}
