package io.netty.buffer;

import io.netty.util.internal.StringUtil;
import java.util.List;

public final class PooledByteBufAllocatorMetric implements ByteBufAllocatorMetric {
   private final PooledByteBufAllocator allocator;

   PooledByteBufAllocatorMetric(PooledByteBufAllocator var1) {
      super();
      this.allocator = var1;
   }

   public int numHeapArenas() {
      return this.allocator.numHeapArenas();
   }

   public int numDirectArenas() {
      return this.allocator.numDirectArenas();
   }

   public List<PoolArenaMetric> heapArenas() {
      return this.allocator.heapArenas();
   }

   public List<PoolArenaMetric> directArenas() {
      return this.allocator.directArenas();
   }

   public int numThreadLocalCaches() {
      return this.allocator.numThreadLocalCaches();
   }

   public int tinyCacheSize() {
      return this.allocator.tinyCacheSize();
   }

   public int smallCacheSize() {
      return this.allocator.smallCacheSize();
   }

   public int normalCacheSize() {
      return this.allocator.normalCacheSize();
   }

   public int chunkSize() {
      return this.allocator.chunkSize();
   }

   public long usedHeapMemory() {
      return this.allocator.usedHeapMemory();
   }

   public long usedDirectMemory() {
      return this.allocator.usedDirectMemory();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(256);
      var1.append(StringUtil.simpleClassName((Object)this)).append("(usedHeapMemory: ").append(this.usedHeapMemory()).append("; usedDirectMemory: ").append(this.usedDirectMemory()).append("; numHeapArenas: ").append(this.numHeapArenas()).append("; numDirectArenas: ").append(this.numDirectArenas()).append("; tinyCacheSize: ").append(this.tinyCacheSize()).append("; smallCacheSize: ").append(this.smallCacheSize()).append("; normalCacheSize: ").append(this.normalCacheSize()).append("; numThreadLocalCaches: ").append(this.numThreadLocalCaches()).append("; chunkSize: ").append(this.chunkSize()).append(')');
      return var1.toString();
   }
}
