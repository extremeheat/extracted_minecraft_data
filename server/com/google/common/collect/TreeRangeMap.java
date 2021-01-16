package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public final class TreeRangeMap<K extends Comparable, V> implements RangeMap<K, V> {
   private final NavigableMap<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> entriesByLowerBound = Maps.newTreeMap();
   private static final RangeMap EMPTY_SUB_RANGE_MAP = new RangeMap() {
      @Nullable
      public Object get(Comparable var1) {
         return null;
      }

      @Nullable
      public Entry<Range, Object> getEntry(Comparable var1) {
         return null;
      }

      public Range span() {
         throw new NoSuchElementException();
      }

      public void put(Range var1, Object var2) {
         Preconditions.checkNotNull(var1);
         throw new IllegalArgumentException("Cannot insert range " + var1 + " into an empty subRangeMap");
      }

      public void putAll(RangeMap var1) {
         if (!var1.asMapOfRanges().isEmpty()) {
            throw new IllegalArgumentException("Cannot putAll(nonEmptyRangeMap) into an empty subRangeMap");
         }
      }

      public void clear() {
      }

      public void remove(Range var1) {
         Preconditions.checkNotNull(var1);
      }

      public Map<Range, Object> asMapOfRanges() {
         return Collections.emptyMap();
      }

      public Map<Range, Object> asDescendingMapOfRanges() {
         return Collections.emptyMap();
      }

      public RangeMap subRangeMap(Range var1) {
         Preconditions.checkNotNull(var1);
         return this;
      }
   };

   public static <K extends Comparable, V> TreeRangeMap<K, V> create() {
      return new TreeRangeMap();
   }

   private TreeRangeMap() {
      super();
   }

   @Nullable
   public V get(K var1) {
      Entry var2 = this.getEntry(var1);
      return var2 == null ? null : var2.getValue();
   }

   @Nullable
   public Entry<Range<K>, V> getEntry(K var1) {
      Entry var2 = this.entriesByLowerBound.floorEntry(Cut.belowValue(var1));
      return var2 != null && ((TreeRangeMap.RangeMapEntry)var2.getValue()).contains(var1) ? (Entry)var2.getValue() : null;
   }

   public void put(Range<K> var1, V var2) {
      if (!var1.isEmpty()) {
         Preconditions.checkNotNull(var2);
         this.remove(var1);
         this.entriesByLowerBound.put(var1.lowerBound, new TreeRangeMap.RangeMapEntry(var1, var2));
      }

   }

   public void putAll(RangeMap<K, V> var1) {
      Iterator var2 = var1.asMapOfRanges().entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.put((Range)var3.getKey(), var3.getValue());
      }

   }

   public void clear() {
      this.entriesByLowerBound.clear();
   }

   public Range<K> span() {
      Entry var1 = this.entriesByLowerBound.firstEntry();
      Entry var2 = this.entriesByLowerBound.lastEntry();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return Range.create(((TreeRangeMap.RangeMapEntry)var1.getValue()).getKey().lowerBound, ((TreeRangeMap.RangeMapEntry)var2.getValue()).getKey().upperBound);
      }
   }

   private void putRangeMapEntry(Cut<K> var1, Cut<K> var2, V var3) {
      this.entriesByLowerBound.put(var1, new TreeRangeMap.RangeMapEntry(var1, var2, var3));
   }

   public void remove(Range<K> var1) {
      if (!var1.isEmpty()) {
         Entry var2 = this.entriesByLowerBound.lowerEntry(var1.lowerBound);
         if (var2 != null) {
            TreeRangeMap.RangeMapEntry var3 = (TreeRangeMap.RangeMapEntry)var2.getValue();
            if (var3.getUpperBound().compareTo(var1.lowerBound) > 0) {
               if (var3.getUpperBound().compareTo(var1.upperBound) > 0) {
                  this.putRangeMapEntry(var1.upperBound, var3.getUpperBound(), ((TreeRangeMap.RangeMapEntry)var2.getValue()).getValue());
               }

               this.putRangeMapEntry(var3.getLowerBound(), var1.lowerBound, ((TreeRangeMap.RangeMapEntry)var2.getValue()).getValue());
            }
         }

         Entry var5 = this.entriesByLowerBound.lowerEntry(var1.upperBound);
         if (var5 != null) {
            TreeRangeMap.RangeMapEntry var4 = (TreeRangeMap.RangeMapEntry)var5.getValue();
            if (var4.getUpperBound().compareTo(var1.upperBound) > 0) {
               this.putRangeMapEntry(var1.upperBound, var4.getUpperBound(), ((TreeRangeMap.RangeMapEntry)var5.getValue()).getValue());
            }
         }

         this.entriesByLowerBound.subMap(var1.lowerBound, var1.upperBound).clear();
      }
   }

   public Map<Range<K>, V> asMapOfRanges() {
      return new TreeRangeMap.AsMapOfRanges(this.entriesByLowerBound.values());
   }

   public Map<Range<K>, V> asDescendingMapOfRanges() {
      return new TreeRangeMap.AsMapOfRanges(this.entriesByLowerBound.descendingMap().values());
   }

   public RangeMap<K, V> subRangeMap(Range<K> var1) {
      return (RangeMap)(var1.equals(Range.all()) ? this : new TreeRangeMap.SubRangeMap(var1));
   }

   private RangeMap<K, V> emptySubRangeMap() {
      return EMPTY_SUB_RANGE_MAP;
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 instanceof RangeMap) {
         RangeMap var2 = (RangeMap)var1;
         return this.asMapOfRanges().equals(var2.asMapOfRanges());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.asMapOfRanges().hashCode();
   }

   public String toString() {
      return this.entriesByLowerBound.values().toString();
   }

   private class SubRangeMap implements RangeMap<K, V> {
      private final Range<K> subRange;

      SubRangeMap(Range<K> var2) {
         super();
         this.subRange = var2;
      }

      @Nullable
      public V get(K var1) {
         return this.subRange.contains(var1) ? TreeRangeMap.this.get(var1) : null;
      }

      @Nullable
      public Entry<Range<K>, V> getEntry(K var1) {
         if (this.subRange.contains(var1)) {
            Entry var2 = TreeRangeMap.this.getEntry(var1);
            if (var2 != null) {
               return Maps.immutableEntry(((Range)var2.getKey()).intersection(this.subRange), var2.getValue());
            }
         }

         return null;
      }

      public Range<K> span() {
         Entry var2 = TreeRangeMap.this.entriesByLowerBound.floorEntry(this.subRange.lowerBound);
         Cut var1;
         if (var2 != null && ((TreeRangeMap.RangeMapEntry)var2.getValue()).getUpperBound().compareTo(this.subRange.lowerBound) > 0) {
            var1 = this.subRange.lowerBound;
         } else {
            var1 = (Cut)TreeRangeMap.this.entriesByLowerBound.ceilingKey(this.subRange.lowerBound);
            if (var1 == null || var1.compareTo(this.subRange.upperBound) >= 0) {
               throw new NoSuchElementException();
            }
         }

         Entry var4 = TreeRangeMap.this.entriesByLowerBound.lowerEntry(this.subRange.upperBound);
         if (var4 == null) {
            throw new NoSuchElementException();
         } else {
            Cut var3;
            if (((TreeRangeMap.RangeMapEntry)var4.getValue()).getUpperBound().compareTo(this.subRange.upperBound) >= 0) {
               var3 = this.subRange.upperBound;
            } else {
               var3 = ((TreeRangeMap.RangeMapEntry)var4.getValue()).getUpperBound();
            }

            return Range.create(var1, var3);
         }
      }

      public void put(Range<K> var1, V var2) {
         Preconditions.checkArgument(this.subRange.encloses(var1), "Cannot put range %s into a subRangeMap(%s)", var1, this.subRange);
         TreeRangeMap.this.put(var1, var2);
      }

      public void putAll(RangeMap<K, V> var1) {
         if (!var1.asMapOfRanges().isEmpty()) {
            Range var2 = var1.span();
            Preconditions.checkArgument(this.subRange.encloses(var2), "Cannot putAll rangeMap with span %s into a subRangeMap(%s)", var2, this.subRange);
            TreeRangeMap.this.putAll(var1);
         }
      }

      public void clear() {
         TreeRangeMap.this.remove(this.subRange);
      }

      public void remove(Range<K> var1) {
         if (var1.isConnected(this.subRange)) {
            TreeRangeMap.this.remove(var1.intersection(this.subRange));
         }

      }

      public RangeMap<K, V> subRangeMap(Range<K> var1) {
         return !var1.isConnected(this.subRange) ? TreeRangeMap.this.emptySubRangeMap() : TreeRangeMap.this.subRangeMap(var1.intersection(this.subRange));
      }

      public Map<Range<K>, V> asMapOfRanges() {
         return new TreeRangeMap.SubRangeMap.SubRangeMapAsMap();
      }

      public Map<Range<K>, V> asDescendingMapOfRanges() {
         return new TreeRangeMap<K, V>.SubRangeMap.SubRangeMapAsMap() {
            Iterator<Entry<Range<K>, V>> entryIterator() {
               if (SubRangeMap.this.subRange.isEmpty()) {
                  return Iterators.emptyIterator();
               } else {
                  final Iterator var1 = TreeRangeMap.this.entriesByLowerBound.headMap(SubRangeMap.this.subRange.upperBound, false).descendingMap().values().iterator();
                  return new AbstractIterator<Entry<Range<K>, V>>() {
                     protected Entry<Range<K>, V> computeNext() {
                        if (var1.hasNext()) {
                           TreeRangeMap.RangeMapEntry var1x = (TreeRangeMap.RangeMapEntry)var1.next();
                           return var1x.getUpperBound().compareTo(SubRangeMap.this.subRange.lowerBound) <= 0 ? (Entry)this.endOfData() : Maps.immutableEntry(var1x.getKey().intersection(SubRangeMap.this.subRange), var1x.getValue());
                        } else {
                           return (Entry)this.endOfData();
                        }
                     }
                  };
               }
            }
         };
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof RangeMap) {
            RangeMap var2 = (RangeMap)var1;
            return this.asMapOfRanges().equals(var2.asMapOfRanges());
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.asMapOfRanges().hashCode();
      }

      public String toString() {
         return this.asMapOfRanges().toString();
      }

      class SubRangeMapAsMap extends AbstractMap<Range<K>, V> {
         SubRangeMapAsMap() {
            super();
         }

         public boolean containsKey(Object var1) {
            return this.get(var1) != null;
         }

         public V get(Object var1) {
            try {
               if (var1 instanceof Range) {
                  Range var2 = (Range)var1;
                  if (!SubRangeMap.this.subRange.encloses(var2) || var2.isEmpty()) {
                     return null;
                  }

                  TreeRangeMap.RangeMapEntry var3 = null;
                  if (var2.lowerBound.compareTo(SubRangeMap.this.subRange.lowerBound) == 0) {
                     Entry var4 = TreeRangeMap.this.entriesByLowerBound.floorEntry(var2.lowerBound);
                     if (var4 != null) {
                        var3 = (TreeRangeMap.RangeMapEntry)var4.getValue();
                     }
                  } else {
                     var3 = (TreeRangeMap.RangeMapEntry)TreeRangeMap.this.entriesByLowerBound.get(var2.lowerBound);
                  }

                  if (var3 != null && var3.getKey().isConnected(SubRangeMap.this.subRange) && var3.getKey().intersection(SubRangeMap.this.subRange).equals(var2)) {
                     return var3.getValue();
                  }
               }

               return null;
            } catch (ClassCastException var5) {
               return null;
            }
         }

         public V remove(Object var1) {
            Object var2 = this.get(var1);
            if (var2 != null) {
               Range var3 = (Range)var1;
               TreeRangeMap.this.remove(var3);
               return var2;
            } else {
               return null;
            }
         }

         public void clear() {
            SubRangeMap.this.clear();
         }

         private boolean removeEntryIf(Predicate<? super Entry<Range<K>, V>> var1) {
            ArrayList var2 = Lists.newArrayList();
            Iterator var3 = this.entrySet().iterator();

            while(var3.hasNext()) {
               Entry var4 = (Entry)var3.next();
               if (var1.apply(var4)) {
                  var2.add(var4.getKey());
               }
            }

            var3 = var2.iterator();

            while(var3.hasNext()) {
               Range var5 = (Range)var3.next();
               TreeRangeMap.this.remove(var5);
            }

            return !var2.isEmpty();
         }

         public Set<Range<K>> keySet() {
            return new Maps.KeySet<Range<K>, V>(this) {
               public boolean remove(@Nullable Object var1) {
                  return SubRangeMapAsMap.this.remove(var1) != null;
               }

               public boolean retainAll(Collection<?> var1) {
                  return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.not(Predicates.in(var1)), Maps.keyFunction()));
               }
            };
         }

         public Set<Entry<Range<K>, V>> entrySet() {
            return new Maps.EntrySet<Range<K>, V>() {
               Map<Range<K>, V> map() {
                  return SubRangeMapAsMap.this;
               }

               public Iterator<Entry<Range<K>, V>> iterator() {
                  return SubRangeMapAsMap.this.entryIterator();
               }

               public boolean retainAll(Collection<?> var1) {
                  return SubRangeMapAsMap.this.removeEntryIf(Predicates.not(Predicates.in(var1)));
               }

               public int size() {
                  return Iterators.size(this.iterator());
               }

               public boolean isEmpty() {
                  return !this.iterator().hasNext();
               }
            };
         }

         Iterator<Entry<Range<K>, V>> entryIterator() {
            if (SubRangeMap.this.subRange.isEmpty()) {
               return Iterators.emptyIterator();
            } else {
               Cut var1 = (Cut)MoreObjects.firstNonNull(TreeRangeMap.this.entriesByLowerBound.floorKey(SubRangeMap.this.subRange.lowerBound), SubRangeMap.this.subRange.lowerBound);
               final Iterator var2 = TreeRangeMap.this.entriesByLowerBound.tailMap(var1, true).values().iterator();
               return new AbstractIterator<Entry<Range<K>, V>>() {
                  protected Entry<Range<K>, V> computeNext() {
                     while(true) {
                        if (var2.hasNext()) {
                           TreeRangeMap.RangeMapEntry var1 = (TreeRangeMap.RangeMapEntry)var2.next();
                           if (var1.getLowerBound().compareTo(SubRangeMap.this.subRange.upperBound) >= 0) {
                              return (Entry)this.endOfData();
                           }

                           if (var1.getUpperBound().compareTo(SubRangeMap.this.subRange.lowerBound) <= 0) {
                              continue;
                           }

                           return Maps.immutableEntry(var1.getKey().intersection(SubRangeMap.this.subRange), var1.getValue());
                        }

                        return (Entry)this.endOfData();
                     }
                  }
               };
            }
         }

         public Collection<V> values() {
            return new Maps.Values<Range<K>, V>(this) {
               public boolean removeAll(Collection<?> var1) {
                  return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.in(var1), Maps.valueFunction()));
               }

               public boolean retainAll(Collection<?> var1) {
                  return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.not(Predicates.in(var1)), Maps.valueFunction()));
               }
            };
         }
      }
   }

   private final class AsMapOfRanges extends Maps.IteratorBasedAbstractMap<Range<K>, V> {
      final Iterable<Entry<Range<K>, V>> entryIterable;

      AsMapOfRanges(Iterable<TreeRangeMap.RangeMapEntry<K, V>> var2) {
         super();
         this.entryIterable = var2;
      }

      public boolean containsKey(@Nullable Object var1) {
         return this.get(var1) != null;
      }

      public V get(@Nullable Object var1) {
         if (var1 instanceof Range) {
            Range var2 = (Range)var1;
            TreeRangeMap.RangeMapEntry var3 = (TreeRangeMap.RangeMapEntry)TreeRangeMap.this.entriesByLowerBound.get(var2.lowerBound);
            if (var3 != null && var3.getKey().equals(var2)) {
               return var3.getValue();
            }
         }

         return null;
      }

      public int size() {
         return TreeRangeMap.this.entriesByLowerBound.size();
      }

      Iterator<Entry<Range<K>, V>> entryIterator() {
         return this.entryIterable.iterator();
      }
   }

   private static final class RangeMapEntry<K extends Comparable, V> extends AbstractMapEntry<Range<K>, V> {
      private final Range<K> range;
      private final V value;

      RangeMapEntry(Cut<K> var1, Cut<K> var2, V var3) {
         this(Range.create(var1, var2), var3);
      }

      RangeMapEntry(Range<K> var1, V var2) {
         super();
         this.range = var1;
         this.value = var2;
      }

      public Range<K> getKey() {
         return this.range;
      }

      public V getValue() {
         return this.value;
      }

      public boolean contains(K var1) {
         return this.range.contains(var1);
      }

      Cut<K> getLowerBound() {
         return this.range.lowerBound;
      }

      Cut<K> getUpperBound() {
         return this.range.upperBound;
      }
   }
}
