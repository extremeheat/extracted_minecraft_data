package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractTable<R, C, V> implements Table<R, C, V> {
   private transient Set<Table.Cell<R, C, V>> cellSet;
   private transient Collection<V> values;

   AbstractTable() {
      super();
   }

   public boolean containsRow(@Nullable Object var1) {
      return Maps.safeContainsKey(this.rowMap(), var1);
   }

   public boolean containsColumn(@Nullable Object var1) {
      return Maps.safeContainsKey(this.columnMap(), var1);
   }

   public Set<R> rowKeySet() {
      return this.rowMap().keySet();
   }

   public Set<C> columnKeySet() {
      return this.columnMap().keySet();
   }

   public boolean containsValue(@Nullable Object var1) {
      Iterator var2 = this.rowMap().values().iterator();

      Map var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Map)var2.next();
      } while(!var3.containsValue(var1));

      return true;
   }

   public boolean contains(@Nullable Object var1, @Nullable Object var2) {
      Map var3 = (Map)Maps.safeGet(this.rowMap(), var1);
      return var3 != null && Maps.safeContainsKey(var3, var2);
   }

   public V get(@Nullable Object var1, @Nullable Object var2) {
      Map var3 = (Map)Maps.safeGet(this.rowMap(), var1);
      return var3 == null ? null : Maps.safeGet(var3, var2);
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void clear() {
      Iterators.clear(this.cellSet().iterator());
   }

   @CanIgnoreReturnValue
   public V remove(@Nullable Object var1, @Nullable Object var2) {
      Map var3 = (Map)Maps.safeGet(this.rowMap(), var1);
      return var3 == null ? null : Maps.safeRemove(var3, var2);
   }

   @CanIgnoreReturnValue
   public V put(R var1, C var2, V var3) {
      return this.row(var1).put(var2, var3);
   }

   public void putAll(Table<? extends R, ? extends C, ? extends V> var1) {
      Iterator var2 = var1.cellSet().iterator();

      while(var2.hasNext()) {
         Table.Cell var3 = (Table.Cell)var2.next();
         this.put(var3.getRowKey(), var3.getColumnKey(), var3.getValue());
      }

   }

   public Set<Table.Cell<R, C, V>> cellSet() {
      Set var1 = this.cellSet;
      return var1 == null ? (this.cellSet = this.createCellSet()) : var1;
   }

   Set<Table.Cell<R, C, V>> createCellSet() {
      return new AbstractTable.CellSet();
   }

   abstract Iterator<Table.Cell<R, C, V>> cellIterator();

   abstract Spliterator<Table.Cell<R, C, V>> cellSpliterator();

   public Collection<V> values() {
      Collection var1 = this.values;
      return var1 == null ? (this.values = this.createValues()) : var1;
   }

   Collection<V> createValues() {
      return new AbstractTable.Values();
   }

   Iterator<V> valuesIterator() {
      return new TransformedIterator<Table.Cell<R, C, V>, V>(this.cellSet().iterator()) {
         V transform(Table.Cell<R, C, V> var1) {
            return var1.getValue();
         }
      };
   }

   Spliterator<V> valuesSpliterator() {
      return CollectSpliterators.map(this.cellSpliterator(), Table.Cell::getValue);
   }

   public boolean equals(@Nullable Object var1) {
      return Tables.equalsImpl(this, var1);
   }

   public int hashCode() {
      return this.cellSet().hashCode();
   }

   public String toString() {
      return this.rowMap().toString();
   }

   class Values extends AbstractCollection<V> {
      Values() {
         super();
      }

      public Iterator<V> iterator() {
         return AbstractTable.this.valuesIterator();
      }

      public boolean contains(Object var1) {
         return AbstractTable.this.containsValue(var1);
      }

      public void clear() {
         AbstractTable.this.clear();
      }

      public int size() {
         return AbstractTable.this.size();
      }
   }

   class CellSet extends AbstractSet<Table.Cell<R, C, V>> {
      CellSet() {
         super();
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof Table.Cell)) {
            return false;
         } else {
            Table.Cell var2 = (Table.Cell)var1;
            Map var3 = (Map)Maps.safeGet(AbstractTable.this.rowMap(), var2.getRowKey());
            return var3 != null && Collections2.safeContains(var3.entrySet(), Maps.immutableEntry(var2.getColumnKey(), var2.getValue()));
         }
      }

      public boolean remove(@Nullable Object var1) {
         if (!(var1 instanceof Table.Cell)) {
            return false;
         } else {
            Table.Cell var2 = (Table.Cell)var1;
            Map var3 = (Map)Maps.safeGet(AbstractTable.this.rowMap(), var2.getRowKey());
            return var3 != null && Collections2.safeRemove(var3.entrySet(), Maps.immutableEntry(var2.getColumnKey(), var2.getValue()));
         }
      }

      public void clear() {
         AbstractTable.this.clear();
      }

      public Iterator<Table.Cell<R, C, V>> iterator() {
         return AbstractTable.this.cellIterator();
      }

      public Spliterator<Table.Cell<R, C, V>> spliterator() {
         return AbstractTable.this.cellSpliterator();
      }

      public int size() {
         return AbstractTable.this.size();
      }
   }
}
