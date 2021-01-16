package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.concurrent.Immutable;

@GwtCompatible
@Immutable
final class SparseImmutableTable<R, C, V> extends RegularImmutableTable<R, C, V> {
   static final ImmutableTable<Object, Object, Object> EMPTY = new SparseImmutableTable(ImmutableList.of(), ImmutableSet.of(), ImmutableSet.of());
   private final ImmutableMap<R, Map<C, V>> rowMap;
   private final ImmutableMap<C, Map<R, V>> columnMap;
   private final int[] cellRowIndices;
   private final int[] cellColumnInRowIndices;

   SparseImmutableTable(ImmutableList<Table.Cell<R, C, V>> var1, ImmutableSet<R> var2, ImmutableSet<C> var3) {
      super();
      ImmutableMap var4 = Maps.indexMap(var2);
      LinkedHashMap var5 = Maps.newLinkedHashMap();
      UnmodifiableIterator var6 = var2.iterator();

      while(var6.hasNext()) {
         Object var7 = var6.next();
         var5.put(var7, new LinkedHashMap());
      }

      LinkedHashMap var16 = Maps.newLinkedHashMap();
      UnmodifiableIterator var17 = var3.iterator();

      while(var17.hasNext()) {
         Object var8 = var17.next();
         var16.put(var8, new LinkedHashMap());
      }

      int[] var18 = new int[var1.size()];
      int[] var19 = new int[var1.size()];

      for(int var9 = 0; var9 < var1.size(); ++var9) {
         Table.Cell var10 = (Table.Cell)var1.get(var9);
         Object var11 = var10.getRowKey();
         Object var12 = var10.getColumnKey();
         Object var13 = var10.getValue();
         var18[var9] = (Integer)var4.get(var11);
         Map var14 = (Map)var5.get(var11);
         var19[var9] = var14.size();
         Object var15 = var14.put(var12, var13);
         if (var15 != null) {
            throw new IllegalArgumentException("Duplicate value for row=" + var11 + ", column=" + var12 + ": " + var13 + ", " + var15);
         }

         ((Map)var16.get(var12)).put(var11, var13);
      }

      this.cellRowIndices = var18;
      this.cellColumnInRowIndices = var19;
      ImmutableMap.Builder var20 = new ImmutableMap.Builder(var5.size());
      Iterator var21 = var5.entrySet().iterator();

      while(var21.hasNext()) {
         Entry var23 = (Entry)var21.next();
         var20.put(var23.getKey(), ImmutableMap.copyOf((Map)var23.getValue()));
      }

      this.rowMap = var20.build();
      ImmutableMap.Builder var22 = new ImmutableMap.Builder(var16.size());
      Iterator var24 = var16.entrySet().iterator();

      while(var24.hasNext()) {
         Entry var25 = (Entry)var24.next();
         var22.put(var25.getKey(), ImmutableMap.copyOf((Map)var25.getValue()));
      }

      this.columnMap = var22.build();
   }

   public ImmutableMap<C, Map<R, V>> columnMap() {
      return this.columnMap;
   }

   public ImmutableMap<R, Map<C, V>> rowMap() {
      return this.rowMap;
   }

   public int size() {
      return this.cellRowIndices.length;
   }

   Table.Cell<R, C, V> getCell(int var1) {
      int var2 = this.cellRowIndices[var1];
      Entry var3 = (Entry)this.rowMap.entrySet().asList().get(var2);
      ImmutableMap var4 = (ImmutableMap)var3.getValue();
      int var5 = this.cellColumnInRowIndices[var1];
      Entry var6 = (Entry)var4.entrySet().asList().get(var5);
      return cellOf(var3.getKey(), var6.getKey(), var6.getValue());
   }

   V getValue(int var1) {
      int var2 = this.cellRowIndices[var1];
      ImmutableMap var3 = (ImmutableMap)this.rowMap.values().asList().get(var2);
      int var4 = this.cellColumnInRowIndices[var1];
      return var3.values().asList().get(var4);
   }

   ImmutableTable.SerializedForm createSerializedForm() {
      ImmutableMap var1 = Maps.indexMap(this.columnKeySet());
      int[] var2 = new int[this.cellSet().size()];
      int var3 = 0;

      Table.Cell var5;
      for(UnmodifiableIterator var4 = this.cellSet().iterator(); var4.hasNext(); var2[var3++] = (Integer)var1.get(var5.getColumnKey())) {
         var5 = (Table.Cell)var4.next();
      }

      return ImmutableTable.SerializedForm.create(this, this.cellRowIndices, var2);
   }
}
