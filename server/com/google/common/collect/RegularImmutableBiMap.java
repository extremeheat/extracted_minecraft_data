package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.io.Serializable;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
class RegularImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {
   static final RegularImmutableBiMap<Object, Object> EMPTY;
   static final double MAX_LOAD_FACTOR = 1.2D;
   private final transient ImmutableMapEntry<K, V>[] keyTable;
   private final transient ImmutableMapEntry<K, V>[] valueTable;
   private final transient Entry<K, V>[] entries;
   private final transient int mask;
   private final transient int hashCode;
   @LazyInit
   @RetainedWith
   private transient ImmutableBiMap<V, K> inverse;

   static <K, V> RegularImmutableBiMap<K, V> fromEntries(Entry<K, V>... var0) {
      return fromEntryArray(var0.length, var0);
   }

   static <K, V> RegularImmutableBiMap<K, V> fromEntryArray(int var0, Entry<K, V>[] var1) {
      Preconditions.checkPositionIndex(var0, var1.length);
      int var2 = Hashing.closedTableSize(var0, 1.2D);
      int var3 = var2 - 1;
      ImmutableMapEntry[] var4 = ImmutableMapEntry.createEntryArray(var2);
      ImmutableMapEntry[] var5 = ImmutableMapEntry.createEntryArray(var2);
      Object var6;
      if (var0 == var1.length) {
         var6 = var1;
      } else {
         var6 = ImmutableMapEntry.createEntryArray(var0);
      }

      int var7 = 0;

      for(int var8 = 0; var8 < var0; ++var8) {
         Entry var9 = var1[var8];
         Object var10 = var9.getKey();
         Object var11 = var9.getValue();
         CollectPreconditions.checkEntryNotNull(var10, var11);
         int var12 = var10.hashCode();
         int var13 = var11.hashCode();
         int var14 = Hashing.smear(var12) & var3;
         int var15 = Hashing.smear(var13) & var3;
         ImmutableMapEntry var16 = var4[var14];
         RegularImmutableMap.checkNoConflictInKeyBucket(var10, var9, var16);
         ImmutableMapEntry var17 = var5[var15];
         checkNoConflictInValueBucket(var11, var9, var17);
         Object var18;
         if (var17 == null && var16 == null) {
            boolean var19 = var9 instanceof ImmutableMapEntry && ((ImmutableMapEntry)var9).isReusable();
            var18 = var19 ? (ImmutableMapEntry)var9 : new ImmutableMapEntry(var10, var11);
         } else {
            var18 = new ImmutableMapEntry.NonTerminalImmutableBiMapEntry(var10, var11, var16, var17);
         }

         var4[var14] = (ImmutableMapEntry)var18;
         var5[var15] = (ImmutableMapEntry)var18;
         ((Object[])var6)[var8] = var18;
         var7 += var12 ^ var13;
      }

      return new RegularImmutableBiMap(var4, var5, (Entry[])var6, var3, var7);
   }

   private RegularImmutableBiMap(ImmutableMapEntry<K, V>[] var1, ImmutableMapEntry<K, V>[] var2, Entry<K, V>[] var3, int var4, int var5) {
      super();
      this.keyTable = var1;
      this.valueTable = var2;
      this.entries = var3;
      this.mask = var4;
      this.hashCode = var5;
   }

   private static void checkNoConflictInValueBucket(Object var0, Entry<?, ?> var1, @Nullable ImmutableMapEntry<?, ?> var2) {
      while(var2 != null) {
         checkNoConflict(!var0.equals(var2.getValue()), "value", var1, var2);
         var2 = var2.getNextInValueBucket();
      }

   }

   @Nullable
   public V get(@Nullable Object var1) {
      return this.keyTable == null ? null : RegularImmutableMap.get(var1, this.keyTable, this.mask);
   }

   ImmutableSet<Entry<K, V>> createEntrySet() {
      return (ImmutableSet)(this.isEmpty() ? ImmutableSet.of() : new ImmutableMapEntrySet.RegularEntrySet(this, this.entries));
   }

