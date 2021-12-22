package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.util.NoSuchElementException;
import net.minecraft.util.Mth;

public class SpatialLongSet extends LongLinkedOpenHashSet {
   private final SpatialLongSet.InternalMap map;

   public SpatialLongSet(int var1, float var2) {
      super(var1, var2);
      this.map = new SpatialLongSet.InternalMap(var1 / 64, var2);
   }

   public boolean add(long var1) {
      return this.map.addBit(var1);
   }

   public boolean rem(long var1) {
      return this.map.removeBit(var1);
   }

   public long removeFirstLong() {
      return this.map.removeFirstBit();
   }

   public int size() {
      throw new UnsupportedOperationException();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   protected static class InternalMap extends Long2LongLinkedOpenHashMap {
      private static final int X_BITS = Mth.log2(60000000);
      private static final int Z_BITS = Mth.log2(60000000);
      private static final int Y_BITS;
      private static final int Y_OFFSET = 0;
      private static final int Z_OFFSET;
      private static final int X_OFFSET;
      private static final long OUTER_MASK;
      private int lastPos = -1;
      private long lastOuterKey;
      private final int minSize;

      public InternalMap(int var1, float var2) {
         super(var1, var2);
         this.minSize = var1;
      }

      static long getOuterKey(long var0) {
         return var0 & ~OUTER_MASK;
      }

      static int getInnerKey(long var0) {
         int var2 = (int)(var0 >>> X_OFFSET & 3L);
         int var3 = (int)(var0 >>> 0 & 3L);
         int var4 = (int)(var0 >>> Z_OFFSET & 3L);
         return var2 << 4 | var4 << 2 | var3;
      }

      static long getFullKey(long var0, int var2) {
         var0 |= (long)(var2 >>> 4 & 3) << X_OFFSET;
         var0 |= (long)(var2 >>> 2 & 3) << Z_OFFSET;
         var0 |= (long)(var2 >>> 0 & 3) << 0;
         return var0;
      }

      public boolean addBit(long var1) {
         long var3 = getOuterKey(var1);
         int var5 = getInnerKey(var1);
         long var6 = 1L << var5;
         int var8;
         if (var3 == 0L) {
            if (this.containsNullKey) {
               return this.replaceBit(this.n, var6);
            }

            this.containsNullKey = true;
            var8 = this.n;
         } else {
            if (this.lastPos != -1 && var3 == this.lastOuterKey) {
               return this.replaceBit(this.lastPos, var6);
            }

            long[] var9 = this.key;
            var8 = (int)HashCommon.mix(var3) & this.mask;

            for(long var10 = var9[var8]; var10 != 0L; var10 = var9[var8]) {
               if (var10 == var3) {
                  this.lastPos = var8;
                  this.lastOuterKey = var3;
                  return this.replaceBit(var8, var6);
               }

               var8 = var8 + 1 & this.mask;
            }
         }

         this.key[var8] = var3;
         this.value[var8] = var6;
         if (this.size == 0) {
            this.first = this.last = var8;
            this.link[var8] = -1L;
         } else {
            long[] var10000 = this.link;
            int var10001 = this.last;
            var10000[var10001] ^= (this.link[this.last] ^ (long)var8 & 4294967295L) & 4294967295L;
            this.link[var8] = ((long)this.last & 4294967295L) << 32 | 4294967295L;
            this.last = var8;
         }

         if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
         }

         return false;
      }

      private boolean replaceBit(int var1, long var2) {
         boolean var4 = (this.value[var1] & var2) != 0L;
         long[] var10000 = this.value;
         var10000[var1] |= var2;
         return var4;
      }

      public boolean removeBit(long var1) {
         long var3 = getOuterKey(var1);
         int var5 = getInnerKey(var1);
         long var6 = 1L << var5;
         if (var3 == 0L) {
            return this.containsNullKey ? this.removeFromNullEntry(var6) : false;
         } else if (this.lastPos != -1 && var3 == this.lastOuterKey) {
            return this.removeFromEntry(this.lastPos, var6);
         } else {
            long[] var8 = this.key;
            int var9 = (int)HashCommon.mix(var3) & this.mask;

            for(long var10 = var8[var9]; var10 != 0L; var10 = var8[var9]) {
               if (var3 == var10) {
                  this.lastPos = var9;
                  this.lastOuterKey = var3;
                  return this.removeFromEntry(var9, var6);
               }

               var9 = var9 + 1 & this.mask;
            }

            return false;
         }
      }

      private boolean removeFromNullEntry(long var1) {
         if ((this.value[this.n] & var1) == 0L) {
            return false;
         } else {
            long[] var10000 = this.value;
            int var10001 = this.n;
            var10000[var10001] &= ~var1;
            if (this.value[this.n] != 0L) {
               return true;
            } else {
               this.containsNullKey = false;
               --this.size;
               this.fixPointers(this.n);
               if (this.size < this.maxFill / 4 && this.n > 16) {
                  this.rehash(this.n / 2);
               }

               return true;
            }
         }
      }

      private boolean removeFromEntry(int var1, long var2) {
         if ((this.value[var1] & var2) == 0L) {
            return false;
         } else {
            long[] var10000 = this.value;
            var10000[var1] &= ~var2;
            if (this.value[var1] != 0L) {
               return true;
            } else {
               this.lastPos = -1;
               --this.size;
               this.fixPointers(var1);
               this.shiftKeys(var1);
               if (this.size < this.maxFill / 4 && this.n > 16) {
                  this.rehash(this.n / 2);
               }

               return true;
            }
         }
      }

      public long removeFirstBit() {
         if (this.size == 0) {
            throw new NoSuchElementException();
         } else {
            int var1 = this.first;
            long var2 = this.key[var1];
            int var4 = Long.numberOfTrailingZeros(this.value[var1]);
            long[] var10000 = this.value;
            var10000[var1] &= ~(1L << var4);
            if (this.value[var1] == 0L) {
               this.removeFirstLong();
               this.lastPos = -1;
            }

            return getFullKey(var2, var4);
         }
      }

      protected void rehash(int var1) {
         if (var1 > this.minSize) {
            super.rehash(var1);
         }

      }

      static {
         Y_BITS = 64 - X_BITS - Z_BITS;
         Z_OFFSET = Y_BITS;
         X_OFFSET = Y_BITS + Z_BITS;
         OUTER_MASK = 3L << X_OFFSET | 3L | 3L << Z_OFFSET;
      }
   }
}
