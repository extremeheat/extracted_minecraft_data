package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface Table<R, C, V> {
   boolean contains(@Nullable @CompatibleWith("R") Object var1, @Nullable @CompatibleWith("C") Object var2);

   boolean containsRow(@Nullable @CompatibleWith("R") Object var1);

   boolean containsColumn(@Nullable @CompatibleWith("C") Object var1);

   boolean containsValue(@Nullable @CompatibleWith("V") Object var1);

   V get(@Nullable @CompatibleWith("R") Object var1, @Nullable @CompatibleWith("C") Object var2);

   boolean isEmpty();

   int size();

   boolean equals(@Nullable Object var1);

   int hashCode();

   void clear();

   @Nullable
   @CanIgnoreReturnValue
   V put(R var1, C var2, V var3);

   void putAll(Table<? extends R, ? extends C, ? extends V> var1);

   @Nullable
   @CanIgnoreReturnValue
   V remove(@Nullable @CompatibleWith("R") Object var1, @Nullable @CompatibleWith("C") Object var2);

   Map<C, V> row(R var1);

   Map<R, V> column(C var1);

   Set<Table.Cell<R, C, V>> cellSet();

   Set<R> rowKeySet();

   Set<C> columnKeySet();

   Collection<V> values();

   Map<R, Map<C, V>> rowMap();

   Map<C, Map<R, V>> columnMap();

   public interface Cell<R, C, V> {
      @Nullable
      R getRowKey();

      @Nullable
      C getColumnKey();

      @Nullable
      V getValue();

      boolean equals(@Nullable Object var1);

      int hashCode();
   }
}
