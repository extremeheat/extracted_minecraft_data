package io.netty.buffer;

import io.netty.util.internal.LongCounter;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

abstract class PoolArena<T> implements PoolArenaMetric {
   static final boolean HAS_UNSAFE = PlatformDependent.hasUnsafe();
   static final int numTinySubpagePools = 32;
   final PooledByteBufAllocator parent;
   private final int maxOrder;
   final int pageSize;
   final int pageShifts;
   final int chunkSize;
   final int subpageOverflowMask;
   final int numSmallSubpagePools;
   final int directMemoryCacheAlignment;
   final int directMemoryCacheAlignmentMask;
   private final PoolSubpage<T>[] tinySubpagePools;
   private final PoolSubpage<T>[] smallSubpagePools;
   private final PoolChunkList<T> q050;
   private final PoolChunkList<T> q025;
   private final PoolChunkList<T> q000;
   private final PoolChunkList<T> qInit;
   private final PoolChunkList<T> q075;
   private final PoolChunkList<T> q100;
   private final List<PoolChunkListMetric> chunkListMetrics;
   private long allocationsNormal;
   private final LongCounter allocationsTiny = PlatformDependent.newLongCounter();
   private final LongCounter allocationsSmall = PlatformDependent.newLongCounter();
   private final LongCounter allocationsHuge = PlatformDependent.newLongCounter();
   private final LongCounter activeBytesHuge = PlatformDependent.newLongCounter();
   private long deallocationsTiny;
   private long deallocationsSmall;
   private long deallocationsNormal;
   private final LongCounter deallocationsHuge = PlatformDependent.newLongCounter();
   final AtomicInteger numThreadCaches = new AtomicInteger();

   protected PoolArena(PooledByteBufAllocator var1, int var2, int var3, int var4, int var5, int var6) {
      super();
      this.parent = var1;
      this.pageSize = var2;
      this.maxOrder = var3;
      this.pageShifts = var4;
      this.chunkSize = var5;
      this.directMemoryCacheAlignment = var6;
      this.directMemoryCacheAlignmentMask = var6 - 1;
      this.subpageOverflowMask = ~(var2 - 1);
      this.tinySubpagePools = this.newSubpagePoolArray(32);

      int var7;
      for(var7 = 0; var7 < this.tinySubpagePools.length; ++var7) {
         this.tinySubpagePools[var7] = this.newSubpagePoolHead(var2);
      }

      this.numSmallSubpagePools = var4 - 9;
      this.smallSubpagePools = this.newSubpagePoolArray(this.numSmallSubpagePools);

      for(var7 = 0; var7 < this.smallSubpagePools.length; ++var7) {
         this.smallSubpagePools[var7] = this.newSubpagePoolHead(var2);
      }

      this.q100 = new PoolChunkList(this, (PoolChunkList)null, 100, 2147483647, var5);
      this.q075 = new PoolChunkList(this, this.q100, 75, 100, var5);
      this.q050 = new PoolChunkList(this, this.q075, 50, 100, var5);
      this.q025 = new PoolChunkList(this, this.q050, 25, 75, var5);
      this.q000 = new PoolChunkList(this, this.q025, 1, 50, var5);
      this.qInit = new PoolChunkList(this, this.q000, -2147483648, 25, var5);
      this.q100.prevList(this.q075);
      this.q075.prevList(this.q050);
      this.q050.prevList(this.q025);
      this.q025.prevList(this.q000);
      this.q000.prevList((PoolChunkList)null);
      this.qInit.prevList(this.qInit);
      ArrayList var8 = new ArrayList(6);
      var8.add(this.qInit);
      var8.add(this.q000);
      var8.add(this.q025);
      var8.add(this.q050);
      var8.add(this.q075);
      var8.add(this.q100);
      this.chunkListMetrics = Collections.unmodifiableList(var8);
   }

   private PoolSubpage<T> newSubpagePoolHead(int var1) {
      PoolSubpage var2 = new PoolSubpage(var1);
      var2.prev = var2;
      var2.next = var2;
      return var2;
   }

   private PoolSubpage<T>[] newSubpagePoolArray(int var1) {
      return new PoolSubpage[var1];
   }

   abstract boolean isDirect();

   PooledByteBuf<T> allocate(PoolThreadCache var1, int var2, int var3) {
      PooledByteBuf var4 = this.newByteBuf(var3);
      this.allocate(var1, var4, var2);
      return var4;
   }

   static int tinyIdx(int var0) {
      return var0 >>> 4;
   }

