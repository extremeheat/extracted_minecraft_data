package io.netty.buffer;

final class PoolChunk<T> implements PoolChunkMetric {
   private static final int INTEGER_SIZE_MINUS_ONE = 31;
   final PoolArena<T> arena;
   final T memory;
   final boolean unpooled;
   final int offset;
   private final byte[] memoryMap;
   private final byte[] depthMap;
   private final PoolSubpage<T>[] subpages;
   private final int subpageOverflowMask;
   private final int pageSize;
   private final int pageShifts;
   private final int maxOrder;
   private final int chunkSize;
   private final int log2ChunkSize;
   private final int maxSubpageAllocs;
   private final byte unusable;
   private int freeBytes;
   PoolChunkList<T> parent;
   PoolChunk<T> prev;
   PoolChunk<T> next;

   PoolChunk(PoolArena<T> var1, T var2, int var3, int var4, int var5, int var6, int var7) {
      super();
      this.unpooled = false;
      this.arena = var1;
      this.memory = var2;
      this.pageSize = var3;
      this.pageShifts = var5;
      this.maxOrder = var4;
      this.chunkSize = var6;
      this.offset = var7;
      this.unusable = (byte)(var4 + 1);
      this.log2ChunkSize = log2(var6);
      this.subpageOverflowMask = ~(var3 - 1);
      this.freeBytes = var6;

      assert var4 < 30 : "maxOrder should be < 30, but is: " + var4;

      this.maxSubpageAllocs = 1 << var4;
      this.memoryMap = new byte[this.maxSubpageAllocs << 1];
      this.depthMap = new byte[this.memoryMap.length];
      int var8 = 1;

      for(int var9 = 0; var9 <= var4; ++var9) {
         int var10 = 1 << var9;

         for(int var11 = 0; var11 < var10; ++var11) {
            this.memoryMap[var8] = (byte)var9;
            this.depthMap[var8] = (byte)var9;
            ++var8;
         }
      }

      this.subpages = this.newSubpageArray(this.maxSubpageAllocs);
   }

   PoolChunk(PoolArena<T> var1, T var2, int var3, int var4) {
      super();
      this.unpooled = true;
      this.arena = var1;
      this.memory = var2;
      this.offset = var4;
      this.memoryMap = null;
      this.depthMap = null;
      this.subpages = null;
      this.subpageOverflowMask = 0;
      this.pageSize = 0;
      this.pageShifts = 0;
      this.maxOrder = 0;
      this.unusable = (byte)(this.maxOrder + 1);
      this.chunkSize = var3;
      this.log2ChunkSize = log2(this.chunkSize);
      this.maxSubpageAllocs = 0;
   }

   private PoolSubpage<T>[] newSubpageArray(int var1) {
      return new PoolSubpage[var1];
   }

   public int usage() {
      int var1;
      synchronized(this.arena) {
         var1 = this.freeBytes;
      }

      return this.usage(var1);
   }

   private int usage(int var1) {
      if (var1 == 0) {
         return 100;
      } else {
         int var2 = (int)((long)var1 * 100L / (long)this.chunkSize);
         return var2 == 0 ? 99 : 100 - var2;
      }
   }

   long allocate(int var1) {
      return (var1 & this.subpageOverflowMask) != 0 ? this.allocateRun(var1) : this.allocateSubpage(var1);
   }

   private void updateParentsAlloc(int var1) {
      while(var1 > 1) {
         int var2 = var1 >>> 1;
         byte var3 = this.value(var1);
         byte var4 = this.value(var1 ^ 1);
         byte var5 = var3 < var4 ? var3 : var4;
         this.setValue(var2, var5);
         var1 = var2;
      }

   }

   private void updateParentsFree(int var1) {
      int var3;
      for(int var2 = this.depth(var1) + 1; var1 > 1; var1 = var3) {
         var3 = var1 >>> 1;
         byte var4 = this.value(var1);
         byte var5 = this.value(var1 ^ 1);
         --var2;
         if (var4 == var2 && var5 == var2) {
            this.setValue(var3, (byte)(var2 - 1));
         } else {
            byte var6 = var4 < var5 ? var4 : var5;
            this.setValue(var3, var6);
         }
      }

   }

   private int allocateNode(int var1) {
      int var2 = 1;
      int var3 = -(1 << var1);
      byte var4 = this.value(var2);
      if (var4 > var1) {
         return -1;
      } else {
         while(var4 < var1 || (var2 & var3) == 0) {
            var2 <<= 1;
            var4 = this.value(var2);
            if (var4 > var1) {
               var2 ^= 1;
               var4 = this.value(var2);
            }
         }

         byte var5 = this.value(var2);

         assert var5 == var1 && (var2 & var3) == 1 << var1 : String.format("val = %d, id & initial = %d, d = %d", var5, var2 & var3, var1);

         this.setValue(var2, this.unusable);
         this.updateParentsAlloc(var2);
         return var2;
      }
   }

