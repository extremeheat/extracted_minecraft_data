package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import javax.annotation.Nullable;

@GwtCompatible
public final class Tables {
   private static final Function<? extends Map<?, ?>, ? extends Map<?, ?>> UNMODIFIABLE_WRAPPER = new Function<Map<Object, Object>, Map<Object, Object>>() {
      public Map<Object, Object> apply(Map<Object, Object> var1) {
         return Collections.unmodifiableMap(var1);
      }
   };

   private Tables() {
      super();
   }

   @Beta
   public static <T, R, C, V, I extends Table<R, C, V>> Collector<T, ?, I> toTable(java.util.function.Function<? super T, ? extends R> var0, java.util.function.Function<? super T, ? extends C> var1, java.util.function.Function<? super T, ? extends V> var2, Supplier<I> var3) {
      return toTable(var0, var1, var2, (var0x, var1x) -> {
         throw new IllegalStateException("Conflicting values " + var0x + " and " + var1x);
      }, var3);
   }

   public static <T, R, C, V, I extends Table<R, C, V>> Collector<T, ?, I> toTable(java.util.function.Function<? super T, ? extends R> var0, java.util.function.Function<? super T, ? extends C> var1, java.util.function.Function<? super T, ? extends V> var2, BinaryOperator<V> var3, Supplier<I> var4) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var3);
      Preconditions.checkNotNull(var4);
      return Collector.of(var4, (var4x, var5) -> {
         merge(var4x, var0.apply(var5), var1.apply(var5), var2.apply(var5), var3);
      }, (var1x, var2x) -> {
         Iterator var3x = var2x.cellSet().iterator();

         while(var3x.hasNext()) {
            Table.Cell var4 = (Table.Cell)var3x.next();
            merge(var1x, var4.getRowKey(), var4.getColumnKey(), var4.getValue(), var3);
         }

         return var1x;
      });
   }

   private static <R, C, V> void merge(Table<R, C, V> var0, R var1, C var2, V var3, BinaryOperator<V> var4) {
      Preconditions.checkNotNull(var3);
      Object var5 = var0.get(var1, var2);
      if (var5 == null) {
         var0.put(var1, var2, var3);
      } else {
         Object var6 = var4.apply(var5, var3);
         if (var6 == null) {
            var0.remove(var1, var2);
         } else {
            var0.put(var1, var2, var6);
         }
      }

   }

   public static <R, C, V> Table.Cell<R, C, V> immutableCell(@Nullable R var0, @Nullable C var1, @Nullable V var2) {
      return new Tables.ImmutableCell(var0, var1, var2);
   }

   public static <R, C, V> Table<C, R, V> transpose(Table<R, C, V> var0) {
      return (Table)(var0 instanceof Tables.TransposeTable ? ((Tables.TransposeTable)var0).original : new Tables.TransposeTable(var0));
   }

   @Beta
   public static <R, C, V> Table<R, C, V> newCustomTable(Map<R, Map<C, V>> var0, com.google.common.base.Supplier<? extends Map<C, V>> var1) {
      Preconditions.checkArgument(var0.isEmpty());
      Preconditions.checkNotNull(var1);
      return new StandardTable(var0, var1);
   }

   @Beta
   public static <R, C, V1, V2> Table<R, C, V2> transformValues(Table<R, C, V1> var0, Function<? super V1, V2> var1) {
      return new Tables.TransformedTable(var0, var1);
   }

   public static <R, C, V> Table<R, C, V> unmodifiableTable(Table<? extends R, ? extends C, ? extends V> var0) {
      return new Tables.UnmodifiableTable(var0);
   }

   @Beta
   public static <R, C, V> RowSortedTable<R, C, V> unmodifiableRowSortedTable(RowSortedTable<R, ? extends C, ? extends V> var0) {
      return new Tables.UnmodifiableRowSortedMap(var0);
   }

   private static <K, V> Function<Map<K, V>, Map<K, V>> unmodifiableWrapper() {
      return UNMODIFIABLE_WRAPPER;
   }

   static boolean equalsImpl(Table<?, ?, ?> var0, @Nullable Object var1) {
      if (var1 == var0) {
         return true;
      } else if (var1 instanceof Table) {
         Table var2 = (Table)var1;
         return var0.cellSet().equals(var2.cellSet());
      } else {
         return false;
      }
   }

   static final class UnmodifiableRowSortedMap<R, C, V> extends Tables.UnmodifiableTable<R, C, V> implements RowSortedTable<R, C, V> {
      private static final long serialVersionUID = 0L;

      public UnmodifiableRowSortedMap(RowSortedTable<R, ? extends C, ? extends V> var1) {
         super(var1);
      }

      protected RowSortedTable<R, C, V> delegate() {
         return (RowSortedTable)super.delegate();
      }

      public SortedMap<R, Map<C, V>> rowMap() {
         Function var1 = Tables.unmodifiableWrapper();
         return Collections.unmodifiableSortedMap(Maps.transformValues(this.delegate().rowMap(), var1));
      }

      public SortedSet<R> rowKeySet() {
         return Collections.unmodifiableSortedSet(this.delegate().rowKeySet());
      }
   }

   private static class UnmodifiableTable<R, C, V> extends ForwardingTable<R, C, V> implements Serializable {
      final Table<? extends R, ? extends C, ? extends V> delegate;
      private static final long serialVersionUID = 0L;

      UnmodifiableTable(Table<? extends R, ? extends C, ? extends V> var1) {
         super();
         this.delegate = (Table)Preconditions.checkNotNull(var1);
      }

      protected Table<R, C, V> delegate() {
         return this.delegate;
      }

      public Set<Table.Cell<R, C, V>> cellSet() {
         return Collections.unmodifiableSet(super.cellSet());
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public Map<R, V> column(@Nullable C var1) {
         return Collections.unmodifiableMap(super.column(var1));
      }

      public Set<C> columnKeySet() {
         return Collections.unmodifiableSet(super.columnKeySet());
      }

      public Map<C, Map<R, V>> columnMap() {
         Function var1 = Tables.unmodifiableWrapper();
         return Collections.unmodifiableMap(Maps.transformValues(super.columnMap(), var1));
      }

      public V put(@Nullable R var1, @Nullable C var2, @Nullable V var3) {
         throw new UnsupportedOperationException();
      }

      public void putAll(Table<? extends R, ? extends C, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public V remove(@Nullable Object var1, @Nullable Object var2) {
         throw new UnsupportedOperationException();
      }

      public Map<C, V> row(@Nullable R var1) {
         return Collections.unmodifiableMap(super.row(var1));
      }

      public Set<R> rowKeySet() {
         return Collections.unmodifiableSet(super.rowKeySet());
      }

      public Map<R, Map<C, V>> rowMap() {
         Function var1 = Tables.unmodifiableWrapper();
         return Collections.unmodifiableMap(Maps.transformValues(super.rowMap(), var1));
      }

      public Collection<V> values() {
         return Collections.unmodifiableCollection(super.values());
      }
   }

   private static class TransformedTable<R, C, V1, V2> extends AbstractTable<R, C, V2> {
      final Table<R, C, V1> fromTable;
      final Function<? super V1, V2> function;

      TransformedTable(Table<R, C, V1> var1, Function<? super V1, V2> var2) {
         super();
         this.fromTable = (Table)Preconditions.checkNotNull(var1);
         this.function = (Function)Preconditions.checkNotNull(var2);
      }

      public boolean contains(Object var1, Object var2) {
         return this.fromTable.contains(var1, var2);
      }

      public V2 get(Object var1, Object var2) {
         return this.contains(var1, var2) ? this.function.apply(this.fromTable.get(var1, var2)) : null;
      }

      public int size() {
         return this.fromTable.size();
      }

      public void clear() {
         this.fromTable.clear();
      }

      public V2 put(R var1, C var2, V2 var3) {
         throw new UnsupportedOperationException();
      }

      public void putAll(Table<? extends R, ? extends C, ? extends V2> var1) {
         throw new UnsupportedOperationException();
      }

      public V2 remove(Object var1, Object var2) {
         return this.contains(var1, var2) ? this.function.apply(this.fromTable.remove(var1, var2)) : null;
      }

      public Map<C, V2> row(R var1) {
         return Maps.transformValues(this.fromTable.row(var1), this.function);
      }

      public Map<R, V2> column(C var1) {
         return Maps.transformValues(this.fromTable.column(var1), this.function);
      }

      Function<Table.Cell<R, C, V1>, Table.Cell<R, C, V2>> cellFunction() {
         return new Function<Table.Cell<R, C, V1>, Table.Cell<R, C, V2>>() {
            public Table.Cell<R, C, V2> apply(Table.Cell<R, C, V1> var1) {
               return Tables.immutableCell(var1.getRowKey(), var1.getColumnKey(), TransformedTable.this.function.apply(var1.getValue()));
            }
         };
      }

      Iterator<Table.Cell<R, C, V2>> cellIterator() {
         return Iterators.transform(this.fromTable.cellSet().iterator(), this.cellFunction());
      }

      Spliterator<Table.Cell<R, C, V2>> cellSpliterator() {
         return CollectSpliterators.map(this.fromTable.cellSet().spliterator(), this.cellFunction());
      }

      public Set<R> rowKeySet() {
         return this.fromTable.rowKeySet();
      }

      public Set<C> columnKeySet() {
         return this.fromTable.columnKeySet();
      }

      Collection<V2> createValues() {
         return Collections2.transform(this.fromTable.values(), this.function);
      }

      public Map<R, Map<C, V2>> rowMap() {
         Function var1 = new Function<Map<C, V1>, Map<C, V2>>() {
            public Map<C, V2> apply(Map<C, V1> var1) {
               return Maps.transformValues(var1, TransformedTable.this.function);
            }
         };
         return Maps.transformValues(this.fromTable.rowMap(), var1);
      }

      public Map<C, Map<R, V2>> columnMap() {
         Function var1 = new Function<Map<R, V1>, Map<R, V2>>() {
            public Map<R, V2> apply(Map<R, V1> var1) {
               return Maps.transformValues(var1, TransformedTable.this.function);
            }
         };
         return Maps.transformValues(this.fromTable.columnMap(), var1);
      }
   }

   private static class TransposeTable<C, R, V> extends AbstractTable<C, R, V> {
      final Table<R, C, V> original;
      private static final Function<Table.Cell<?, ?, ?>, Table.Cell<?, ?, ?>> TRANSPOSE_CELL = new Function<Table.Cell<?, ?, ?>, Table.Cell<?, ?, ?>>() {
         public Table.Cell<?, ?, ?> apply(Table.Cell<?, ?, ?> var1) {
            return Tables.immutableCell(var1.getColumnKey(), var1.getRowKey(), var1.getValue());
         }
      };

      TransposeTable(Table<R, C, V> var1) {
         super();
         this.original = (Table)Preconditions.checkNotNull(var1);
      }

      public void clear() {
         this.original.clear();
      }

      public Map<C, V> column(R var1) {
         return this.original.row(var1);
      }

      public Set<R> columnKeySet() {
         return this.original.rowKeySet();
      }

      public Map<R, Map<C, V>> columnMap() {
         return this.original.rowMap();
      }

      public boolean contains(@Nullable Object var1, @Nullable Object var2) {
         return this.original.contains(var2, var1);
      }

      public boolean containsColumn(@Nullable Object var1) {
         return this.original.containsRow(var1);
      }

      public boolean containsRow(@Nullable Object var1) {
         return this.original.containsColumn(var1);
      }

      public boolean containsValue(@Nullable Object var1) {
         return this.original.containsValue(var1);
      }

      public V get(@Nullable Object var1, @Nullable Object var2) {
         return this.original.get(var2, var1);
      }

      public V put(C var1, R var2, V var3) {
         return this.original.put(var2, var1, var3);
      }

      public void putAll(Table<? extends C, ? extends R, ? extends V> var1) {
         this.original.putAll(Tables.transpose(var1));
      }

      public V remove(@Nullable Object var1, @Nullable Object var2) {
         return this.original.remove(var2, var1);
      }

      public Map<R, V> row(C var1) {
         return this.original.column(var1);
      }

      public Set<C> rowKeySet() {
         return this.original.columnKeySet();
      }

      public Map<C, Map<R, V>> rowMap() {
         return this.original.columnMap();
      }

      public int size() {
         return this.original.size();
      }

      public Collection<V> values() {
         return this.original.values();
      }

      Iterator<Table.Cell<C, R, V>> cellIterator() {
         return Iterators.transform(this.original.cellSet().iterator(), TRANSPOSE_CELL);
      }

      Spliterator<Table.Cell<C, R, V>> cellSpliterator() {
         return CollectSpliterators.map(this.original.cellSet().spliterator(), TRANSPOSE_CELL);
      }
   }

   abstract static class AbstractCell<R, C, V> implements Table.Cell<R, C, V> {
      AbstractCell() {
         super();
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Table.Cell)) {
            return false;
         } else {
            Table.Cell var2 = (Table.Cell)var1;
            return Objects.equal(this.getRowKey(), var2.getRowKey()) && Objects.equal(this.getColumnKey(), var2.getColumnKey()) && Objects.equal(this.getValue(), var2.getValue());
         }
      }

      public int hashCode() {
         return Objects.hashCode(this.getRowKey(), this.getColumnKey(), this.getValue());
      }

      public String toString() {
         return "(" + this.getRowKey() + "," + this.getColumnKey() + ")=" + this.getValue();
      }
   }

   static final class ImmutableCell<R, C, V> extends Tables.AbstractCell<R, C, V> implements Serializable {
      private final R rowKey;
      private final C columnKey;
      private final V value;
      private static final long serialVersionUID = 0L;

      ImmutableCell(@Nullable R var1, @Nullable C var2, @Nullable V var3) {
         super();
         this.rowKey = var1;
         this.columnKey = var2;
         this.value = var3;
      }

      public R getRowKey() {
         return this.rowKey;
      }

      public C getColumnKey() {
         return this.columnKey;
      }

      public V getValue() {
         return this.value;
      }
   }
}
