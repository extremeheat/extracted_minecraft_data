package net.minecraft.client.renderer;

import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class SectionBufferBuilderPool {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int MAX_BUILDERS_32_BIT = 4;
   private final Queue<SectionBufferBuilderPack> freeBuffers;
   private volatile int freeBufferCount;

   private SectionBufferBuilderPool(List<SectionBufferBuilderPack> var1) {
      super();
      this.freeBuffers = Queues.newArrayDeque(var1);
      this.freeBufferCount = this.freeBuffers.size();
   }

   public static SectionBufferBuilderPool allocate(int var0) {
      int var1 = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / SectionBufferBuilderPack.TOTAL_BUFFERS_SIZE);
      int var2 = Math.max(1, Math.min(var0, var1));
      ArrayList var3 = new ArrayList(var2);

      try {
         for(int var4 = 0; var4 < var2; ++var4) {
            var3.add(new SectionBufferBuilderPack());
         }
      } catch (OutOfMemoryError var7) {
         LOGGER.warn("Allocated only {}/{} buffers", var3.size(), var2);
         int var5 = Math.min(var3.size() * 2 / 3, var3.size() - 1);

         for(int var6 = 0; var6 < var5; ++var6) {
            ((SectionBufferBuilderPack)var3.remove(var3.size() - 1)).close();
         }
      }

      return new SectionBufferBuilderPool(var3);
   }

   @Nullable
   public SectionBufferBuilderPack acquire() {
      SectionBufferBuilderPack var1 = this.freeBuffers.poll();
      if (var1 != null) {
         this.freeBufferCount = this.freeBuffers.size();
         return var1;
      } else {
         return null;
      }
   }

   public void release(SectionBufferBuilderPack var1) {
      this.freeBuffers.add(var1);
      this.freeBufferCount = this.freeBuffers.size();
   }

   public boolean isEmpty() {
      return this.freeBuffers.isEmpty();
   }

   public int getFreeBufferCount() {
      return this.freeBufferCount;
   }
}