   static int smallIdx(int var0) {
      int var1 = 0;

      for(int var2 = var0 >>> 10; var2 != 0; ++var1) {
         var2 >>>= 1;
      }

      return var1;
   }

   boolean isTinyOrSmall(int var1) {
      return (var1 & this.subpageOverflowMask) == 0;
   }

   static boolean isTiny(int var0) {
      return (var0 & -512) == 0;
   }

   private void allocate(PoolThreadCache var1, PooledByteBuf<T> var2, int var3) {
      int var4 = this.normalizeCapacity(var3);
      if (this.isTinyOrSmall(var4)) {
         boolean var7 = isTiny(var4);
         int var5;
         PoolSubpage[] var6;
         if (var7) {
            if (var1.allocateTiny(this, var2, var3, var4)) {
               return;
            }

            var5 = tinyIdx(var4);
            var6 = this.tinySubpagePools;
         } else {
            if (var1.allocateSmall(this, var2, var3, var4)) {
               return;
            }

            var5 = smallIdx(var4);
            var6 = this.smallSubpagePools;
         }

         PoolSubpage var8 = var6[var5];
         synchronized(var8) {
            PoolSubpage var10 = var8.next;
            if (var10 != var8) {
               assert var10.doNotDestroy && var10.elemSize == var4;

               long var11 = var10.allocate();

               assert var11 >= 0L;

               var10.chunk.initBufWithSubpage(var2, var11, var3);
               this.incTinySmallAllocation(var7);
               return;
            }
         }

         synchronized(this) {
            this.allocateNormal(var2, var3, var4);
         }

         this.incTinySmallAllocation(var7);
      } else {
         if (var4 <= this.chunkSize) {
            if (var1.allocateNormal(this, var2, var3, var4)) {
               return;
            }

            synchronized(this) {
               this.allocateNormal(var2, var3, var4);
               ++this.allocationsNormal;
            }
         } else {
            this.allocateHuge(var2, var3);
         }

      }
   }

   private void allocateNormal(PooledByteBuf<T> var1, int var2, int var3) {
      if (!this.q050.allocate(var1, var2, var3) && !this.q025.allocate(var1, var2, var3) && !this.q000.allocate(var1, var2, var3) && !this.qInit.allocate(var1, var2, var3) && !this.q075.allocate(var1, var2, var3)) {
         PoolChunk var4 = this.newChunk(this.pageSize, this.maxOrder, this.pageShifts, this.chunkSize);
         long var5 = var4.allocate(var3);

         assert var5 > 0L;

         var4.initBuf(var1, var5, var2);
         this.qInit.add(var4);
      }
   }

   private void incTinySmallAllocation(boolean var1) {
      if (var1) {
         this.allocationsTiny.increment();
      } else {
         this.allocationsSmall.increment();
      }

   }

   private void allocateHuge(PooledByteBuf<T> var1, int var2) {
      PoolChunk var3 = this.newUnpooledChunk(var2);
      this.activeBytesHuge.add((long)var3.chunkSize());
      var1.initUnpooled(var3, var2);
      this.allocationsHuge.increment();
   }

   void free(PoolChunk<T> var1, long var2, int var4, PoolThreadCache var5) {
      if (var1.unpooled) {
         int var6 = var1.chunkSize();
         this.destroyChunk(var1);
         this.activeBytesHuge.add((long)(-var6));
         this.deallocationsHuge.increment();
      } else {
         PoolArena.SizeClass var7 = this.sizeClass(var4);
         if (var5 != null && var5.add(this, var1, var2, var4, var7)) {
            return;
         }

         this.freeChunk(var1, var2, var7);
      }

   }

   private PoolArena.SizeClass sizeClass(int var1) {
      if (!this.isTinyOrSmall(var1)) {
         return PoolArena.SizeClass.Normal;
      } else {
         return isTiny(var1) ? PoolArena.SizeClass.Tiny : PoolArena.SizeClass.Small;
      }
   }

   void freeChunk(PoolChunk<T> var1, long var2, PoolArena.SizeClass var4) {
      boolean var5;
      synchronized(this) {
         switch(var4) {
         case Normal:
            ++this.deallocationsNormal;
            break;
         case Small:
            ++this.deallocationsSmall;
            break;
         case Tiny:
            ++this.deallocationsTiny;
            break;
         default:
            throw new Error();
         }

         var5 = !var1.parent.free(var1, var2);
      }

      if (var5) {
         this.destroyChunk(var1);
      }

   }

