package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible
class StandardRowSortedTable<R, C, V> extends StandardTable<R, C, V> implements RowSortedTable<R, C, V> {
   private static final long serialVersionUID = 0L;

   StandardRowSortedTable(SortedMap<R, Map<C, V>> var1, Supplier<? extends Map<C, V>> var2) {
      super(var1, var2);
   }

   private SortedMap<R, Map<C, V>> sortedBackingMap() {
      return (SortedMap)this.backingMap;
   }

   public SortedSet<R> rowKeySet() {
      return (SortedSet)this.rowMap().keySet();
   }

   public SortedMap<R, Map<C, V>> rowMap() {
      return (SortedMap)super.rowMap();
   }

   SortedMap<R, Map<C, V>> createRowMap() {
      return new StandardRowSortedTable.RowSortedMap();
   }

   private class RowSortedMap extends StandardTable<R, C, V>.RowMap implements SortedMap<R, Map<C, V>> {
      private RowSortedMap() {
         super();
      }

      public SortedSet<R> keySet() {
         return (SortedSet)super.keySet();
      }

      SortedSet<R> createKeySet() {
         return new Maps.SortedKeySet(this);
      }

      public Comparator<? super R> comparator() {
         return StandardRowSortedTable.this.sortedBackingMap().comparator();
      }

      public R firstKey() {
         return StandardRowSortedTable.this.sortedBackingMap().firstKey();
      }

      public R lastKey() {
         return StandardRowSortedTable.this.sortedBackingMap().lastKey();
      }

      public SortedMap<R, Map<C, V>> headMap(R var1) {
         Preconditions.checkNotNull(var1);
         return (new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().headMap(var1), StandardRowSortedTable.this.factory)).rowMap();
      }

      public SortedMap<R, Map<C, V>> subMap(R var1, R var2) {
         Preconditions.checkNotNull(var1);
         Preconditions.checkNotNull(var2);
         return (new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().subMap(var1, var2), StandardRowSortedTable.this.factory)).rowMap();
      }

      public SortedMap<R, Map<C, V>> tailMap(R var1) {
         Preconditions.checkNotNull(var1);
         return (new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().tailMap(var1), StandardRowSortedTable.this.factory)).rowMap();
      }

      // $FF: synthetic method
      RowSortedMap(Object var2) {
         this();
      }
   }
}
