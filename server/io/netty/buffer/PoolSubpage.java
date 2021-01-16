package io.netty.buffer;

final class PoolSubpage<T> implements PoolSubpageMetric {
   final PoolChunk<T> chunk;
   private final int memoryMapIdx;
   private final int runOffset;
   private final int pageSize;
   private final long[] bitmap;
   PoolSubpage<T> prev;
   PoolSubpage<T> next;
   boolean doNotDestroy;
   int elemSize;
   private int maxNumElems;
   private int bitmapLength;
   private int nextAvail;
   private int numAvail;

   PoolSubpage(int var1) {
      super();
      this.chunk = null;
      this.memoryMapIdx = -1;
      this.runOffset = -1;
      this.elemSize = -1;
      this.pageSize = var1;
      this.bitmap = null;
   }

   PoolSubpage(PoolSubpage<T> var1, PoolChunk<T> var2, int var3, int var4, int var5, int var6) {
      super();
      this.chunk = var2;
      this.memoryMapIdx = var3;
      this.runOffset = var4;
      this.pageSize = var5;
      this.bitmap = new long[var5 >>> 10];
      this.init(var1, var6);
   }

   void init(PoolSubpage<T> var1, int var2) {
      this.doNotDestroy = true;
      this.elemSize = var2;
      if (var2 != 0) {
         this.maxNumElems = this.numAvail = this.pageSize / var2;
         this.nextAvail = 0;
         this.bitmapLength = this.maxNumElems >>> 6;
         if ((this.maxNumElems & 63) != 0) {
            ++this.bitmapLength;
         }

         for(int var3 = 0; var3 < this.bitmapLength; ++var3) {
            this.bitmap[var3] = 0L;
         }
      }

      this.addToPool(var1);
   }

   long allocate() {
      if (this.elemSize == 0) {
         return this.toHandle(0);
      } else if (this.numAvail != 0 && this.doNotDestroy) {
         int var1 = this.getNextAvail();
         int var2 = var1 >>> 6;
         int var3 = var1 & 63;

         assert (this.bitmap[var2] >>> var3 & 1L) == 0L;

         long[] var10000 = this.bitmap;
         var10000[var2] |= 1L << var3;
         if (--this.numAvail == 0) {
            this.removeFromPool();
         }

         return this.toHandle(var1);
      } else {
         return -1L;
      }
   }

   boolean free(PoolSubpage<T> var1, int var2) {
      if (this.elemSize == 0) {
         return true;
      } else {
         int var3 = var2 >>> 6;
         int var4 = var2 & 63;

         assert (this.bitmap[var3] >>> var4 & 1L) != 0L;

         long[] var10000 = this.bitmap;
         var10000[var3] ^= 1L << var4;
         this.setNextAvail(var2);
         if (this.numAvail++ == 0) {
            this.addToPool(var1);
            return true;
         } else if (this.numAvail != this.maxNumElems) {
            return true;
         } else if (this.prev == this.next) {
            return true;
         } else {
            this.doNotDestroy = false;
            this.removeFromPool();
            return false;
         }
      }
   }

   private void addToPool(PoolSubpage<T> var1) {
      assert this.prev == null && this.next == null;

      this.prev = var1;
      this.next = var1.next;
      this.next.prev = this;
      var1.next = this;
   }

   private void removeFromPool() {
      assert this.prev != null && this.next != null;

      this.prev.next = this.next;
      this.next.prev = this.prev;
      this.next = null;
      this.prev = null;
   }

   private void setNextAvail(int var1) {
      this.nextAvail = var1;
   }

   private int getNextAvail() {
      int var1 = this.nextAvail;
      if (var1 >= 0) {
         this.nextAvail = -1;
         return var1;
      } else {
         return this.findNextAvail();
      }
   }

   private int findNextAvail() {
      long[] var1 = this.bitmap;
      int var2 = this.bitmapLength;

      for(int var3 = 0; var3 < var2; ++var3) {
         long var4 = var1[var3];
         if (~var4 != 0L) {
            return this.findNextAvail0(var3, var4);
         }
      }

      return -1;
   }

   private int findNextAvail0(int var1, long var2) {
      int var4 = this.maxNumElems;
      int var5 = var1 << 6;

      for(int var6 = 0; var6 < 64; ++var6) {
         if ((var2 & 1L) == 0L) {
            int var7 = var5 | var6;
            if (var7 < var4) {
               return var7;
            }
            break;
         }

         var2 >>>= 1;
      }

      return -1;
   }

   private long toHandle(int var1) {
      return 4611686018427387904L | (long)var1 << 32 | (long)this.memoryMapIdx;
   }

   public String toString() {
      boolean var1;
      int var2;
      int var3;
      int var4;
      synchronized(this.chunk.arena) {
         if (!this.doNotDestroy) {
            var1 = false;
            var4 = -1;
            var3 = -1;
            var2 = -1;
         } else {
            var1 = true;
            var2 = this.maxNumElems;
            var3 = this.numAvail;
            var4 = this.elemSize;
         }
      }

      return !var1 ? "(" + this.memoryMapIdx + ": not in use)" : "(" + this.memoryMapIdx + ": " + (var2 - var3) + '/' + var2 + ", offset: " + this.runOffset + ", length: " + this.pageSize + ", elemSize: " + var4 + ')';
   }

   public int maxNumElements() {
      synchronized(this.chunk.arena) {
         return this.maxNumElems;
      }
   }

   public int numAvailable() {
      synchronized(this.chunk.arena) {
         return this.numAvail;
      }
   }

   public int elementSize() {
      synchronized(this.chunk.arena) {
         return this.elemSize;
      }
   }

   public int pageSize() {
      return this.pageSize;
   }

   void destroy() {
      if (this.chunk != null) {
         this.chunk.destroy();
      }

   }
}