   PoolSubpage<T> findSubpagePoolHead(int var1) {
      int var2;
      PoolSubpage[] var3;
      if (isTiny(var1)) {
         var2 = var1 >>> 4;
         var3 = this.tinySubpagePools;
      } else {
         var2 = 0;

         for(var1 >>>= 10; var1 != 0; ++var2) {
            var1 >>>= 1;
         }

         var3 = this.smallSubpagePools;
      }

      return var3[var2];
   }

   int normalizeCapacity(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("capacity: " + var1 + " (expected: 0+)");
      } else if (var1 >= this.chunkSize) {
         return this.directMemoryCacheAlignment == 0 ? var1 : this.alignCapacity(var1);
      } else if (!isTiny(var1)) {
         int var2 = var1 - 1;
         var2 |= var2 >>> 1;
         var2 |= var2 >>> 2;
         var2 |= var2 >>> 4;
         var2 |= var2 >>> 8;
         var2 |= var2 >>> 16;
         ++var2;
         if (var2 < 0) {
            var2 >>>= 1;
         }

         assert this.directMemoryCacheAlignment == 0 || (var2 & this.directMemoryCacheAlignmentMask) == 0;

         return var2;
      } else if (this.directMemoryCacheAlignment > 0) {
         return this.alignCapacity(var1);
      } else {
         return (var1 & 15) == 0 ? var1 : (var1 & -16) + 16;
      }
   }

   int alignCapacity(int var1) {
      int var2 = var1 & this.directMemoryCacheAlignmentMask;
      return var2 == 0 ? var1 : var1 + this.directMemoryCacheAlignment - var2;
   }

   void reallocate(PooledByteBuf<T> var1, int var2, boolean var3) {
      if (var2 >= 0 && var2 <= var1.maxCapacity()) {
         int var4 = var1.length;
         if (var4 != var2) {
            PoolChunk var5 = var1.chunk;
            long var6 = var1.handle;
            Object var8 = var1.memory;
            int var9 = var1.offset;
            int var10 = var1.maxLength;
            int var11 = var1.readerIndex();
            int var12 = var1.writerIndex();
            this.allocate(this.parent.threadCache(), var1, var2);
            if (var2 > var4) {
               this.memoryCopy(var8, var9, var1.memory, var1.offset, var4);
            } else if (var2 < var4) {
               if (var11 < var2) {
                  if (var12 > var2) {
                     var12 = var2;
                  }

                  this.memoryCopy(var8, var9 + var11, var1.memory, var1.offset + var11, var12 - var11);
               } else {
                  var12 = var2;
                  var11 = var2;
               }
            }

            var1.setIndex(var11, var12);
            if (var3) {
               this.free(var5, var6, var10, var1.cache);
            }

         }
      } else {
         throw new IllegalArgumentException("newCapacity: " + var2);
      }
   }

   public int numThreadCaches() {
      return this.numThreadCaches.get();
   }

   public int numTinySubpages() {
      return this.tinySubpagePools.length;
   }

   public int numSmallSubpages() {
      return this.smallSubpagePools.length;
   }

   public int numChunkLists() {
      return this.chunkListMetrics.size();
   }

   public List<PoolSubpageMetric> tinySubpages() {
      return subPageMetricList(this.tinySubpagePools);
   }

   public List<PoolSubpageMetric> smallSubpages() {
      return subPageMetricList(this.smallSubpagePools);
   }

   public List<PoolChunkListMetric> chunkLists() {
      return this.chunkListMetrics;
   }

   private static List<PoolSubpageMetric> subPageMetricList(PoolSubpage<?>[] var0) {
      ArrayList var1 = new ArrayList();
      PoolSubpage[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         PoolSubpage var5 = var2[var4];
         if (var5.next != var5) {
            PoolSubpage var6 = var5.next;

            do {
               var1.add(var6);
               var6 = var6.next;
            } while(var6 != var5);
         }
      }

      return var1;
   }

   public long numAllocations() {
      long var1;
      synchronized(this) {
         var1 = this.allocationsNormal;
      }

      return this.allocationsTiny.value() + this.allocationsSmall.value() + var1 + this.allocationsHuge.value();
   }

   public long numTinyAllocations() {
      return this.allocationsTiny.value();
   }

   public long numSmallAllocations() {
      return this.allocationsSmall.value();
   }

   public synchronized long numNormalAllocations() {
      return this.allocationsNormal;
   }

   public long numDeallocations() {
      long var1;
      synchronized(this) {
         var1 = this.deallocationsTiny + this.deallocationsSmall + this.deallocationsNormal;
      }

      return var1 + this.deallocationsHuge.value();
   }

   public synchronized long numTinyDeallocations() {
      return this.deallocationsTiny;
   }

   public synchronized long numSmallDeallocations() {
      return this.deallocationsSmall;
   }

   public synchronized long numNormalDeallocations() {
      return this.deallocationsNormal;
   }

   public long numHugeAllocations() {
      return this.allocationsHuge.value();
   }

   public long numHugeDeallocations() {
      return this.deallocationsHuge.value();
   }

   public long numActiveAllocations() {
      long var1 = this.allocationsTiny.value() + this.allocationsSmall.value() + this.allocationsHuge.value() - this.deallocationsHuge.value();
      synchronized(this) {
         var1 += this.allocationsNormal - (this.deallocationsTiny + this.deallocationsSmall + this.deallocationsNormal);
      }

      return Math.max(var1, 0L);
   }

   public long numActiveTinyAllocations() {
      return Math.max(this.numTinyAllocations() - this.numTinyDeallocations(), 0L);
   }

   public long numActiveSmallAllocations() {
      return Math.max(this.numSmallAllocations() - this.numSmallDeallocations(), 0L);
   }

   public long numActiveNormalAllocations() {
      long var1;
      synchronized(this) {
         var1 = this.allocationsNormal - this.deallocationsNormal;
      }

      return Math.max(var1, 0L);
   }

   public long numActiveHugeAllocations() {
      return Math.max(this.numHugeAllocations() - this.numHugeDeallocations(), 0L);
   }

   public long numActiveBytes() {
      long var1 = this.activeBytesHuge.value();
      synchronized(this) {
         PoolChunkMetric var6;
         for(int var4 = 0; var4 < this.chunkListMetrics.size(); ++var4) {
            for(Iterator var5 = ((PoolChunkListMetric)this.chunkListMetrics.get(var4)).iterator(); var5.hasNext(); var1 += (long)var6.chunkSize()) {
               var6 = (PoolChunkMetric)var5.next();
            }
         }

         return Math.max(0L, var1);
      }
   }

   protected abstract PoolChunk<T> newChunk(int var1, int var2, int var3, int var4);

   protected abstract PoolChunk<T> newUnpooledChunk(int var1);

   protected abstract PooledByteBuf<T> newByteBuf(int var1);

   protected abstract void memoryCopy(T var1, int var2, T var3, int var4, int var5);

   protected abstract void destroyChunk(PoolChunk<T> var1);

   public synchronized String toString() {
      StringBuilder var1 = (new StringBuilder()).append("Chunk(s) at 0~25%:").append(StringUtil.NEWLINE).append(this.qInit).append(StringUtil.NEWLINE).append("Chunk(s) at 0~50%:").append(StringUtil.NEWLINE).append(this.q000).append(StringUtil.NEWLINE).append("Chunk(s) at 25~75%:").append(StringUtil.NEWLINE).append(this.q025).append(StringUtil.NEWLINE).append("Chunk(s) at 50~100%:").append(StringUtil.NEWLINE).append(this.q050).append(StringUtil.NEWLINE).append("Chunk(s) at 75~100%:").append(StringUtil.NEWLINE).append(this.q075).append(StringUtil.NEWLINE).append("Chunk(s) at 100%:").append(StringUtil.NEWLINE).append(this.q100).append(StringUtil.NEWLINE).append("tiny subpages:");
      appendPoolSubPages(var1, this.tinySubpagePools);
      var1.append(StringUtil.NEWLINE).append("small subpages:");
      appendPoolSubPages(var1, this.smallSubpagePools);
      var1.append(StringUtil.NEWLINE);
      return var1.toString();
   }

   private static void appendPoolSubPages(StringBuilder var0, PoolSubpage<?>[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         PoolSubpage var3 = var1[var2];
         if (var3.next != var3) {
            var0.append(StringUtil.NEWLINE).append(var2).append(": ");
            PoolSubpage var4 = var3.next;

            do {
               var0.append(var4);
               var4 = var4.next;
            } while(var4 != var3);
         }
      }

   }

   protected final void finalize() throws Throwable {
      try {
         super.finalize();
      } finally {
         destroyPoolSubPages(this.smallSubpagePools);
         destroyPoolSubPages(this.tinySubpagePools);
         this.destroyPoolChunkLists(this.qInit, this.q000, this.q025, this.q050, this.q075, this.q100);
      }

   }

   private static void destroyPoolSubPages(PoolSubpage<?>[] var0) {
      PoolSubpage[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         PoolSubpage var4 = var1[var3];
         var4.destroy();
      }

   }

   private void destroyPoolChunkLists(PoolChunkList<T>... var1) {
      PoolChunkList[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         PoolChunkList var5 = var2[var4];
         var5.destroy(this);
      }

   }

   static final class DirectArena extends PoolArena<ByteBuffer> {
      DirectArena(PooledByteBufAllocator var1, int var2, int var3, int var4, int var5, int var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      boolean isDirect() {
         return true;
      }

      private int offsetCacheLine(ByteBuffer var1) {
         return HAS_UNSAFE ? (int)(PlatformDependent.directBufferAddress(var1) & (long)this.directMemoryCacheAlignmentMask) : 0;
      }

      protected PoolChunk<ByteBuffer> newChunk(int var1, int var2, int var3, int var4) {
         if (this.directMemoryCacheAlignment == 0) {
            return new PoolChunk(this, allocateDirect(var4), var1, var2, var3, var4, 0);
         } else {
            ByteBuffer var5 = allocateDirect(var4 + this.directMemoryCacheAlignment);
            return new PoolChunk(this, var5, var1, var2, var3, var4, this.offsetCacheLine(var5));
         }
      }

      protected PoolChunk<ByteBuffer> newUnpooledChunk(int var1) {
         if (this.directMemoryCacheAlignment == 0) {
            return new PoolChunk(this, allocateDirect(var1), var1, 0);
         } else {
            ByteBuffer var2 = allocateDirect(var1 + this.directMemoryCacheAlignment);
            return new PoolChunk(this, var2, var1, this.offsetCacheLine(var2));
         }
      }

      private static ByteBuffer allocateDirect(int var0) {
         return PlatformDependent.useDirectBufferNoCleaner() ? PlatformDependent.allocateDirectNoCleaner(var0) : ByteBuffer.allocateDirect(var0);
      }

      protected void destroyChunk(PoolChunk<ByteBuffer> var1) {
         if (PlatformDependent.useDirectBufferNoCleaner()) {
            PlatformDependent.freeDirectNoCleaner((ByteBuffer)var1.memory);
         } else {
            PlatformDependent.freeDirectBuffer((ByteBuffer)var1.memory);
         }

      }

      protected PooledByteBuf<ByteBuffer> newByteBuf(int var1) {
         return (PooledByteBuf)(HAS_UNSAFE ? PooledUnsafeDirectByteBuf.newInstance(var1) : PooledDirectByteBuf.newInstance(var1));
      }

      protected void memoryCopy(ByteBuffer var1, int var2, ByteBuffer var3, int var4, int var5) {
         if (var5 != 0) {
            if (HAS_UNSAFE) {
               PlatformDependent.copyMemory(PlatformDependent.directBufferAddress(var1) + (long)var2, PlatformDependent.directBufferAddress(var3) + (long)var4, (long)var5);
            } else {
               var1 = var1.duplicate();
               var3 = var3.duplicate();
               var1.position(var2).limit(var2 + var5);
               var3.position(var4);
               var3.put(var1);
            }

         }
      }
   }

   static final class HeapArena extends PoolArena<byte[]> {
      HeapArena(PooledByteBufAllocator var1, int var2, int var3, int var4, int var5, int var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      private static byte[] newByteArray(int var0) {
         return PlatformDependent.allocateUninitializedArray(var0);
      }

      boolean isDirect() {
         return false;
      }

      protected PoolChunk<byte[]> newChunk(int var1, int var2, int var3, int var4) {
         return new PoolChunk(this, newByteArray(var4), var1, var2, var3, var4, 0);
      }

      protected PoolChunk<byte[]> newUnpooledChunk(int var1) {
         return new PoolChunk(this, newByteArray(var1), var1, 0);
      }

      protected void destroyChunk(PoolChunk<byte[]> var1) {
      }

      protected PooledByteBuf<byte[]> newByteBuf(int var1) {
         return (PooledByteBuf)(HAS_UNSAFE ? PooledUnsafeHeapByteBuf.newUnsafeInstance(var1) : PooledHeapByteBuf.newInstance(var1));
      }

      protected void memoryCopy(byte[] var1, int var2, byte[] var3, int var4, int var5) {
         if (var5 != 0) {
            System.arraycopy(var1, var2, var3, var4, var5);
         }
      }
   }

   static enum SizeClass {
      Tiny,
      Small,
      Normal;

      private SizeClass() {
      }
   }
}
