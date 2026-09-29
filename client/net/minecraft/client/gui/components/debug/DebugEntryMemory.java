package net.minecraft.client.gui.components.debug;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryMemory implements DebugScreenEntry {
   private final AllocationRateCalculator allocationRateCalculator = new AllocationRateCalculator();

   public DebugEntryMemory() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      long max = Runtime.getRuntime().maxMemory();
      long total = Runtime.getRuntime().totalMemory();
      long free = Runtime.getRuntime().freeMemory();
      long used = total - free;
      displayer.addFactToGroup(DebugGroups.MEMORY, "Used", (fact) -> fact.formattedValue("%2d", used * 100L / max).text("% ").formattedValue("%03d", bytesToMebibytes(used)).text("/").formattedValue("%03d", bytesToMebibytes(max)).text("MiB"));
      displayer.addFactToGroup(DebugGroups.MEMORY, "Alloc rate", (fact) -> fact.formattedValue("%03d", bytesToMebibytes(this.allocationRateCalculator.bytesAllocatedPerSecond(used))).text("MiB/s"));
      displayer.addFactToGroup(DebugGroups.MEMORY, "Allocated", (fact) -> fact.formattedValue("%2d", total * 100L / max).text("% ").formattedValue("%03d", bytesToMebibytes(total)).text("MiB"));
   }

   private static long bytesToMebibytes(final long used) {
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
