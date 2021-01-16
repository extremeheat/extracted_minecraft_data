package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
class FilteredEntryMultimap<K, V> extends AbstractMultimap<K, V> implements FilteredMultimap<K, V> {
   final Multimap<K, V> unfiltered;
   final Predicate<? super Entry<K, V>> predicate;

   FilteredEntryMultimap(Multimap<K, V> var1, Predicate<? super Entry<K, V>> var2) {
      super();
      this.unfiltered = (Multimap)Preconditions.checkNotNull(var1);
      this.predicate = (Predicate)Preconditions.checkNotNull(var2);
   }

   public Multimap<K, V> unfiltered() {
      return this.unfiltered;
   }

   public Predicate<? super Entry<K, V>> entryPredicate() {
      return this.predicate;
   }

   public int size() {
      return this.entries().size();
   }

   private boolean satisfies(K var1, V var2) {
      return this.predicate.apply(Maps.immutableEntry(var1, var2));
   }

   static <E> Collection<E> filterCollection(Collection<E> var0, Predicate<? super E> var1) {
      return (Collection)(var0 instanceof Set ? Sets.filter((Set)var0, var1) : Collections2.filter(var0, var1));
   }

   public boolean containsKey(@Nullable Object var1) {
      return this.asMap().get(var1) != null;
   }

   public Collection<V> removeAll(@Nullable Object var1) {
      return (Collection)MoreObjects.firstNonNull(this.asMap().remove(var1), this.unmodifiableEmptyCollection());
   }

   Collection<V> unmodifiableEmptyCollection() {
      return (Collection)(this.unfiltered instanceof SetMultimap ? Collections.emptySet() : Collections.emptyList());
   }

   public void clear() {
      this.entries().clear();
   }

   public Collection<V> get(K var1) {
      return filterCollection(this.unfiltered.get(var1), new FilteredEntryMultimap.ValuePredicate(var1));
   }

   Collection<Entry<K, V>> createEntries() {
      return filterCollection(this.unfiltered.entries(), this.predicate);
   }

   Collection<V> createValues() {
      return new FilteredMultimapValues(this);
   }

   Iterator<Entry<K, V>> entryIterator() {
      throw new AssertionError("should never be called");
   }

   Map<K, Collection<V>> createAsMap() {
      return new FilteredEntryMultimap.AsMap();
   }

   public Set<K> keySet() {
      return this.asMap().keySet();
   }

   boolean removeEntriesIf(Predicate<? super Entry<K, Collection<V>>> var1) {
      Iterator var2 = this.unfiltered.asMap().entrySet().iterator();
      boolean var3 = false;

      while(var2.hasNext()) {
         Entry var4 = (Entry)var2.next();
         Object var5 = var4.getKey();
         Collection var6 = filterCollection((Collection)var4.getValue(), new FilteredEntryMultimap.ValuePredicate(var5));
         if (!var6.isEmpty() && var1.apply(Maps.immutableEntry(var5, var6))) {
            if (var6.size() == ((Collection)var4.getValue()).size()) {
               var2.remove();
            } else {
               var6.clear();
            }

            var3 = true;
         }
      }

      return var3;
   }

   Multiset<K> createKeys() {
      return new FilteredEntryMultimap.Keys();
   }

   class Keys extends Multimaps.Keys<K, V> {
      Keys() {
         super(FilteredEntryMultimap.this);
      }

      public int remove(@Nullable Object var1, int var2) {
         CollectPreconditions.checkNonnegative(var2, "occurrences");
         if (var2 == 0) {
            return this.count(var1);
         } else {
            Collection var3 = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(var1);
            if (var3 == null) {
               return 0;
            } else {
               Object var4 = var1;
               int var5 = 0;
               Iterator var6 = var3.iterator();

               while(var6.hasNext()) {
                  Object var7 = var6.next();
                  if (FilteredEntryMultimap.this.satisfies(var4, var7)) {
                     ++var5;
                     if (var5 <= var2) {
                        var6.remove();
                     }
                  }
               }

               return var5;
            }
         }
      }

      public Set<Multiset.Entry<K>> entrySet() {
         return new Multisets.EntrySet<K>() {
            Multiset<K> multiset() {
               return Keys.this;
            }

            public Iterator<Multiset.Entry<K>> iterator() {
               return Keys.this.entryIterator();
            }

            public int size() {
               return FilteredEntryMultimap.this.keySet().size();
            }

            private boolean removeEntriesIf(final Predicate<? super Multiset.Entry<K>> var1) {
               return FilteredEntryMultimap.this.removeEntriesIf(new Predicate<Entry<K, Collection<V>>>() {
                  public boolean apply(Entry<K, Collection<V>> var1x) {
                     return var1.apply(Multisets.immutableEntry(var1x.getKey(), ((Collection)var1x.getValue()).size()));
                  }
               });
            }

            public boolean removeAll(Collection<?> var1) {
               return this.removeEntriesIf(Predicates.in(var1));
            }

            public boolean retainAll(Collection<?> var1) {
               return this.removeEntriesIf(Predicates.not(Predicates.in(var1)));
            }
         };
      }
   }

   class AsMap extends Maps.ViewCachingAbstractMap<K, Collection<V>> {
      AsMap() {
         super();
      }

      public boolean containsKey(@Nullable Object var1) {
         return this.get(var1) != null;
      }

