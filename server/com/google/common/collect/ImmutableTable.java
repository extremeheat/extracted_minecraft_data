package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ImmutableTable<R, C, V> extends AbstractTable<R, C, V> implements Serializable {
   @Beta
   public static <T, R, C, V> Collector<T, ?, ImmutableTable<R, C, V>> toImmutableTable(Function<? super T, ? extends R> var0, Function<? super T, ? extends C> var1, Function<? super T, ? extends V> var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      return Collector.of(() -> {
         return new ImmutableTable.Builder();
      }, (var3, var4) -> {
         var3.put(var0.apply(var4), var1.apply(var4), var2.apply(var4));
      }, (var0x, var1x) -> {
         return var0x.combine(var1x);
      }, (var0x) -> {
         return var0x.build();
      });
   }

   public static <T, R, C, V> Collector<T, ?, ImmutableTable<R, C, V>> toImmutableTable(Function<? super T, ? extends R> var0, Function<? super T, ? extends C> var1, Function<? super T, ? extends V> var2, BinaryOperator<V> var3) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var3);
      return Collector.of(() -> {
         return new ImmutableTable.CollectorState();
      }, (var4, var5) -> {
         var4.put(var0.apply(var5), var1.apply(var5), var2.apply(var5), var3);
      }, (var1x, var2x) -> {
         return var1x.combine(var2x, var3);
      }, (var0x) -> {
         return var0x.toTable();
      });
   }

   public static <R, C, V> ImmutableTable<R, C, V> of() {
      return SparseImmutableTable.EMPTY;
   }

   public static <R, C, V> ImmutableTable<R, C, V> of(R var0, C var1, V var2) {
      return new SingletonImmutableTable(var0, var1, var2);
   }

   public static <R, C, V> ImmutableTable<R, C, V> copyOf(Table<? extends R, ? extends C, ? extends V> var0) {
      if (var0 instanceof ImmutableTable) {
         ImmutableTable var1 = (ImmutableTable)var0;
         return var1;
      } else {
         return copyOf((Iterable)var0.cellSet());
      }
   }

   private static <R, C, V> ImmutableTable<R, C, V> copyOf(Iterable<? extends Table.Cell<? extends R, ? extends C, ? extends V>> var0) {
      ImmutableTable.Builder var1 = builder();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Table.Cell var3 = (Table.Cell)var2.next();
         var1.put(var3);
      }

      return var1.build();
   }

   public static <R, C, V> ImmutableTable.Builder<R, C, V> builder() {
      return new ImmutableTable.Builder();
   }

   static <R, C, V> Table.Cell<R, C, V> cellOf(R var0, C var1, V var2) {
      return Tables.immutableCell(Preconditions.checkNotNull(var0), Preconditions.checkNotNull(var1), Preconditions.checkNotNull(var2));
   }

   ImmutableTable() {
      super();
   }

   public ImmutableSet<Table.Cell<R, C, V>> cellSet() {
      return (ImmutableSet)super.cellSet();
   }

   abstract ImmutableSet<Table.Cell<R, C, V>> createCellSet();

   final UnmodifiableIterator<Table.Cell<R, C, V>> cellIterator() {
      throw new AssertionError("should never be called");
   }

   final Spliterator<Table.Cell<R, C, V>> cellSpliterator() {
      throw new AssertionError("should never be called");
   }

   public ImmutableCollection<V> values() {
      return (ImmutableCollection)super.values();
   }

   abstract ImmutableCollection<V> createValues();

   final Iterator<V> valuesIterator() {
      throw new AssertionError("should never be called");
   }

   public ImmutableMap<R, V> column(C var1) {
      Preconditions.checkNotNull(var1);
      return (ImmutableMap)MoreObjects.firstNonNull((ImmutableMap)this.columnMap().get(var1), ImmutableMap.of());
   }

   public ImmutableSet<C> columnKeySet() {
      return this.columnMap().keySet();
   }

   public abstract ImmutableMap<C, Map<R, V>> columnMap();

   public ImmutableMap<C, V> row(R var1) {
      Preconditions.checkNotNull(var1);
      return (ImmutableMap)MoreObjects.firstNonNull((ImmutableMap)this.rowMap().get(var1), ImmutableMap.of());
   }

   public ImmutableSet<R> rowKeySet() {
      return this.rowMap().keySet();
   }

   public abstract ImmutableMap<R, Map<C, V>> rowMap();

   public boolean contains(@Nullable Object var1, @Nullable Object var2) {
      return this.get(var1, var2) != null;
   }

   public boolean containsValue(@Nullable Object var1) {
      return this.values().contains(var1);
   }

   /** @deprecated */
   @Deprecated
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public final V put(R var1, C var2, V var3) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final void putAll(Table<? extends R, ? extends C, ? extends V> var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   public final V remove(Object var1, Object var2) {
      throw new UnsupportedOperationException();
   }

   abstract ImmutableTable.SerializedForm createSerializedForm();

   final Object writeReplace() {
      return this.createSerializedForm();
   }

   static final class SerializedForm implements Serializable {
      private final Object[] rowKeys;
      private final Object[] columnKeys;
      private final Object[] cellValues;
      private final int[] cellRowIndices;
      private final int[] cellColumnIndices;
      private static final long serialVersionUID = 0L;

      private SerializedForm(Object[] var1, Object[] var2, Object[] var3, int[] var4, int[] var5) {
         super();
         this.rowKeys = var1;
         this.columnKeys = var2;
         this.cellValues = var3;
         this.cellRowIndices = var4;
         this.cellColumnIndices = var5;
      }

      static ImmutableTable.SerializedForm create(ImmutableTable<?, ?, ?> var0, int[] var1, int[] var2) {
         return new ImmutableTable.SerializedForm(var0.rowKeySet().toArray(), var0.columnKeySet().toArray(), var0.values().toArray(), var1, var2);
      }

      Object readResolve() {
         if (this.cellValues.length == 0) {
            return ImmutableTable.of();
         } else if (this.cellValues.length == 1) {
            return ImmutableTable.of(this.rowKeys[0], this.columnKeys[0], this.cellValues[0]);
         } else {
            ImmutableList.Builder var1 = new ImmutableList.Builder(this.cellValues.length);

            for(int var2 = 0; var2 < this.cellValues.length; ++var2) {
               var1.add((Object)ImmutableTable.cellOf(this.rowKeys[this.cellRowIndices[var2]], this.columnKeys[this.cellColumnIndices[var2]], this.cellValues[var2]));
            }

            return RegularImmutableTable.forOrderedComponents(var1.build(), ImmutableSet.copyOf(this.rowKeys), ImmutableSet.copyOf(this.columnKeys));
         }
      }
   }

   public static final class Builder<R, C, V> {
      private final List<Table.Cell<R, C, V>> cells = Lists.newArrayList();
      private Comparator<? super R> rowComparator;
      private Comparator<? super C> columnComparator;

      public Builder() {
         super();
      }

      @CanIgnoreReturnValue
      public ImmutableTable.Builder<R, C, V> orderRowsBy(Comparator<? super R> var1) {
         this.rowComparator = (Comparator)Preconditions.checkNotNull(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableTable.Builder<R, C, V> orderColumnsBy(Comparator<? super C> var1) {
         this.columnComparator = (Comparator)Preconditions.checkNotNull(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableTable.Builder<R, C, V> put(R var1, C var2, V var3) {
         this.cells.add(ImmutableTable.cellOf(var1, var2, var3));
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableTable.Builder<R, C, V> put(Table.Cell<? extends R, ? extends C, ? extends V> var1) {
         if (var1 instanceof Tables.ImmutableCell) {
            Preconditions.checkNotNull(var1.getRowKey());
            Preconditions.checkNotNull(var1.getColumnKey());
            Preconditions.checkNotNull(var1.getValue());
            this.cells.add(var1);
         } else {
            this.put(var1.getRowKey(), var1.getColumnKey(), var1.getValue());
         }

         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableTable.Builder<R, C, V> putAll(Table<? extends R, ? extends C, ? extends V> var1) {
         Iterator var2 = var1.cellSet().iterator();

         while(var2.hasNext()) {
            Table.Cell var3 = (Table.Cell)var2.next();
            this.put(var3);
         }

         return this;
      }

      ImmutableTable.Builder<R, C, V> combine(ImmutableTable.Builder<R, C, V> var1) {
         this.cells.addAll(var1.cells);
         return this;
      }

      public ImmutableTable<R, C, V> build() {
         int var1 = this.cells.size();
         switch(var1) {
         case 0:
            return ImmutableTable.of();
         case 1:
            return new SingletonImmutableTable((Table.Cell)Iterables.getOnlyElement(this.cells));
         default:
            return RegularImmutableTable.forCells(this.cells, this.rowComparator, this.columnComparator);
         }
      }
   }

   private static final class MutableCell<R, C, V> extends Tables.AbstractCell<R, C, V> {
      private final R row;
      private final C column;
      private V value;

      MutableCell(R var1, C var2, V var3) {
         super();
         this.row = Preconditions.checkNotNull(var1);
         this.column = Preconditions.checkNotNull(var2);
         this.value = Preconditions.checkNotNull(var3);
      }

      public R getRowKey() {
         return this.row;
      }

      public C getColumnKey() {
         return this.column;
      }

      public V getValue() {
         return this.value;
      }

      void merge(V var1, BinaryOperator<V> var2) {
         Preconditions.checkNotNull(var1);
         this.value = Preconditions.checkNotNull(var2.apply(this.value, var1));
      }
   }

   private static final class CollectorState<R, C, V> {
      final List<ImmutableTable.MutableCell<R, C, V>> insertionOrder;
      final Table<R, C, ImmutableTable.MutableCell<R, C, V>> table;

      private CollectorState() {
         super();
         this.insertionOrder = new ArrayList();
         this.table = HashBasedTable.create();
      }

      void put(R var1, C var2, V var3, BinaryOperator<V> var4) {
         ImmutableTable.MutableCell var5 = (ImmutableTable.MutableCell)this.table.get(var1, var2);
         if (var5 == null) {
            ImmutableTable.MutableCell var6 = new ImmutableTable.MutableCell(var1, var2, var3);
            this.insertionOrder.add(var6);
            this.table.put(var1, var2, var6);
         } else {
            var5.merge(var3, var4);
         }

      }

      ImmutableTable.CollectorState<R, C, V> combine(ImmutableTable.CollectorState<R, C, V> var1, BinaryOperator<V> var2) {
         Iterator var3 = var1.insertionOrder.iterator();

         while(var3.hasNext()) {
            ImmutableTable.MutableCell var4 = (ImmutableTable.MutableCell)var3.next();
            this.put(var4.getRowKey(), var4.getColumnKey(), var4.getValue(), var2);
         }

         return this;
      }

      ImmutableTable<R, C, V> toTable() {
         return ImmutableTable.copyOf((Iterable)this.insertionOrder);
      }

      // $FF: synthetic method
      CollectorState(Object var1) {
         this();
      }
   }
}