   public void forEach(BiConsumer<? super K, ? super V> var1) {
      Preconditions.checkNotNull(var1);
      Entry[] var2 = this.entries;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Entry var5 = var2[var4];
         var1.accept(var5.getKey(), var5.getValue());
      }

   }

   boolean isHashCodeFast() {
      return true;
   }

   public int hashCode() {
      return this.hashCode;
   }

   boolean isPartialView() {
      return false;
   }

   public int size() {
      return this.entries.length;
   }

   public ImmutableBiMap<V, K> inverse() {
      if (this.isEmpty()) {
         return ImmutableBiMap.of();
      } else {
         ImmutableBiMap var1 = this.inverse;
         return var1 == null ? (this.inverse = new RegularImmutableBiMap.Inverse()) : var1;
      }
   }

   static {
      EMPTY = new RegularImmutableBiMap((ImmutableMapEntry[])null, (ImmutableMapEntry[])null, (Entry[])ImmutableMap.EMPTY_ENTRY_ARRAY, 0, 0);
   }

   private static class InverseSerializedForm<K, V> implements Serializable {
      private final ImmutableBiMap<K, V> forward;
      private static final long serialVersionUID = 1L;

      InverseSerializedForm(ImmutableBiMap<K, V> var1) {
         super();
         this.forward = var1;
      }

      Object readResolve() {
         return this.forward.inverse();
      }
   }

   private final class Inverse extends ImmutableBiMap<V, K> {
      private Inverse() {
         super();
      }

      public int size() {
         return this.inverse().size();
      }

      public ImmutableBiMap<K, V> inverse() {
         return RegularImmutableBiMap.this;
      }

      public void forEach(BiConsumer<? super V, ? super K> var1) {
         Preconditions.checkNotNull(var1);
         RegularImmutableBiMap.this.forEach((var1x, var2) -> {
            var1.accept(var2, var1x);
         });
      }

      public K get(@Nullable Object var1) {
         if (var1 != null && RegularImmutableBiMap.this.valueTable != null) {
            int var2 = Hashing.smear(var1.hashCode()) & RegularImmutableBiMap.this.mask;

            for(ImmutableMapEntry var3 = RegularImmutableBiMap.this.valueTable[var2]; var3 != null; var3 = var3.getNextInValueBucket()) {
               if (var1.equals(var3.getValue())) {
                  return var3.getKey();
               }
            }

            return null;
         } else {
            return null;
         }
      }

      ImmutableSet<Entry<V, K>> createEntrySet() {
         return new RegularImmutableBiMap.Inverse.InverseEntrySet();
      }

      boolean isPartialView() {
         return false;
      }

      Object writeReplace() {
         return new RegularImmutableBiMap.InverseSerializedForm(RegularImmutableBiMap.this);
      }

      // $FF: synthetic method
      Inverse(Object var2) {
         this();
      }

      final class InverseEntrySet extends ImmutableMapEntrySet<V, K> {
         InverseEntrySet() {
            super();
         }

         ImmutableMap<V, K> map() {
            return Inverse.this;
         }

         boolean isHashCodeFast() {
            return true;
         }

         public int hashCode() {
            return RegularImmutableBiMap.this.hashCode;
         }

         public UnmodifiableIterator<Entry<V, K>> iterator() {
            return this.asList().iterator();
         }

         public void forEach(Consumer<? super Entry<V, K>> var1) {
            this.asList().forEach(var1);
         }

         ImmutableList<Entry<V, K>> createAsList() {
            return new ImmutableAsList<Entry<V, K>>() {
               public Entry<V, K> get(int var1) {
                  Entry var2 = RegularImmutableBiMap.this.entries[var1];
                  return Maps.immutableEntry(var2.getValue(), var2.getKey());
               }

               ImmutableCollection<Entry<V, K>> delegateCollection() {
                  return InverseEntrySet.this;
               }
            };
         }
      }
   }
}
