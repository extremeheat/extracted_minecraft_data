package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.IdMap;

public class CrudeIncrementalIntIdentityHashBiMap<K> implements IdMap<K> {
   private static final int NOT_FOUND = -1;
   private static final Object EMPTY_SLOT = null;
   private static final float LOADFACTOR = 0.8F;
   private K[] keys;
   private int[] values;
   private K[] byId;
   private int nextId;
   private int size;

   private CrudeIncrementalIntIdentityHashBiMap(int var1) {
      super();
      this.keys = (K[])(new Object[var1]);
      this.values = new int[var1];
      this.byId = (K[])(new Object[var1]);
   }

   private CrudeIncrementalIntIdentityHashBiMap(K[] var1, int[] var2, K[] var3, int var4, int var5) {
      super();
      this.keys = (K[])var1;
      this.values = var2;
      this.byId = (K[])var3;
      this.nextId = var4;
      this.size = var5;
   }

   public static <A> CrudeIncrementalIntIdentityHashBiMap<A> create(int var0) {
      return new CrudeIncrementalIntIdentityHashBiMap((int)((float)var0 / 0.8F));
   }

   @Override
   public int getId(@Nullable K var1) {
      return this.getValue(this.indexOf((K)var1, this.hash((K)var1)));
   }

   @Nullable
   @Override
   public K byId(int var1) {
      return var1 >= 0 && var1 < this.byId.length ? this.byId[var1] : null;
   }

   private int getValue(int var1) {
      return var1 == -1 ? -1 : this.values[var1];
   }

   public boolean contains(K var1) {
      return this.getId((K)var1) != -1;
   }

   public boolean contains(int var1) {
      return this.byId(var1) != null;
   }

   public int add(K var1) {
      int var2 = this.nextId();
      this.addMapping((K)var1, var2);
      return var2;
   }

   private int nextId() {
      while (this.nextId < this.byId.length && this.byId[this.nextId] != null) {
         this.nextId++;
      }

      return this.nextId;
   }

   private void grow(int var1) {
      Object[] var2 = this.keys;
      int[] var3 = this.values;
      CrudeIncrementalIntIdentityHashBiMap var4 = new CrudeIncrementalIntIdentityHashBiMap(var1);

      for (int var5 = 0; var5 < var2.length; var5++) {
         if (var2[var5] != null) {
            var4.addMapping(var2[var5], var3[var5]);
         }
      }

      this.keys = var4.keys;
      this.values = var4.values;
      this.byId = var4.byId;
      this.nextId = var4.nextId;
      this.size = var4.size;
   }

   public void addMapping(K var1, int var2) {
      int var3 = Math.max(var2, this.size + 1);
      if ((float)var3 >= (float)this.keys.length * 0.8F) {
         int var4 = this.keys.length << 1;

         while (var4 < var2) {
            var4 <<= 1;
         }

         this.grow(var4);
      }

      int var5 = this.findEmpty(this.hash((K)var1));
      this.keys[var5] = (K)var1;
      this.values[var5] = var2;
      this.byId[var2] = (K)var1;
      this.size++;
      if (var2 == this.nextId) {
         this.nextId++;
      }
   }

   private int hash(@Nullable K var1) {
      return (Mth.murmurHash3Mixer(System.identityHashCode(var1)) & 2147483647) % this.keys.length;
   }

   private int indexOf(@Nullable K var1, int var2) {
      for (int var3 = var2; var3 < this.keys.length; var3++) {
         if (this.keys[var3] == var1) {
            return var3;
         }

         if (this.keys[var3] == EMPTY_SLOT) {
            return -1;
         }
      }

      for (int var4 = 0; var4 < var2; var4++) {
         if (this.keys[var4] == var1) {
            return var4;
         }

         if (this.keys[var4] == EMPTY_SLOT) {
            return -1;
         }
      }

      return -1;
   }

   private int findEmpty(int var1) {
      for (int var2 = var1; var2 < this.keys.length; var2++) {
         if (this.keys[var2] == EMPTY_SLOT) {
            return var2;
         }
      }

      for (int var3 = 0; var3 < var1; var3++) {
         if (this.keys[var3] == EMPTY_SLOT) {
            return var3;
         }
      }

      throw new RuntimeException("Overflowed :(");
   }

   @Override
   public Iterator<K> iterator() {
      return Iterators.filter(Iterators.forArray(this.byId), Predicates.notNull());
   }

   public void clear() {
      Arrays.fill(this.keys, null);
      Arrays.fill(this.byId, null);
      this.nextId = 0;
      this.size = 0;
   }

   @Override
   public int size() {
      return this.size;
   }

   public CrudeIncrementalIntIdentityHashBiMap<K> copy() {
      return new CrudeIncrementalIntIdentityHashBiMap<>(
         (K[])((Object[])this.keys.clone()), (int[])this.values.clone(), (K[])((Object[])this.byId.clone()), this.nextId, this.size
      );
   }
}
