package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
class StandardTable<R, C, V> extends AbstractTable<R, C, V> implements Serializable {
   @GwtTransient
   final Map<R, Map<C, V>> backingMap;
   @GwtTransient
   final Supplier<? extends Map<C, V>> factory;
   private transient Set<C> columnKeySet;
   private transient Map<R, Map<C, V>> rowMap;
   private transient StandardTable<R, C, V>.ColumnMap columnMap;
   private static final long serialVersionUID = 0L;

   StandardTable(Map<R, Map<C, V>> var1, Supplier<? extends Map<C, V>> var2) {
      super();
      this.backingMap = var1;
      this.factory = var2;
   }

   public boolean contains(@Nullable Object var1, @Nullable Object var2) {
      return var1 != null && var2 != null && super.contains(var1, var2);
   }

   public boolean containsColumn(@Nullable Object var1) {
      if (var1 == null) {
         return false;
      } else {
         Iterator var2 = this.backingMap.values().iterator();

         Map var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = (Map)var2.next();
         } while(!Maps.safeContainsKey(var3, var1));

         return true;
      }
   }

   public boolean containsRow(@Nullable Object var1) {
      return var1 != null && Maps.safeContainsKey(this.backingMap, var1);
   }

   public boolean containsValue(@Nullable Object var1) {
      return var1 != null && super.containsValue(var1);
   }

   public V get(@Nullable Object var1, @Nullable Object var2) {
      return var1 != null && var2 != null ? super.get(var1, var2) : null;
   }

   public boolean isEmpty() {
      return this.backingMap.isEmpty();
   }

   public int size() {
      int var1 = 0;

      Map var3;
      for(Iterator var2 = this.backingMap.values().iterator(); var2.hasNext(); var1 += var3.size()) {
         var3 = (Map)var2.next();
      }

      return var1;
   }

   public void clear() {
      this.backingMap.clear();
   }

   private Map<C, V> getOrCreate(R var1) {
      Map var2 = (Map)this.backingMap.get(var1);
      if (var2 == null) {
         var2 = (Map)this.factory.get();
         this.backingMap.put(var1, var2);
      }

      return var2;
   }

   @CanIgnoreReturnValue
   public V put(R var1, C var2, V var3) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var3);
      return this.getOrCreate(var1).put(var2, var3);
   }

   @CanIgnoreReturnValue
   public V remove(@Nullable Object var1, @Nullable Object var2) {
      if (var1 != null && var2 != null) {
         Map var3 = (Map)Maps.safeGet(this.backingMap, var1);
         if (var3 == null) {
            return null;
         } else {
            Object var4 = var3.remove(var2);
            if (var3.isEmpty()) {
               this.backingMap.remove(var1);
            }

            return var4;
         }
      } else {
         return null;
      }
   }

   @CanIgnoreReturnValue
   private Map<R, V> removeColumn(Object var1) {
      LinkedHashMap var2 = new LinkedHashMap();
      Iterator var3 = this.backingMap.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         Object var5 = ((Map)var4.getValue()).remove(var1);
         if (var5 != null) {
            var2.put(var4.getKey(), var5);
            if (((Map)var4.getValue()).isEmpty()) {
               var3.remove();
            }
         }
      }

      return var2;
   }

   private boolean containsMapping(Object var1, Object var2, Object var3) {
      return var3 != null && var3.equals(this.get(var1, var2));
   }

   private boolean removeMapping(Object var1, Object var2, Object var3) {
      if (this.containsMapping(var1, var2, var3)) {
         this.remove(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   public Set<Table.Cell<R, C, V>> cellSet() {
      return super.cellSet();
   }

   Iterator<Table.Cell<R, C, V>> cellIterator() {
      return new StandardTable.CellIterator();
   }

   Spliterator<Table.Cell<R, C, V>> cellSpliterator() {
      return CollectSpliterators.flatMap(this.backingMap.entrySet().spliterator(), (var0) -> {
         return CollectSpliterators.map(((Map)var0.getValue()).entrySet().spliterator(), (var1) -> {
            return Tables.immutableCell(var0.getKey(), var1.getKey(), var1.getValue());
         });
      }, 65, (long)this.size());
   }

   public Map<C, V> row(R var1) {
      return new StandardTable.Row(var1);
   }

   public Map<R, V> column(C var1) {
      return new StandardTable.Column(var1);
   }

   public Set<R> rowKeySet() {
      return this.rowMap().keySet();
   }

   public Set<C> columnKeySet() {
      Set var1 = this.columnKeySet;
      return var1 == null ? (this.columnKeySet = new StandardTable.ColumnKeySet()) : var1;
   }

   Iterator<C> createColumnKeyIterator() {
      return new StandardTable.ColumnKeyIterator();
   }

   public Collection<V> values() {
      return super.values();
   }

   public Map<R, Map<C, V>> rowMap() {
      Map var1 = this.rowMap;
      return var1 == null ? (this.rowMap = this.createRowMap()) : var1;
   }

   Map<R, Map<C, V>> createRowMap() {
      return new StandardTable.RowMap();
   }

   public Map<C, Map<R, V>> columnMap() {
      StandardTable.ColumnMap var1 = this.columnMap;
      return var1 == null ? (this.columnMap = new StandardTable.ColumnMap()) : var1;
   }

   private class ColumnMap extends Maps.ViewCachingAbstractMap<C, Map<R, V>> {
      private ColumnMap() {
         super();
      }

      public Map<R, V> get(Object var1) {
         return StandardTable.this.containsColumn(var1) ? StandardTable.this.column(var1) : null;
      }

      public boolean containsKey(Object var1) {
         return StandardTable.this.containsColumn(var1);
      }

      public Map<R, V> remove(Object var1) {
         return StandardTable.this.containsColumn(var1) ? StandardTable.this.removeColumn(var1) : null;
      }

      public Set<Entry<C, Map<R, V>>> createEntrySet() {
         return new StandardTable.ColumnMap.ColumnMapEntrySet();
      }

      public Set<C> keySet() {
         return StandardTable.this.columnKeySet();
      }

      Collection<Map<R, V>> createValues() {
         return new StandardTable.ColumnMap.ColumnMapValues();
      }

      // $FF: synthetic method
      ColumnMap(Object var2) {
         this();
      }

      private class ColumnMapValues extends Maps.Values<C, Map<R, V>> {
         ColumnMapValues() {
            super(ColumnMap.this);
         }

         public boolean remove(Object var1) {
            Iterator var2 = ColumnMap.this.entrySet().iterator();

            Entry var3;
            do {
               if (!var2.hasNext()) {
                  return false;
               }

               var3 = (Entry)var2.next();
            } while(!((Map)var3.getValue()).equals(var1));

            StandardTable.this.removeColumn(var3.getKey());
            return true;
         }

         public boolean removeAll(Collection<?> var1) {
            Preconditions.checkNotNull(var1);
            boolean var2 = false;
            Iterator var3 = Lists.newArrayList(StandardTable.this.columnKeySet().iterator()).iterator();

            while(var3.hasNext()) {
               Object var4 = var3.next();
               if (var1.contains(StandardTable.this.column(var4))) {
                  StandardTable.this.removeColumn(var4);
                  var2 = true;
               }
            }

            return var2;
         }

         public boolean retainAll(Collection<?> var1) {
            Preconditions.checkNotNull(var1);
            boolean var2 = false;
            Iterator var3 = Lists.newArrayList(StandardTable.this.columnKeySet().iterator()).iterator();

            while(var3.hasNext()) {
               Object var4 = var3.next();
               if (!var1.contains(StandardTable.this.column(var4))) {
                  StandardTable.this.removeColumn(var4);
                  var2 = true;
               }
            }

            return var2;
         }
      }

      class ColumnMapEntrySet extends StandardTable<R, C, V>.TableSet<Entry<C, Map<R, V>>> {
         ColumnMapEntrySet() {
            super(null);
         }

         public Iterator<Entry<C, Map<R, V>>> iterator() {
            return Maps.asMapEntryIterator(StandardTable.this.columnKeySet(), new Function<C, Map<R, V>>() {
               public Map<R, V> apply(C var1) {
                  return StandardTable.this.column(var1);
               }
            });
         }

         public int size() {
            return StandardTable.this.columnKeySet().size();
         }

         public boolean contains(Object var1) {
            if (var1 instanceof Entry) {
               Entry var2 = (Entry)var1;
               if (StandardTable.this.containsColumn(var2.getKey())) {
                  Object var3 = var2.getKey();
                  return ColumnMap.this.get(var3).equals(var2.getValue());
               }
            }

            return false;
         }

         public boolean remove(Object var1) {
            if (this.contains(var1)) {
               Entry var2 = (Entry)var1;
               StandardTable.this.removeColumn(var2.getKey());
               return true;
            } else {
               return false;
            }
         }

         public boolean removeAll(Collection<?> var1) {
            Preconditions.checkNotNull(var1);
            return Sets.removeAllImpl(this, (Iterator)var1.iterator());
         }

         public boolean retainAll(Collection<?> var1) {
            Preconditions.checkNotNull(var1);
            boolean var2 = false;
            Iterator var3 = Lists.newArrayList(StandardTable.this.columnKeySet().iterator()).iterator();

            while(var3.hasNext()) {
               Object var4 = var3.next();
               if (!var1.contains(Maps.immutableEntry(var4, StandardTable.this.column(var4)))) {
                  StandardTable.this.removeColumn(var4);
                  var2 = true;
               }
            }

            return var2;
         }
      }
   }

   class RowMap extends Maps.ViewCachingAbstractMap<R, Map<C, V>> {
      RowMap() {
         super();
      }

      public boolean containsKey(Object var1) {
         return StandardTable.this.containsRow(var1);
      }

      public Map<C, V> get(Object var1) {
         return StandardTable.this.containsRow(var1) ? StandardTable.this.row(var1) : null;
      }

      public Map<C, V> remove(Object var1) {
         return var1 == null ? null : (Map)StandardTable.this.backingMap.remove(var1);
      }

      protected Set<Entry<R, Map<C, V>>> createEntrySet() {
         return new StandardTable.RowMap.EntrySet();
      }

      class EntrySet extends StandardTable<R, C, V>.TableSet<Entry<R, Map<C, V>>> {
         EntrySet() {
            super(null);
         }

         public Iterator<Entry<R, Map<C, V>>> iterator() {
            return Maps.asMapEntryIterator(StandardTable.this.backingMap.keySet(), new Function<R, Map<C, V>>() {
               public Map<C, V> apply(R var1) {
                  return StandardTable.this.row(var1);
               }
            });
         }

         public int size() {
            return StandardTable.this.backingMap.size();
         }

         public boolean contains(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Entry var2 = (Entry)var1;
               return var2.getKey() != null && var2.getValue() instanceof Map && Collections2.safeContains(StandardTable.this.backingMap.entrySet(), var2);
            }
         }

         public boolean remove(Object var1) {
            if (!(var1 instanceof Entry)) {
               return false;
            } else {
               Entry var2 = (Entry)var1;
               return var2.getKey() != null && var2.getValue() instanceof Map && StandardTable.this.backingMap.entrySet().remove(var2);
            }
         }
      }
   }

   private class ColumnKeyIterator extends AbstractIterator<C> {
      final Map<C, V> seen;
      final Iterator<Map<C, V>> mapIterator;
      Iterator<Entry<C, V>> entryIterator;

      private ColumnKeyIterator() {
         super();
         this.seen = (Map)StandardTable.this.factory.get();
         this.mapIterator = StandardTable.this.backingMap.values().iterator();
         this.entryIterator = Iterators.emptyIterator();
      }

      protected C computeNext() {
         while(true) {
            if (this.entryIterator.hasNext()) {
               Entry var1 = (Entry)this.entryIterator.next();
               if (!this.seen.containsKey(var1.getKey())) {
                  this.seen.put(var1.getKey(), var1.getValue());
                  return var1.getKey();
               }
            } else {
               if (!this.mapIterator.hasNext()) {
                  return this.endOfData();
               }

               this.entryIterator = ((Map)this.mapIterator.next()).entrySet().iterator();
            }
         }
      }

      // $FF: synthetic method
      ColumnKeyIterator(Object var2) {
         this();
      }
   }

   private class ColumnKeySet extends StandardTable<R, C, V>.TableSet<C> {
      private ColumnKeySet() {
         super(null);
      }

      public Iterator<C> iterator() {
         return StandardTable.this.createColumnKeyIterator();
      }

      public int size() {
         return Iterators.size(this.iterator());
      }

      public boolean remove(Object var1) {
         if (var1 == null) {
            return false;
         } else {
            boolean var2 = false;
            Iterator var3 = StandardTable.this.backingMap.values().iterator();

            while(var3.hasNext()) {
               Map var4 = (Map)var3.next();
               if (var4.keySet().remove(var1)) {
                  var2 = true;
                  if (var4.isEmpty()) {
                     var3.remove();
                  }
               }
            }

            return var2;
         }
      }

      public boolean removeAll(Collection<?> var1) {
         Preconditions.checkNotNull(var1);
         boolean var2 = false;
         Iterator var3 = StandardTable.this.backingMap.values().iterator();

         while(var3.hasNext()) {
            Map var4 = (Map)var3.next();
            if (Iterators.removeAll(var4.keySet().iterator(), var1)) {
               var2 = true;
               if (var4.isEmpty()) {
                  var3.remove();
               }
            }
         }

         return var2;
      }

      public boolean retainAll(Collection<?> var1) {
         Preconditions.checkNotNull(var1);
         boolean var2 = false;
         Iterator var3 = StandardTable.this.backingMap.values().iterator();

         while(var3.hasNext()) {
            Map var4 = (Map)var3.next();
            if (var4.keySet().retainAll(var1)) {
               var2 = true;
               if (var4.isEmpty()) {
                  var3.remove();
               }
            }
         }

         return var2;
      }

      public boolean contains(Object var1) {
         return StandardTable.this.containsColumn(var1);
      }

      // $FF: synthetic method
      ColumnKeySet(Object var2) {
         this();
      }
   }

   private class Column extends Maps.ViewCachingAbstractMap<R, V> {
      final C columnKey;

      Column(C var2) {
         super();
         this.columnKey = Preconditions.checkNotNull(var2);
      }

      public V put(R var1, V var2) {
         return StandardTable.this.put(var1, this.columnKey, var2);
      }

      public V get(Object var1) {
         return StandardTable.this.get(var1, this.columnKey);
      }

      public boolean containsKey(Object var1) {
         return StandardTable.this.contains(var1, this.columnKey);
      }

      public V remove(Object var1) {
         return StandardTable.this.remove(var1, this.columnKey);
      }

      @CanIgnoreReturnValue
      boolean removeFromColumnIf(Predicate<? super Entry<R, V>> var1) {
         boolean var2 = false;
         Iterator var3 = StandardTable.this.backingMap.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            Map var5 = (Map)var4.getValue();
            Object var6 = var5.get(this.columnKey);
            if (var6 != null && var1.apply(Maps.immutableEntry(var4.getKey(), var6))) {
               var5.remove(this.columnKey);
               var2 = true;
               if (var5.isEmpty()) {
                  var3.remove();
               }
            }
         }

         return var2;
      }

      Set<Entry<R, V>> createEntrySet() {
         return new StandardTable.Column.EntrySet();
      }

      Set<R> createKeySet() {
         return new StandardTable.Column.KeySet();
      }

      Collection<V> createValues() {
         return new StandardTable.Column.Values();
      }

      private class Values extends Maps.Values<R, V> {
         Values() {
            super(Column.this);
         }

         public boolean remove(Object var1) {
            return var1 != null && Column.this.removeFromColumnIf(Maps.valuePredicateOnEntries(Predicates.equalTo(var1)));
         }

         public boolean removeAll(Collection<?> var1) {
            return Column.this.removeFromColumnIf(Maps.valuePredicateOnEntries(Predicates.in(var1)));
         }

         public boolean retainAll(Collection<?> var1) {
            return Column.this.removeFromColumnIf(Maps.valuePredicateOnEntries(Predicates.not(Predicates.in(var1))));
         }
      }

      private class KeySet extends Maps.KeySet<R, V> {
         KeySet() {
            super(Column.this);
         }

         public boolean contains(Object var1) {
            return StandardTable.this.contains(var1, Column.this.columnKey);
         }

         public boolean remove(Object var1) {
            return StandardTable.this.remove(var1, Column.this.columnKey) != null;
         }

         public boolean retainAll(Collection<?> var1) {
            return Column.this.removeFromColumnIf(Maps.keyPredicateOnEntries(Predicates.not(Predicates.in(var1))));
         }
      }

      private class EntrySetIterator extends AbstractIterator<Entry<R, V>> {
         final Iterator<Entry<R, Map<C, V>>> iterator;

         private EntrySetIterator() {
            super();
            this.iterator = StandardTable.this.backingMap.entrySet().iterator();
         }

         protected Entry<R, V> computeNext() {
            while(true) {
               if (this.iterator.hasNext()) {
                  final Entry var1 = (Entry)this.iterator.next();
                  if (!((Map)var1.getValue()).containsKey(Column.this.columnKey)) {
                     continue;
                  }

                  class 1EntryImpl extends AbstractMapEntry<R, V> {
                     _EntryImpl/* $FF was: 1EntryImpl*/() {
                        super();
                     }

                     public R getKey() {
                        return var1.getKey();
                     }

                     public V getValue() {
                        return ((Map)var1.getValue()).get(Column.this.columnKey);
                     }

                     public V setValue(V var1x) {
                        return ((Map)var1.getValue()).put(Column.this.columnKey, Preconditions.checkNotNull(var1x));
                     }
                  }

                  return new 1EntryImpl();
               }

               return (Entry)this.endOfData();
            }
         }

         // $FF: synthetic method
         EntrySetIterator(Object var2) {
            this();
         }
      }

      private class EntrySet extends Sets.ImprovedAbstractSet<Entry<R, V>> {
         private EntrySet() {
            super();
         }

         public Iterator<Entry<R, V>> iterator() {
            return Column.this.new EntrySetIterator();
         }

         public int size() {
            int var1 = 0;
            Iterator var2 = StandardTable.this.backingMap.values().iterator();

            while(var2.hasNext()) {
               Map var3 = (Map)var2.next();
               if (var3.containsKey(Column.this.columnKey)) {
                  ++var1;
               }
            }

            return var1;
         }

         public boolean isEmpty() {
            return !StandardTable.this.containsColumn(Column.this.columnKey);
         }

         public void clear() {
            Column.this.removeFromColumnIf(Predicates.alwaysTrue());
         }

         public boolean contains(Object var1) {
            if (var1 instanceof Entry) {
               Entry var2 = (Entry)var1;
               return StandardTable.this.containsMapping(var2.getKey(), Column.this.columnKey, var2.getValue());
            } else {
               return false;
            }
         }

         public boolean remove(Object var1) {
            if (var1 instanceof Entry) {
               Entry var2 = (Entry)var1;
               return StandardTable.this.removeMapping(var2.getKey(), Column.this.columnKey, var2.getValue());
            } else {
               return false;
            }
         }

         public boolean retainAll(Collection<?> var1) {
            return Column.this.removeFromColumnIf(Predicates.not(Predicates.in(var1)));
         }

         // $FF: synthetic method
         EntrySet(Object var2) {
            this();
         }
      }
   }

   class Row extends Maps.IteratorBasedAbstractMap<C, V> {
      final R rowKey;
      Map<C, V> backingRowMap;

      Row(R var2) {
         super();
         this.rowKey = Preconditions.checkNotNull(var2);
      }

      Map<C, V> backingRowMap() {
         return this.backingRowMap != null && (!this.backingRowMap.isEmpty() || !StandardTable.this.backingMap.containsKey(this.rowKey)) ? this.backingRowMap : (this.backingRowMap = this.computeBackingRowMap());
      }

      Map<C, V> computeBackingRowMap() {
         return (Map)StandardTable.this.backingMap.get(this.rowKey);
      }

      void maintainEmptyInvariant() {
         if (this.backingRowMap() != null && this.backingRowMap.isEmpty()) {
            StandardTable.this.backingMap.remove(this.rowKey);
            this.backingRowMap = null;
         }

      }

      public boolean containsKey(Object var1) {
         Map var2 = this.backingRowMap();
         return var1 != null && var2 != null && Maps.safeContainsKey(var2, var1);
      }

      public V get(Object var1) {
         Map var2 = this.backingRowMap();
         return var1 != null && var2 != null ? Maps.safeGet(var2, var1) : null;
      }

      public V put(C var1, V var2) {
         Preconditions.checkNotNull(var1);
         Preconditions.checkNotNull(var2);
         return this.backingRowMap != null && !this.backingRowMap.isEmpty() ? this.backingRowMap.put(var1, var2) : StandardTable.this.put(this.rowKey, var1, var2);
      }

      public V remove(Object var1) {
         Map var2 = this.backingRowMap();
         if (var2 == null) {
            return null;
         } else {
            Object var3 = Maps.safeRemove(var2, var1);
            this.maintainEmptyInvariant();
            return var3;
         }
      }

      public void clear() {
         Map var1 = this.backingRowMap();
         if (var1 != null) {
            var1.clear();
         }

         this.maintainEmptyInvariant();
      }

      public int size() {
         Map var1 = this.backingRowMap();
         return var1 == null ? 0 : var1.size();
      }

      Iterator<Entry<C, V>> entryIterator() {
         Map var1 = this.backingRowMap();
         if (var1 == null) {
            return Iterators.emptyModifiableIterator();
         } else {
            final Iterator var2 = var1.entrySet().iterator();
            return new Iterator<Entry<C, V>>() {
               public boolean hasNext() {
                  return var2.hasNext();
               }

               public Entry<C, V> next() {
                  return Row.this.wrapEntry((Entry)var2.next());
               }

               public void remove() {
                  var2.remove();
                  Row.this.maintainEmptyInvariant();
               }
            };
         }
      }

      Spliterator<Entry<C, V>> entrySpliterator() {
         Map var1 = this.backingRowMap();
         return var1 == null ? Spliterators.emptySpliterator() : CollectSpliterators.map(var1.entrySet().spliterator(), this::wrapEntry);
      }

      Entry<C, V> wrapEntry(final Entry<C, V> var1) {
         return new ForwardingMapEntry<C, V>() {
            protected Entry<C, V> delegate() {
               return var1;
            }

            public V setValue(V var1x) {
               return super.setValue(Preconditions.checkNotNull(var1x));
            }

            public boolean equals(Object var1x) {
               return this.standardEquals(var1x);
            }
         };
      }
   }

   private class CellIterator implements Iterator<Table.Cell<R, C, V>> {
      final Iterator<Entry<R, Map<C, V>>> rowIterator;
      Entry<R, Map<C, V>> rowEntry;
      Iterator<Entry<C, V>> columnIterator;

      private CellIterator() {
         super();
         this.rowIterator = StandardTable.this.backingMap.entrySet().iterator();
         this.columnIterator = Iterators.emptyModifiableIterator();
      }

      public boolean hasNext() {
         return this.rowIterator.hasNext() || this.columnIterator.hasNext();
      }

      public Table.Cell<R, C, V> next() {
         if (!this.columnIterator.hasNext()) {
            this.rowEntry = (Entry)this.rowIterator.next();
            this.columnIterator = ((Map)this.rowEntry.getValue()).entrySet().iterator();
         }

         Entry var1 = (Entry)this.columnIterator.next();
         return Tables.immutableCell(this.rowEntry.getKey(), var1.getKey(), var1.getValue());
      }

      public void remove() {
         this.columnIterator.remove();
         if (((Map)this.rowEntry.getValue()).isEmpty()) {
            this.rowIterator.remove();
         }

      }

      // $FF: synthetic method
      CellIterator(Object var2) {
         this();
      }
   }

   private abstract class TableSet<T> extends Sets.ImprovedAbstractSet<T> {
      private TableSet() {
         super();
      }

      public boolean isEmpty() {
         return StandardTable.this.backingMap.isEmpty();
      }

      public void clear() {
         StandardTable.this.backingMap.clear();
      }

      // $FF: synthetic method
      TableSet(Object var2) {
         this();
      }
   }
}
