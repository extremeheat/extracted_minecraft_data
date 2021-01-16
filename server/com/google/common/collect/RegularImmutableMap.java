package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.j2objc.annotations.Weak;
import java.io.Serializable;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
final class RegularImmutableMap<K, V> extends ImmutableMap<K, V> {
   private final transient Entry<K, V>[] entries;
   private final transient ImmutableMapEntry<K, V>[] table;
   private final transient int mask;
   private static final double MAX_LOAD_FACTOR = 1.2D;
   private static final long serialVersionUID = 0L;

   static <K, V> RegularImmutableMap<K, V> fromEntries(Entry<K, V>... var0) {
      return fromEntryArray(var0.length, var0);
   }

   static <K, V> RegularImmutableMap<K, V> fromEntryArray(int var0, Entry<K, V>[] var1) {
      Preconditions.checkPositionIndex(var0, var1.length);
      Object var2;
      if (var0 == var1.length) {
         var2 = var1;
      } else {
         var2 = ImmutableMapEntry.createEntryArray(var0);
      }

      int var3 = Hashing.closedTableSize(var0, 1.2D);
      ImmutableMapEntry[] var4 = ImmutableMapEntry.createEntryArray(var3);
      int var5 = var3 - 1;

      for(int var6 = 0; var6 < var0; ++var6) {
         Entry var7 = var1[var6];
         Object var8 = var7.getKey();
         Object var9 = var7.getValue();
         CollectPreconditions.checkEntryNotNull(var8, var9);
         int var10 = Hashing.smear(var8.hashCode()) & var5;
         ImmutableMapEntry var11 = var4[var10];
         Object var12;
         if (var11 != null) {
            var12 = new ImmutableMapEntry.NonTerminalImmutableMapEntry(var8, var9, var11);
         } else {
            boolean var13 = var7 instanceof ImmutableMapEntry && ((ImmutableMapEntry)var7).isReusable();
            var12 = var13 ? (ImmutableMapEntry)var7 : new ImmutableMapEntry(var8, var9);
         }

         var4[var10] = (ImmutableMapEntry)var12;
         ((Object[])var2)[var6] = var12;
         checkNoConflictInKeyBucket(var8, (Entry)var12, var11);
      }

      return new RegularImmutableMap((Entry[])var2, var4, var5);
   }

   private RegularImmutableMap(Entry<K, V>[] var1, ImmutableMapEntry<K, V>[] var2, int var3) {
      super();
      this.entries = var1;
      this.table = var2;
      this.mask = var3;
   }

   static void checkNoConflictInKeyBucket(Object var0, Entry<?, ?> var1, @Nullable ImmutableMapEntry<?, ?> var2) {
      while(var2 != null) {
         checkNoConflict(!var0.equals(var2.getKey()), "key", var1, var2);
         var2 = var2.getNextInKeyBucket();
      }

   }

   public V get(@Nullable Object var1) {
      return get(var1, this.table, this.mask);
   }

   @Nullable
   static <V> V get(@Nullable Object var0, ImmutableMapEntry<?, V>[] var1, int var2) {
      if (var0 == null) {
         return null;
      } else {
         int var3 = Hashing.smear(var0.hashCode()) & var2;

         for(ImmutableMapEntry var4 = var1[var3]; var4 != null; var4 = var4.getNextInKeyBucket()) {
            Object var5 = var4.getKey();
            if (var0.equals(var5)) {
               return var4.getValue();
            }
         }

         return null;
      }
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

   public int size() {
      return this.entries.length;
   }

   boolean isPartialView() {
      return false;
   }

   ImmutableSet<Entry<K, V>> createEntrySet() {
      return new ImmutableMapEntrySet.RegularEntrySet(this, this.entries);
   }

   ImmutableSet<K> createKeySet() {
      return new RegularImmutableMap.KeySet(this);
   }

   ImmutableCollection<V> createValues() {
      return new RegularImmutableMap.Values(this);
   }

   @GwtCompatible(
      emulated = true
   )
   private static final class Values<K, V> extends ImmutableList<V> {
      @Weak
      final RegularImmutableMap<K, V> map;

      Values(RegularImmutableMap<K, V> var1) {
         super();
         this.map = var1;
      }

      public V get(int var1) {
         return this.map.entries[var1].getValue();
      }

      public int size() {
         return this.map.size();
      }

      boolean isPartialView() {
         return true;
      }

      @GwtIncompatible
      Object writeReplace() {
         return new RegularImmutableMap.Values.SerializedForm(this.map);
      }

      @GwtIncompatible
      private static class SerializedForm<V> implements Serializable {
         final ImmutableMap<?, V> map;
         private static final long serialVersionUID = 0L;

         SerializedForm(ImmutableMap<?, V> var1) {
            super();
            this.map = var1;
         }

         Object readResolve() {
            return this.map.values();
         }
      }
   }

   @GwtCompatible(
      emulated = true
   )
   private static final class KeySet<K, V> extends ImmutableSet.Indexed<K> {
      @Weak
      private final RegularImmutableMap<K, V> map;

      KeySet(RegularImmutableMap<K, V> var1) {
         super();
         this.map = var1;
      }

      K get(int var1) {
         return this.map.entries[var1].getKey();
      }

      public boolean contains(Object var1) {
         return this.map.containsKey(var1);
      }

      boolean isPartialView() {
         return true;
      }

      public int size() {
         return this.map.size();
      }

      @GwtIncompatible
      Object writeReplace() {
         return new RegularImmutableMap.KeySet.SerializedForm(this.map);
      }

      @GwtIncompatible
      private static class SerializedForm<K> implements Serializable {
         final ImmutableMap<K, ?> map;
         private static final long serialVersionUID = 0L;

         SerializedForm(ImmutableMap<K, ?> var1) {
            super();
            this.map = var1;
         }

         Object readResolve() {
            return this.map.keySet();
         }
      }
   }
}
