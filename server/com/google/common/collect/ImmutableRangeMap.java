package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public class ImmutableRangeMap<K extends Comparable<?>, V> implements RangeMap<K, V>, Serializable {
   private static final ImmutableRangeMap<Comparable<?>, Object> EMPTY = new ImmutableRangeMap(ImmutableList.of(), ImmutableList.of());
   private final transient ImmutableList<Range<K>> ranges;
   private final transient ImmutableList<V> values;
   private static final long serialVersionUID = 0L;

   public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> of() {
      return EMPTY;
   }

   public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> of(Range<K> var0, V var1) {
      return new ImmutableRangeMap(ImmutableList.of(var0), ImmutableList.of(var1));
   }

   public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> copyOf(RangeMap<K, ? extends V> var0) {
      if (var0 instanceof ImmutableRangeMap) {
         return (ImmutableRangeMap)var0;
      } else {
         Map var1 = var0.asMapOfRanges();
         ImmutableList.Builder var2 = new ImmutableList.Builder(var1.size());
         ImmutableList.Builder var3 = new ImmutableList.Builder(var1.size());
         Iterator var4 = var1.entrySet().iterator();

         while(var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            var2.add(var5.getKey());
            var3.add(var5.getValue());
         }

         return new ImmutableRangeMap(var2.build(), var3.build());
      }
   }

   public static <K extends Comparable<?>, V> ImmutableRangeMap.Builder<K, V> builder() {
      return new ImmutableRangeMap.Builder();
   }

   ImmutableRangeMap(ImmutableList<Range<K>> var1, ImmutableList<V> var2) {
      super();
      this.ranges = var1;
      this.values = var2;
   }

   @Nullable
   public V get(K var1) {
      int var2 = SortedLists.binarySearch(this.ranges, (Function)Range.lowerBoundFn(), (Comparable)Cut.belowValue(var1), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
      if (var2 == -1) {
         return null;
      } else {
         Range var3 = (Range)this.ranges.get(var2);
         return var3.contains(var1) ? this.values.get(var2) : null;
      }
   }

   @Nullable
   public Entry<Range<K>, V> getEntry(K var1) {
      int var2 = SortedLists.binarySearch(this.ranges, (Function)Range.lowerBoundFn(), (Comparable)Cut.belowValue(var1), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
      if (var2 == -1) {
         return null;
      } else {
         Range var3 = (Range)this.ranges.get(var2);
         return var3.contains(var1) ? Maps.immutableEntry(var3, this.values.get(var2)) : null;
      }
   }

   public Range<K> span() {
      if (this.ranges.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         Range var1 = (Range)this.ranges.get(0);
         Range var2 = (Range)this.ranges.get(this.ranges.size() - 1);
         return Range.create(var1.lowerBound, var2.upperBound);
      }
   }

   /** @deprecated */
   @Deprecated
   public void put(Range<K> var1, V var2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public void putAll(RangeMap<K, V> var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public void clear() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public void remove(Range<K> var1) {
      throw new UnsupportedOperationException();
   }

   public ImmutableMap<Range<K>, V> asMapOfRanges() {
      if (this.ranges.isEmpty()) {
         return ImmutableMap.of();
      } else {
         RegularImmutableSortedSet var1 = new RegularImmutableSortedSet(this.ranges, Range.RANGE_LEX_ORDERING);
         return new ImmutableSortedMap(var1, this.values);
      }
   }

   public ImmutableMap<Range<K>, V> asDescendingMapOfRanges() {
      if (this.ranges.isEmpty()) {
         return ImmutableMap.of();
      } else {
         RegularImmutableSortedSet var1 = new RegularImmutableSortedSet(this.ranges.reverse(), Range.RANGE_LEX_ORDERING.reverse());
         return new ImmutableSortedMap(var1, this.values.reverse());
      }
   }

   public ImmutableRangeMap<K, V> subRangeMap(final Range<K> var1) {
      if (((Range)Preconditions.checkNotNull(var1)).isEmpty()) {
         return of();
      } else if (!this.ranges.isEmpty() && !var1.encloses(this.span())) {
         final int var2 = SortedLists.binarySearch(this.ranges, (Function)Range.upperBoundFn(), (Comparable)var1.lowerBound, SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
         int var3 = SortedLists.binarySearch(this.ranges, (Function)Range.lowerBoundFn(), (Comparable)var1.upperBound, SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
         if (var2 >= var3) {
            return of();
         } else {
            final int var5 = var3 - var2;
            ImmutableList var6 = new ImmutableList<Range<K>>() {
               public int size() {
                  return var5;
               }

               public Range<K> get(int var1x) {
                  Preconditions.checkElementIndex(var1x, var5);
                  return var1x != 0 && var1x != var5 - 1 ? (Range)ImmutableRangeMap.this.ranges.get(var1x + var2) : ((Range)ImmutableRangeMap.this.ranges.get(var1x + var2)).intersection(var1);
               }

               boolean isPartialView() {
                  return true;
               }
            };
            return new ImmutableRangeMap<K, V>(var6, this.values.subList(var2, var3)) {
               public ImmutableRangeMap<K, V> subRangeMap(Range<K> var1x) {
                  return var1.isConnected(var1x) ? ImmutableRangeMap.this.subRangeMap(var1x.intersection(var1)) : ImmutableRangeMap.of();
               }
            };
         }
      } else {
         return this;
      }
   }

   public int hashCode() {
      return this.asMapOfRanges().hashCode();
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 instanceof RangeMap) {
         RangeMap var2 = (RangeMap)var1;
         return this.asMapOfRanges().equals(var2.asMapOfRanges());
      } else {
         return false;
      }
   }

   public String toString() {
      return this.asMapOfRanges().toString();
   }

   Object writeReplace() {
      return new ImmutableRangeMap.SerializedForm(this.asMapOfRanges());
   }

   private static class SerializedForm<K extends Comparable<?>, V> implements Serializable {
      private final ImmutableMap<Range<K>, V> mapOfRanges;
      private static final long serialVersionUID = 0L;

      SerializedForm(ImmutableMap<Range<K>, V> var1) {
         super();
         this.mapOfRanges = var1;
      }

      Object readResolve() {
         return this.mapOfRanges.isEmpty() ? ImmutableRangeMap.of() : this.createRangeMap();
      }

      Object createRangeMap() {
         ImmutableRangeMap.Builder var1 = new ImmutableRangeMap.Builder();
         UnmodifiableIterator var2 = this.mapOfRanges.entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            var1.put((Range)var3.getKey(), var3.getValue());
         }

         return var1.build();
      }
   }

   public static final class Builder<K extends Comparable<?>, V> {
      private final List<Entry<Range<K>, V>> entries = Lists.newArrayList();

      public Builder() {
         super();
      }

      @CanIgnoreReturnValue
      public ImmutableRangeMap.Builder<K, V> put(Range<K> var1, V var2) {
         Preconditions.checkNotNull(var1);
         Preconditions.checkNotNull(var2);
         Preconditions.checkArgument(!var1.isEmpty(), "Range must not be empty, but was %s", (Object)var1);
         this.entries.add(Maps.immutableEntry(var1, var2));
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableRangeMap.Builder<K, V> putAll(RangeMap<K, ? extends V> var1) {
         Iterator var2 = var1.asMapOfRanges().entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            this.put((Range)var3.getKey(), var3.getValue());
         }

         return this;
      }

      public ImmutableRangeMap<K, V> build() {
         Collections.sort(this.entries, Range.RANGE_LEX_ORDERING.onKeys());
         ImmutableList.Builder var1 = new ImmutableList.Builder(this.entries.size());
         ImmutableList.Builder var2 = new ImmutableList.Builder(this.entries.size());

         for(int var3 = 0; var3 < this.entries.size(); ++var3) {
            Range var4 = (Range)((Entry)this.entries.get(var3)).getKey();
            if (var3 > 0) {
               Range var5 = (Range)((Entry)this.entries.get(var3 - 1)).getKey();
               if (var4.isConnected(var5) && !var4.intersection(var5).isEmpty()) {
                  throw new IllegalArgumentException("Overlapping ranges: range " + var5 + " overlaps with entry " + var4);
               }
            }

            var1.add((Object)var4);
            var2.add(((Entry)this.entries.get(var3)).getValue());
         }

         return new ImmutableRangeMap(var1.build(), var2.build());
      }
   }
}