      public void clear() {
         FilteredEntryMultimap.this.clear();
      }

      public Collection<V> get(@Nullable Object var1) {
         Collection var2 = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(var1);
         if (var2 == null) {
            return null;
         } else {
            var2 = FilteredEntryMultimap.filterCollection(var2, FilteredEntryMultimap.this.new ValuePredicate(var1));
            return var2.isEmpty() ? null : var2;
         }
      }

      public Collection<V> remove(@Nullable Object var1) {
         Collection var2 = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(var1);
         if (var2 == null) {
            return null;
         } else {
            Object var3 = var1;
            ArrayList var4 = Lists.newArrayList();
            Iterator var5 = var2.iterator();

            while(var5.hasNext()) {
               Object var6 = var5.next();
               if (FilteredEntryMultimap.this.satisfies(var3, var6)) {
                  var5.remove();
                  var4.add(var6);
               }
            }

            if (var4.isEmpty()) {
               return null;
            } else if (FilteredEntryMultimap.this.unfiltered instanceof SetMultimap) {
               return Collections.unmodifiableSet(Sets.newLinkedHashSet(var4));
            } else {
               return Collections.unmodifiableList(var4);
            }
         }
      }

      Set<K> createKeySet() {
         class 1KeySetImpl extends Maps.KeySet<K, Collection<V>> {
            _KeySetImpl/* $FF was: 1KeySetImpl*/() {
               super(AsMap.this);
            }

            public boolean removeAll(Collection<?> var1) {
               return FilteredEntryMultimap.this.removeEntriesIf(Maps.keyPredicateOnEntries(Predicates.in(var1)));
            }

            public boolean retainAll(Collection<?> var1) {
               return FilteredEntryMultimap.this.removeEntriesIf(Maps.keyPredicateOnEntries(Predicates.not(Predicates.in(var1))));
            }

            public boolean remove(@Nullable Object var1) {
               return AsMap.this.remove(var1) != null;
            }
         }

         return new 1KeySetImpl();
      }

      Set<Entry<K, Collection<V>>> createEntrySet() {
         class 1EntrySetImpl extends Maps.EntrySet<K, Collection<V>> {
            _EntrySetImpl/* $FF was: 1EntrySetImpl*/() {
               super();
            }

            Map<K, Collection<V>> map() {
               return AsMap.this;
            }

            public Iterator<Entry<K, Collection<V>>> iterator() {
               return new AbstractIterator<Entry<K, Collection<V>>>() {
                  final Iterator<Entry<K, Collection<V>>> backingIterator;

                  {
                     this.backingIterator = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();
                  }

                  protected Entry<K, Collection<V>> computeNext() {
                     while(true) {
                        if (this.backingIterator.hasNext()) {
                           Entry var1 = (Entry)this.backingIterator.next();
                           Object var2 = var1.getKey();
                           Collection var3 = FilteredEntryMultimap.filterCollection((Collection)var1.getValue(), FilteredEntryMultimap.this.new ValuePredicate(var2));
                           if (var3.isEmpty()) {
                              continue;
                           }

                           return Maps.immutableEntry(var2, var3);
                        }

                        return (Entry)this.endOfData();
                     }
                  }
               };
            }

            public boolean removeAll(Collection<?> var1) {
               return FilteredEntryMultimap.this.removeEntriesIf(Predicates.in(var1));
            }

            public boolean retainAll(Collection<?> var1) {
               return FilteredEntryMultimap.this.removeEntriesIf(Predicates.not(Predicates.in(var1)));
            }

            public int size() {
               return Iterators.size(this.iterator());
            }
         }

         return new 1EntrySetImpl();
      }

      Collection<Collection<V>> createValues() {
         class 1ValuesImpl extends Maps.Values<K, Collection<V>> {
            _ValuesImpl/* $FF was: 1ValuesImpl*/() {
               super(AsMap.this);
            }

            public boolean remove(@Nullable Object var1) {
               if (var1 instanceof Collection) {
                  Collection var2 = (Collection)var1;
                  Iterator var3 = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();

                  while(var3.hasNext()) {
                     Entry var4 = (Entry)var3.next();
                     Object var5 = var4.getKey();
                     Collection var6 = FilteredEntryMultimap.filterCollection((Collection)var4.getValue(), FilteredEntryMultimap.this.new ValuePredicate(var5));
                     if (!var6.isEmpty() && var2.equals(var6)) {
                        if (var6.size() == ((Collection)var4.getValue()).size()) {
                           var3.remove();
                        } else {
                           var6.clear();
                        }

                        return true;
                     }
                  }
               }

               return false;
            }

            public boolean removeAll(Collection<?> var1) {
               return FilteredEntryMultimap.this.removeEntriesIf(Maps.valuePredicateOnEntries(Predicates.in(var1)));
            }

            public boolean retainAll(Collection<?> var1) {
               return FilteredEntryMultimap.this.removeEntriesIf(Maps.valuePredicateOnEntries(Predicates.not(Predicates.in(var1))));
            }
         }

         return new 1ValuesImpl();
      }
   }

   final class ValuePredicate implements Predicate<V> {
      private final K key;

      ValuePredicate(K var2) {
         super();
         this.key = var2;
      }

      public boolean apply(@Nullable V var1) {
         return FilteredEntryMultimap.this.satisfies(this.key, var1);
      }
   }
}
