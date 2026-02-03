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

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      long max = Runtime.getRuntime().maxMemory();
      long total = Runtime.getRuntime().totalMemory();
      long free = Runtime.getRuntime().freeMemory();
      long used = total - free;
      displayer.addToGroup(GROUP, List.of(String.format(Locale.ROOT, "Mem: %2d%% %03d/%03dMB", used * 100L / max, bytesToMegabytes(used), bytesToMegabytes(max)), String.format(Locale.ROOT, "Allocation rate: %03dMB/s", bytesToMegabytes(this.allocationRateCalculator.bytesAllocatedPerSecond(used))), String.format(Locale.ROOT, "Allocated: %2d%% %03dMB", total * 100L / max, bytesToMegabytes(total))));
   }

   private static long bytesToMegabytes(final long used) {
      return used / 1024L / 1024L;
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }

   private static class AllocationRateCalculator {
      private static final int UPDATE_INTERVAL_MS = 500;
      private static final List<GarbageCollectorMXBean> GC_MBEANS = ManagementFactory.getGarbageCollectorMXBeans();
      private long lastTime = 0L;
      private long lastHeapUsage = -1L;
      private long lastGcCounts = -1L;
      private long lastRate = 0L;

      private AllocationRateCalculator() {
         super();
      }

      private long bytesAllocatedPerSecond(final long currentHeapUsage) {
         long time = System.currentTimeMillis();
         if (time - this.lastTime < 500L) {
            return this.lastRate;
         } else {
            long gcCounts = gcCounts();
            if (this.lastTime != 0L && gcCounts == this.lastGcCounts) {
               double multiplier = (double)TimeUnit.SECONDS.toMillis(1L) / (double)(time - this.lastTime);
               long delta = currentHeapUsage - this.lastHeapUsage;
               this.lastRate = Math.round((double)delta * multiplier);
            }

            this.lastTime = time;
            this.lastHeapUsage = currentHeapUsage;
            this.lastGcCounts = gcCounts;
            return this.lastRate;
         }
      }

      private static long gcCounts() {
         long total = 0L;

         for(GarbageCollectorMXBean gcBean : GC_MBEANS) {
            total += gcBean.getCollectionCount();
         }

         return total;
      }
   }
}
