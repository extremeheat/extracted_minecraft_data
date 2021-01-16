package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import com.google.j2objc.annotations.Weak;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public class ImmutableSetMultimap<K, V> extends ImmutableMultimap<K, V> implements SetMultimap<K, V> {
   private final transient ImmutableSet<V> emptySet;
   @LazyInit
   @RetainedWith
   private transient ImmutableSetMultimap<V, K> inverse;
   private transient ImmutableSet<Entry<K, V>> entries;
   @GwtIncompatible
   private static final long serialVersionUID = 0L;

   @Beta
   public static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> toImmutableSetMultimap(Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1) {
      Preconditions.checkNotNull(var0, "keyFunction");
      Preconditions.checkNotNull(var1, "valueFunction");
      return Collector.of(ImmutableSetMultimap::builder, (var2, var3) -> {
         var2.put(var0.apply(var3), var1.apply(var3));
      }, ImmutableSetMultimap.Builder::combine, ImmutableSetMultimap.Builder::build);
   }

   @Beta
   public static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> flatteningToImmutableSetMultimap(Function<? super T, ? extends K> var0, Function<? super T, ? extends Stream<? extends V>> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Function var10000 = (var1x) -> {
         return Preconditions.checkNotNull(var0.apply(var1x));
      };
      Function var10001 = (var1x) -> {
         return ((Stream)var1.apply(var1x)).peek(Preconditions::checkNotNull);
      };
      MultimapBuilder.SetMultimapBuilder var10002 = MultimapBuilder.linkedHashKeys().linkedHashSetValues();
      var10002.getClass();
      return Collectors.collectingAndThen(Multimaps.flatteningToMultimap(var10000, var10001, var10002::build), ImmutableSetMultimap::copyOf);
   }

   public static <K, V> ImmutableSetMultimap<K, V> of() {
      return EmptyImmutableSetMultimap.INSTANCE;
   }

   public static <K, V> ImmutableSetMultimap<K, V> of(K var0, V var1) {
      ImmutableSetMultimap.Builder var2 = builder();
      var2.put(var0, var1);
      return var2.build();
   }

   public static <K, V> ImmutableSetMultimap<K, V> of(K var0, V var1, K var2, V var3) {
      ImmutableSetMultimap.Builder var4 = builder();
      var4.put(var0, var1);
      var4.put(var2, var3);
      return var4.build();
   }

   public static <K, V> ImmutableSetMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5) {
      ImmutableSetMultimap.Builder var6 = builder();
      var6.put(var0, var1);
      var6.put(var2, var3);
      var6.put(var4, var5);
      return var6.build();
   }

   public static <K, V> ImmutableSetMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7) {
      ImmutableSetMultimap.Builder var8 = builder();
      var8.put(var0, var1);
      var8.put(var2, var3);
      var8.put(var4, var5);
      var8.put(var6, var7);
      return var8.build();
   }

   public static <K, V> ImmutableSetMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9) {
      ImmutableSetMultimap.Builder var10 = builder();
      var10.put(var0, var1);
      var10.put(var2, var3);
      var10.put(var4, var5);
      var10.put(var6, var7);
      var10.put(var8, var9);
      return var10.build();
   }

   public static <K, V> ImmutableSetMultimap.Builder<K, V> builder() {
      return new ImmutableSetMultimap.Builder();
   }

   public static <K, V> ImmutableSetMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> var0) {
      return copyOf(var0, (Comparator)null);
   }

   private static <K, V> ImmutableSetMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> var0, Comparator<? super V> var1) {
      Preconditions.checkNotNull(var0);
      if (var0.isEmpty() && var1 == null) {
         return of();
      } else {
         if (var0 instanceof ImmutableSetMultimap) {
            ImmutableSetMultimap var2 = (ImmutableSetMultimap)var0;
            if (!var2.isPartialView()) {
               return var2;
            }
         }

         ImmutableMap.Builder var9 = new ImmutableMap.Builder(var0.asMap().size());
         int var3 = 0;
         Iterator var4 = var0.asMap().entrySet().iterator();

         while(var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            Object var6 = var5.getKey();
            Collection var7 = (Collection)var5.getValue();
            ImmutableSet var8 = valueSet(var1, var7);
            if (!var8.isEmpty()) {
               var9.put(var6, var8);
               var3 += var8.size();
            }
         }

         return new ImmutableSetMultimap(var9.build(), var3, var1);
      }
   }

   @Beta
   public static <K, V> ImmutableSetMultimap<K, V> copyOf(Iterable<? extends Entry<? extends K, ? extends V>> var0) {
      return (new ImmutableSetMultimap.Builder()).putAll(var0).build();
   }

   ImmutableSetMultimap(ImmutableMap<K, ImmutableSet<V>> var1, int var2, @Nullable Comparator<? super V> var3) {
      super(var1, var2);
      this.emptySet = emptySet(var3);
   }

   public ImmutableSet<V> get(@Nullable K var1) {
      ImmutableSet var2 = (ImmutableSet)this.map.get(var1);
      return (ImmutableSet)MoreObjects.firstNonNull(var2, this.emptySet);
   }

   public ImmutableSetMultimap<V, K> inverse() {
      ImmutableSetMultimap var1 = this.inverse;
      return var1 == null ? (this.inverse = this.invert()) : var1;
   }

   private ImmutableSetMultimap<V, K> invert() {
      ImmutableSetMultimap.Builder var1 = builder();
      UnmodifiableIterator var2 = this.entries().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.put(var3.getValue(), var3.getKey());
      }

      ImmutableSetMultimap var4 = var1.build();
      var4.inverse = this;
      return var4;
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public ImmutableSet<V> removeAll(Object var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public ImmutableSet<V> replaceValues(K var1, Iterable<? extends V> var2) {
      throw new UnsupportedOperationException();
   }

   public ImmutableSet<Entry<K, V>> entries() {
      ImmutableSet var1 = this.entries;
      return var1 == null ? (this.entries = new ImmutableSetMultimap.EntrySet(this)) : var1;
   }

   private static <V> ImmutableSet<V> valueSet(@Nullable Comparator<? super V> var0, Collection<? extends V> var1) {
      return (ImmutableSet)(var0 == null ? ImmutableSet.copyOf(var1) : ImmutableSortedSet.copyOf(var0, var1));
   }

   private static <V> ImmutableSet<V> emptySet(@Nullable Comparator<? super V> var0) {
      return (ImmutableSet)(var0 == null ? ImmutableSet.of() : ImmutableSortedSet.emptySet(var0));
   }

   private static <V> ImmutableSet.Builder<V> valuesBuilder(@Nullable Comparator<? super V> var0) {
      return (ImmutableSet.Builder)(var0 == null ? new ImmutableSet.Builder() : new ImmutableSortedSet.Builder(var0));
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.valueComparator());
      Serialization.writeMultimap(this, var1);
   }

   @Nullable
   Comparator<? super V> valueComparator() {
      return this.emptySet instanceof ImmutableSortedSet ? ((ImmutableSortedSet)this.emptySet).comparator() : null;
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Comparator var2 = (Comparator)var1.readObject();
      int var3 = var1.readInt();
      if (var3 < 0) {
         throw new InvalidObjectException("Invalid key count " + var3);
      } else {
         ImmutableMap.Builder var4 = ImmutableMap.builder();
         int var5 = 0;

         for(int var6 = 0; var6 < var3; ++var6) {
            Object var7 = var1.readObject();
            int var8 = var1.readInt();
            if (var8 <= 0) {
               throw new InvalidObjectException("Invalid value count " + var8);
            }

            ImmutableSet.Builder var9 = valuesBuilder(var2);

            for(int var10 = 0; var10 < var8; ++var10) {
               var9.add(var1.readObject());
            }

            ImmutableSet var12 = var9.build();
            if (var12.size() != var8) {
               throw new InvalidObjectException("Duplicate key-value pairs exist for key " + var7);
            }

            var4.put(var7, var12);
            var5 += var8;
         }

         ImmutableMap var13;
         try {
            var13 = var4.build();
         } catch (IllegalArgumentException var11) {
            throw (InvalidObjectException)(new InvalidObjectException(var11.getMessage())).initCause(var11);
         }

         ImmutableMultimap.FieldSettersHolder.MAP_FIELD_SETTER.set(this, var13);
         ImmutableMultimap.FieldSettersHolder.SIZE_FIELD_SETTER.set(this, var5);
         ImmutableMultimap.FieldSettersHolder.EMPTY_SET_FIELD_SETTER.set(this, emptySet(var2));
      }
   }

   private static final class EntrySet<K, V> extends ImmutableSet<Entry<K, V>> {
      @Weak
      private final transient ImmutableSetMultimap<K, V> multimap;

      EntrySet(ImmutableSetMultimap<K, V> var1) {
         super();
         this.multimap = var1;
      }

      public boolean contains(@Nullable Object var1) {
         if (var1 instanceof Entry) {
            Entry var2 = (Entry)var1;
            return this.multimap.containsEntry(var2.getKey(), var2.getValue());
         } else {
            return false;
         }
      }

      public int size() {
         return this.multimap.size();
      }

      public UnmodifiableIterator<Entry<K, V>> iterator() {
         return this.multimap.entryIterator();
      }

      boolean isPartialView() {
         return false;
      }
   }

   public static final class Builder<K, V> extends ImmutableMultimap.Builder<K, V> {
      public Builder() {
         super(MultimapBuilder.linkedHashKeys().linkedHashSetValues().build());
      }

      @CanIgnoreReturnValue
      public ImmutableSetMultimap.Builder<K, V> put(K var1, V var2) {
         this.builderMultimap.put(Preconditions.checkNotNull(var1), Preconditions.checkNotNull(var2));
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSetMultimap.Builder<K, V> put(Entry<? extends K, ? extends V> var1) {
         this.builderMultimap.put(Preconditions.checkNotNull(var1.getKey()), Preconditions.checkNotNull(var1.getValue()));
         return this;
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableSetMultimap.Builder<K, V> putAll(Iterable<? extends Entry<? extends K, ? extends V>> var1) {
         super.putAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSetMultimap.Builder<K, V> putAll(K var1, Iterable<? extends V> var2) {
         Collection var3 = this.builderMultimap.get(Preconditions.checkNotNull(var1));
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            Object var5 = var4.next();
            var3.add(Preconditions.checkNotNull(var5));
         }

         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSetMultimap.Builder<K, V> putAll(K var1, V... var2) {
         return this.putAll(var1, (Iterable)Arrays.asList(var2));
      }

      @CanIgnoreReturnValue
      public ImmutableSetMultimap.Builder<K, V> putAll(Multimap<? extends K, ? extends V> var1) {
         Iterator var2 = var1.asMap().entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            this.putAll(var3.getKey(), (Iterable)var3.getValue());
         }

         return this;
      }

      @CanIgnoreReturnValue
      ImmutableSetMultimap.Builder<K, V> combine(ImmutableMultimap.Builder<K, V> var1) {
         super.combine(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSetMultimap.Builder<K, V> orderKeysBy(Comparator<? super K> var1) {
         this.keyComparator = (Comparator)Preconditions.checkNotNull(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableSetMultimap.Builder<K, V> orderValuesBy(Comparator<? super V> var1) {
         super.orderValuesBy(var1);
         return this;
      }

      public ImmutableSetMultimap<K, V> build() {
         if (this.keyComparator != null) {
            SetMultimap var1 = MultimapBuilder.linkedHashKeys().linkedHashSetValues().build();
            ImmutableList var2 = Ordering.from(this.keyComparator).onKeys().immutableSortedCopy(this.builderMultimap.asMap().entrySet());
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               Entry var4 = (Entry)var3.next();
               var1.putAll(var4.getKey(), (Iterable)var4.getValue());
            }

            this.builderMultimap = var1;
         }

         return ImmutableSetMultimap.copyOf(this.builderMultimap, this.valueComparator);
      }
   }
}
