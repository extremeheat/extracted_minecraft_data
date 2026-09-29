package net.minecraft.client.gui.components.debug;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryDetailedMemory implements DebugScreenEntry {
   private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

   public DebugEntryDetailedMemory() {
      super();
   }

   public void display(final DebugScreenDisplayer displayer, final @Nullable Level serverOrClientLevel, final @Nullable LevelChunk clientChunk, final @Nullable LevelChunk serverChunk) {
      displayer.addFactToGroup(DebugGroups.MEMORY, "Heap", (fact) -> getMemoryUsage(fact, this.memoryBean.getHeapMemoryUsage()));
      displayer.addFactToGroup(DebugGroups.MEMORY, "Non-heap", (fact) -> getMemoryUsage(fact, this.memoryBean.getNonHeapMemoryUsage()));
   }

   private static long bytesToMebibytes(final long used) {
      return used / 1024L / 1024L;
   }

   private static void getMemoryUsage(final DebugFact fact, final MemoryUsage memoryUsage) {
      fact.text("i=").formattedValue("%03d", bytesToMebibytes(memoryUsage.getInit())).text("MiB u=").formattedValue("%03d", bytesToMebibytes(memoryUsage.getUsed())).text("MiB c=").formattedValue("%03d", bytesToMebibytes(memoryUsage.getCommitted())).text("MiB m=").formattedValue("%03d", bytesToMebibytes(memoryUsage.getMax())).text("MiB");
   }

   public boolean isAllowed(final boolean reducedDebugInfo) {
      return true;
   }
}
