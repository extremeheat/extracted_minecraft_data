package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableBiMap<K, V> extends ImmutableBiMapFauxverideShim<K, V> implements BiMap<K, V> {
   @Beta
   public static <T, K, V> Collector<T, ?, ImmutableBiMap<K, V>> toImmutableBiMap(Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1) {
      return CollectCollectors.toImmutableBiMap(var0, var1);
   }

   public static <K, V> ImmutableBiMap<K, V> of() {
      return RegularImmutableBiMap.EMPTY;
   }

   public static <K, V> ImmutableBiMap<K, V> of(K var0, V var1) {
      return new SingletonImmutableBiMap(var0, var1);
   }

   public static <K, V> ImmutableBiMap<K, V> of(K var0, V var1, K var2, V var3) {
      return RegularImmutableBiMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3));
   }

   public static <K, V> ImmutableBiMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5) {
      return RegularImmutableBiMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5));
   }

   public static <K, V> ImmutableBiMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7) {
      return RegularImmutableBiMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7));
   }

   public static <K, V> ImmutableBiMap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9) {
      return RegularImmutableBiMap.fromEntries(entryOf(var0, var1), entryOf(var2, var3), entryOf(var4, var5), entryOf(var6, var7), entryOf(var8, var9));
   }

   public static <K, V> ImmutableBiMap.Builder<K, V> builder() {
      return new ImmutableBiMap.Builder();
   }

   public static <K, V> ImmutableBiMap<K, V> copyOf(Map<? extends K, ? extends V> var0) {
      if (var0 instanceof ImmutableBiMap) {
         ImmutableBiMap var1 = (ImmutableBiMap)var0;
         if (!var1.isPartialView()) {
            return var1;
         }
      }

      return copyOf((Iterable)var0.entrySet());
   }

   @Beta
   public static <K, V> ImmutableBiMap<K, V> copyOf(Iterable<? extends Entry<? extends K, ? extends V>> var0) {
      Entry[] var1 = (Entry[])((Entry[])Iterables.toArray(var0, (Object[])EMPTY_ENTRY_ARRAY));
      switch(var1.length) {
      case 0:
         return of();
      case 1:
         Entry var2 = var1[0];
         return of(var2.getKey(), var2.getValue());
      default:
         return RegularImmutableBiMap.fromEntries(var1);
      }
   }

   ImmutableBiMap() {
      super();
   }

   public abstract ImmutableBiMap<V, K> inverse();

   public ImmutableSet<V> values() {
      return this.inverse().keySet();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public V forcePut(K var1, V var2) {
      throw new UnsupportedOperationException();
   }

   Object writeReplace() {
      return new ImmutableBiMap.SerializedForm(this);
   }

   private static class SerializedForm extends ImmutableMap.SerializedForm {
      private static final long serialVersionUID = 0L;

      SerializedForm(ImmutableBiMap<?, ?> var1) {
         super(var1);
      }

      Object readResolve() {
         ImmutableBiMap.Builder var1 = new ImmutableBiMap.Builder();
         return this.createMap(var1);
      }
   }

   public static final class Builder<K, V> extends ImmutableMap.Builder<K, V> {
      public Builder() {
         super();
      }

      Builder(int var1) {
         super(var1);
      }

      @CanIgnoreReturnValue
      public ImmutableBiMap.Builder<K, V> put(K var1, V var2) {
         super.put(var1, var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableBiMap.Builder<K, V> put(Entry<? extends K, ? extends V> var1) {
         super.put(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableBiMap.Builder<K, V> putAll(Map<? extends K, ? extends V> var1) {
         super.putAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableBiMap.Builder<K, V> putAll(Iterable<? extends Entry<? extends K, ? extends V>> var1) {
         super.putAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableBiMap.Builder<K, V> orderEntriesByValue(Comparator<? super V> var1) {
         super.orderEntriesByValue(var1);
         return this;
      }

      @CanIgnoreReturnValue
      ImmutableBiMap.Builder<K, V> combine(ImmutableMap.Builder<K, V> var1) {
         super.combine(var1);
         return this;
      }

      public ImmutableBiMap<K, V> build() {
         switch(this.size) {
         case 0:
            return ImmutableBiMap.of();
         case 1:
            return ImmutableBiMap.of(this.entries[0].getKey(), this.entries[0].getValue());
         default:
            if (this.valueComparator != null) {
               if (this.entriesUsed) {
                  this.entries = (ImmutableMapEntry[])Arrays.copyOf(this.entries, this.size);
               }

               Arrays.sort(this.entries, 0, this.size, Ordering.from(this.valueComparator).onResultOf(Maps.valueFunction()));
            }

            this.entriesUsed = this.size == this.entries.length;
            return RegularImmutableBiMap.fromEntryArray(this.size, this.entries);
         }
      }
   }
}
