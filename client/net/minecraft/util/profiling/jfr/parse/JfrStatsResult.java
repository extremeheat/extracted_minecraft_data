package net.minecraft.util.profiling.jfr.parse;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.jfr.serialize.JfrResultJsonSerializer;
import net.minecraft.util.profiling.jfr.stats.ChunkGenStat;
import net.minecraft.util.profiling.jfr.stats.CpuLoadStat;
import net.minecraft.util.profiling.jfr.stats.FileIOStat;
import net.minecraft.util.profiling.jfr.stats.GcHeapStat;
import net.minecraft.util.profiling.jfr.stats.NetworkPacketSummary;
import net.minecraft.util.profiling.jfr.stats.ThreadAllocationStat;
import net.minecraft.util.profiling.jfr.stats.TickTimeStat;
import net.minecraft.util.profiling.jfr.stats.TimedStatSummary;
import net.minecraft.world.level.chunk.ChunkStatus;

public record JfrStatsResult(
   Instant a,
   Instant b,
   Duration c,
   @Nullable Duration d,
   List<TickTimeStat> e,
   List<CpuLoadStat> f,
   GcHeapStat.Summary g,
   ThreadAllocationStat.Summary h,
   NetworkPacketSummary i,
   NetworkPacketSummary j,
   FileIOStat.Summary k,
   FileIOStat.Summary l,
   List<ChunkGenStat> m
) {
   private final Instant recordingStarted;
   private final Instant recordingEnded;
   private final Duration recordingDuration;
   @Nullable
   private final Duration worldCreationDuration;
   private final List<TickTimeStat> tickTimes;
   private final List<CpuLoadStat> cpuLoadStats;
   private final GcHeapStat.Summary heapSummary;
   private final ThreadAllocationStat.Summary threadAllocationSummary;
   private final NetworkPacketSummary receivedPacketsSummary;
   private final NetworkPacketSummary sentPacketsSummary;
   private final FileIOStat.Summary fileWrites;
   private final FileIOStat.Summary fileReads;
   private final List<ChunkGenStat> chunkGenStats;

   public JfrStatsResult(
      Instant var1,
      Instant var2,
      Duration var3,
      @Nullable Duration var4,
      List<TickTimeStat> var5,
      List<CpuLoadStat> var6,
      GcHeapStat.Summary var7,
      ThreadAllocationStat.Summary var8,
      NetworkPacketSummary var9,
      NetworkPacketSummary var10,
      FileIOStat.Summary var11,
      FileIOStat.Summary var12,
      List<ChunkGenStat> var13
   ) {
      super();
      this.recordingStarted = var1;
      this.recordingEnded = var2;
      this.recordingDuration = var3;
      this.worldCreationDuration = var4;
      this.tickTimes = var5;
      this.cpuLoadStats = var6;
      this.heapSummary = var7;
      this.threadAllocationSummary = var8;
      this.receivedPacketsSummary = var9;
      this.sentPacketsSummary = var10;
      this.fileWrites = var11;
      this.fileReads = var12;
      this.chunkGenStats = var13;
   }

   public List<Pair<ChunkStatus, TimedStatSummary<ChunkGenStat>>> chunkGenSummary() {
      Map var1 = this.chunkGenStats.stream().collect(Collectors.groupingBy(ChunkGenStat::status));
      return var1.entrySet()
         .stream()
         .map(var0 -> Pair.of((ChunkStatus)var0.getKey(), TimedStatSummary.summary((List)var0.getValue())))
         .sorted(Comparator.comparing(var0 -> ((TimedStatSummary)var0.getSecond()).totalDuration()).reversed())
         .toList();
   }

   public String asJson() {
      return new JfrResultJsonSerializer().format(this);
   }
}
