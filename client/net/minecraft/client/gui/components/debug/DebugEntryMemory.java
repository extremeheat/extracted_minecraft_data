package net.minecraft.client.gui.components.debug;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryMemory implements DebugScreenEntry {
   private static final Identifier GROUP = Identifier.withDefaultNamespace("memory");
   private final AllocationRateCalculator allocationRateCalculator = new AllocationRateCalculator();

   public DebugEntryMemory() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      long var5 = Runtime.getRuntime().maxMemory();
      long var7 = Runtime.getRuntime().totalMemory();
      long var9 = Runtime.getRuntime().freeMemory();
      long var11 = var7 - var9;
      var1.addToGroup(GROUP, List.of(String.format(Locale.ROOT, "Mem: %2d%% %03d/%03dMB", var11 * 100L / var5, bytesToMegabytes(var11), bytesToMegabytes(var5)), String.format(Locale.ROOT, "Allocation rate: %03dMB/s", bytesToMegabytes(this.allocationRateCalculator.bytesAllocatedPerSecond(var11))), String.format(Locale.ROOT, "Allocated: %2d%% %03dMB", var7 * 100L / var5, bytesToMegabytes(var7))));
   }

   private static long bytesToMegabytes(long var0) {
      return var0 / 1024L / 1024L;
   }

   public boolean isAllowed(boolean var1) {
      return true;
   }

   static class AllocationRateCalculator {
      private static final int UPDATE_INTERVAL_MS = 500;
      private static final List<GarbageCollectorMXBean> GC_MBEANS = ManagementFactory.getGarbageCollectorMXBeans();
      private long lastTime = 0L;
      private long lastHeapUsage = -1L;
      private long lastGcCounts = -1L;
      private long lastRate = 0L;

      AllocationRateCalculator() {
         super();
      }

      long bytesAllocatedPerSecond(long var1) {
         long var3 = System.currentTimeMillis();
         if (var3 - this.lastTime < 500L) {
            return this.lastRate;
         } else {
            long var5 = gcCounts();
            if (this.lastTime != 0L && var5 == this.lastGcCounts) {
               double var7 = (double)TimeUnit.SECONDS.toMillis(1L) / (double)(var3 - this.lastTime);
               long var9 = var1 - this.lastHeapUsage;
               this.lastRate = Math.round((double)var9 * var7);
            }

            this.lastTime = var3;
            this.lastHeapUsage = var1;
            this.lastGcCounts = var5;
            return this.lastRate;
         }
      }

      private static long gcCounts() {
         long var0 = 0L;

         for(GarbageCollectorMXBean var3 : GC_MBEANS) {
            var0 += var3.getCollectionCount();
         }

         return var0;
      }
   }
}
