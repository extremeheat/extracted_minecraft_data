package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
class FilteredKeyMultimap<K, V> extends AbstractMultimap<K, V> implements FilteredMultimap<K, V> {
   final Multimap<K, V> unfiltered;
   final Predicate<? super K> keyPredicate;

   FilteredKeyMultimap(Multimap<K, V> var1, Predicate<? super K> var2) {
      super();
      this.unfiltered = (Multimap)Preconditions.checkNotNull(var1);
      this.keyPredicate = (Predicate)Preconditions.checkNotNull(var2);
   }

   public Multimap<K, V> unfiltered() {
      return this.unfiltered;
   }

   public Predicate<? super Entry<K, V>> entryPredicate() {
      return Maps.keyPredicateOnEntries(this.keyPredicate);
   }

   public int size() {
      int var1 = 0;

      Collection var3;
      for(Iterator var2 = this.asMap().values().iterator(); var2.hasNext(); var1 += var3.size()) {
         var3 = (Collection)var2.next();
      }

      return var1;
   }

   public boolean containsKey(@Nullable Object var1) {
      return this.unfiltered.containsKey(var1) ? this.keyPredicate.apply(var1) : false;
   }

   public Collection<V> removeAll(Object var1) {
      return this.containsKey(var1) ? this.unfiltered.removeAll(var1) : this.unmodifiableEmptyCollection();
   }

   Collection<V> unmodifiableEmptyCollection() {
      return (Collection)(this.unfiltered instanceof SetMultimap ? ImmutableSet.of() : ImmutableList.of());
   }

   public void clear() {
      this.keySet().clear();
   }

   Set<K> createKeySet() {
      return Sets.filter(this.unfiltered.keySet(), this.keyPredicate);
   }

   public Collection<V> get(K var1) {
      if (this.keyPredicate.apply(var1)) {
         return this.unfiltered.get(var1);
      } else {
         return (Collection)(this.unfiltered instanceof SetMultimap ? new FilteredKeyMultimap.AddRejectingSet(var1) : new FilteredKeyMultimap.AddRejectingList(var1));
      }
   }

   Iterator<Entry<K, V>> entryIterator() {
      throw new AssertionError("should never be called");
   }

   Collection<Entry<K, V>> createEntries() {
      return new FilteredKeyMultimap.Entries();
   }

   Collection<V> createValues() {
      return new FilteredMultimapValues(this);
   }

   Map<K, Collection<V>> createAsMap() {
      return Maps.filterKeys(this.unfiltered.asMap(), this.keyPredicate);
   }

   Multiset<K> createKeys() {
      return Multisets.filter(this.unfiltered.keys(), this.keyPredicate);
   }

   class Entries extends ForwardingCollection<Entry<K, V>> {
      Entries() {
         super();
      }

      protected Collection<Entry<K, V>> delegate() {
         return Collections2.filter(FilteredKeyMultimap.this.unfiltered.entries(), FilteredKeyMultimap.this.entryPredicate());
      }

      public boolean remove(@Nullable Object var1) {
         if (var1 instanceof Entry) {
            Entry var2 = (Entry)var1;
            if (FilteredKeyMultimap.this.unfiltered.containsKey(var2.getKey()) && FilteredKeyMultimap.this.keyPredicate.apply(var2.getKey())) {
               return FilteredKeyMultimap.this.unfiltered.remove(var2.getKey(), var2.getValue());
            }
         }

         return false;
      }
   }

   static class AddRejectingList<K, V> extends ForwardingList<V> {
      final K key;

      AddRejectingList(K var1) {
         super();
         this.key = var1;
      }

      public boolean add(V var1) {
         this.add(0, var1);
         return true;
      }

      public boolean addAll(Collection<? extends V> var1) {
         this.addAll(0, var1);
         return true;
      }

      public void add(int var1, V var2) {
         Preconditions.checkPositionIndex(var1, 0);
         throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
      }

      @CanIgnoreReturnValue
      public boolean addAll(int var1, Collection<? extends V> var2) {
         Preconditions.checkNotNull(var2);
         Preconditions.checkPositionIndex(var1, 0);
         throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
      }

      protected List<V> delegate() {
         return Collections.emptyList();
      }
   }

   static class AddRejectingSet<K, V> extends ForwardingSet<V> {
      final K key;

      AddRejectingSet(K var1) {
         super();
         this.key = var1;
      }

      public boolean add(V var1) {
         throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
      }

      public boolean addAll(Collection<? extends V> var1) {
         Preconditions.checkNotNull(var1);
         throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
      }

      protected Set<V> delegate() {
         return Collections.emptySet();
      }
   }
}
