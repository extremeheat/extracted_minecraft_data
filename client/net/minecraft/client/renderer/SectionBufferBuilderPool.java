package net.minecraft.client.renderer;

import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SectionBufferBuilderPool {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Queue<SectionBufferBuilderPack> freeBuffers;
   private volatile int freeBufferCount;

   private SectionBufferBuilderPool(final List<SectionBufferBuilderPack> buffers) {
      super();
      this.freeBuffers = Queues.newArrayDeque(buffers);
      this.freeBufferCount = this.freeBuffers.size();
   }

   public static SectionBufferBuilderPool allocate(final int maxWorkers) {
      int maxBuffers = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / SectionBufferBuilderPack.TOTAL_BUFFERS_SIZE);
      int targetBufferCount = Math.max(1, Math.min(maxWorkers, maxBuffers));
      List<SectionBufferBuilderPack> buffers = new ArrayList(targetBufferCount);

      try {
         for(int i = 0; i < targetBufferCount; ++i) {
            buffers.add(new SectionBufferBuilderPack());
         }
      } catch (OutOfMemoryError var7) {
         LOGGER.warn("Allocated only {}/{} buffers", buffers.size(), targetBufferCount);
         int buffersToDrop = Math.min(buffers.size() * 2 / 3, buffers.size() - 1);

         for(int i = 0; i < buffersToDrop; ++i) {
            ((SectionBufferBuilderPack)buffers.remove(buffers.size() - 1)).close();
         }
      }

      return new SectionBufferBuilderPool(buffers);
   }

   public @Nullable SectionBufferBuilderPack acquire() {
      SectionBufferBuilderPack buffer = (SectionBufferBuilderPack)this.freeBuffers.poll();
      if (buffer != null) {
         this.freeBufferCount = this.freeBuffers.size();
         return buffer;
      } else {
         return null;
      }
   }

   public void release(final SectionBufferBuilderPack buffer) {
      this.freeBuffers.add(buffer);
      this.freeBufferCount = this.freeBuffers.size();
   }

   public boolean isEmpty() {
      return this.freeBuffers.isEmpty();
   }

   public int getFreeBufferCount() {
      return this.freeBufferCount;
   }
}