   private long allocateRun(int var1) {
      int var2 = this.maxOrder - (log2(var1) - this.pageShifts);
      int var3 = this.allocateNode(var2);
      if (var3 < 0) {
         return (long)var3;
      } else {
         this.freeBytes -= this.runLength(var3);
         return (long)var3;
      }
   }

   private long allocateSubpage(int var1) {
      PoolSubpage var2 = this.arena.findSubpagePoolHead(var1);
      synchronized(var2) {
         int var4 = this.maxOrder;
         int var5 = this.allocateNode(var4);
         if (var5 < 0) {
            return (long)var5;
         } else {
            PoolSubpage[] var6 = this.subpages;
            int var7 = this.pageSize;
            this.freeBytes -= var7;
            int var8 = this.subpageIdx(var5);
            PoolSubpage var9 = var6[var8];
            if (var9 == null) {
               var9 = new PoolSubpage(var2, this, var5, this.runOffset(var5), var7, var1);
               var6[var8] = var9;
            } else {
               var9.init(var2, var1);
            }

            return var9.allocate();
         }
      }
   }

   void free(long var1) {
      int var3 = memoryMapIdx(var1);
      int var4 = bitmapIdx(var1);
      if (var4 != 0) {
         PoolSubpage var5 = this.subpages[this.subpageIdx(var3)];

         assert var5 != null && var5.doNotDestroy;

         PoolSubpage var6 = this.arena.findSubpagePoolHead(var5.elemSize);
         synchronized(var6) {
            if (var5.free(var6, var4 & 1073741823)) {
               return;
            }
         }
      }

      this.freeBytes += this.runLength(var3);
      this.setValue(var3, this.depth(var3));
      this.updateParentsFree(var3);
   }

   void initBuf(PooledByteBuf<T> var1, long var2, int var4) {
      int var5 = memoryMapIdx(var2);
      int var6 = bitmapIdx(var2);
      if (var6 == 0) {
         byte var7 = this.value(var5);

         assert var7 == this.unusable : String.valueOf(var7);

         var1.init(this, var2, this.runOffset(var5) + this.offset, var4, this.runLength(var5), this.arena.parent.threadCache());
      } else {
         this.initBufWithSubpage(var1, var2, var6, var4);
      }

   }

   void initBufWithSubpage(PooledByteBuf<T> var1, long var2, int var4) {
      this.initBufWithSubpage(var1, var2, bitmapIdx(var2), var4);
   }

   private void initBufWithSubpage(PooledByteBuf<T> var1, long var2, int var4, int var5) {
      assert var4 != 0;

      int var6 = memoryMapIdx(var2);
      PoolSubpage var7 = this.subpages[this.subpageIdx(var6)];

      assert var7.doNotDestroy;

      assert var5 <= var7.elemSize;

      var1.init(this, var2, this.runOffset(var6) + (var4 & 1073741823) * var7.elemSize + this.offset, var5, var7.elemSize, this.arena.parent.threadCache());
   }

   private byte value(int var1) {
      return this.memoryMap[var1];
   }

   private void setValue(int var1, byte var2) {
      this.memoryMap[var1] = var2;
   }

   private byte depth(int var1) {
      return this.depthMap[var1];
   }

   private static int log2(int var0) {
      return 31 - Integer.numberOfLeadingZeros(var0);
   }

   private int runLength(int var1) {
      return 1 << this.log2ChunkSize - this.depth(var1);
   }

   private int runOffset(int var1) {
      int var2 = var1 ^ 1 << this.depth(var1);
      return var2 * this.runLength(var1);
   }

   private int subpageIdx(int var1) {
      return var1 ^ this.maxSubpageAllocs;
   }

   private static int memoryMapIdx(long var0) {
      return (int)var0;
   }

   private static int bitmapIdx(long var0) {
      return (int)(var0 >>> 32);
   }

   public int chunkSize() {
      return this.chunkSize;
   }

   public int freeBytes() {
      synchronized(this.arena) {
         return this.freeBytes;
      }
   }

   public String toString() {
      int var1;
      synchronized(this.arena) {
         var1 = this.freeBytes;
      }

      return "Chunk(" + Integer.toHexString(System.identityHashCode(this)) + ": " + this.usage(var1) + "%, " + (this.chunkSize - var1) + '/' + this.chunkSize + ')';
   }

   void destroy() {
      this.arena.destroyChunk(this);
   }
}
