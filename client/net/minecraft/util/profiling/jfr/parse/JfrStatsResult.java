package net.minecraft.util.profiling.jfr.parse;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.jfr.serialize.JfrResultJsonSerializer;
import net.minecraft.util.profiling.jfr.stats.ChunkGenStat;
import net.minecraft.util.profiling.jfr.stats.ChunkIdentification;
import net.minecraft.util.profiling.jfr.stats.CpuLoadStat;
import net.minecraft.util.profiling.jfr.stats.FileIOStat;
import net.minecraft.util.profiling.jfr.stats.FpsStat;
import net.minecraft.util.profiling.jfr.stats.GcHeapStat;
import net.minecraft.util.profiling.jfr.stats.IoSummary;
import net.minecraft.util.profiling.jfr.stats.PacketIdentification;
import net.minecraft.util.profiling.jfr.stats.StructureGenStat;
import net.minecraft.util.profiling.jfr.stats.ThreadAllocationStat;
import net.minecraft.util.profiling.jfr.stats.TickTimeStat;
import net.minecraft.util.profiling.jfr.stats.TimedStatSummary;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public record JfrStatsResult(Instant recordingStarted, Instant recordingEnded, Duration recordingDuration, @Nullable Duration worldCreationDuration, List<FpsStat> fps, List<TickTimeStat> serverTickTimes, List<CpuLoadStat> cpuLoadStats, GcHeapStat.Summary heapSummary, ThreadAllocationStat.Summary threadAllocationSummary, IoSummary<PacketIdentification> receivedPacketsSummary, IoSummary<PacketIdentification> sentPacketsSummary, IoSummary<ChunkIdentification> writtenChunks, IoSummary<ChunkIdentification> readChunks, FileIOStat.Summary fileWrites, FileIOStat.Summary fileReads, List<ChunkGenStat> chunkGenStats, List<StructureGenStat> structureGenStats) {
   public JfrStatsResult(Instant var1, Instant var2, Duration var3, @Nullable Duration var4, List<FpsStat> var5, List<TickTimeStat> var6, List<CpuLoadStat> var7, GcHeapStat.Summary var8, ThreadAllocationStat.Summary var9, IoSummary<PacketIdentification> var10, IoSummary<PacketIdentification> var11, IoSummary<ChunkIdentification> var12, IoSummary<ChunkIdentification> var13, FileIOStat.Summary var14, FileIOStat.Summary var15, List<ChunkGenStat> var16, List<StructureGenStat> var17) {
      super();
      this.recordingStarted = var1;
      this.recordingEnded = var2;
      this.recordingDuration = var3;
      this.worldCreationDuration = var4;
      this.fps = var5;
      this.serverTickTimes = var6;
      this.cpuLoadStats = var7;
      this.heapSummary = var8;
      this.threadAllocationSummary = var9;
      this.receivedPacketsSummary = var10;
      this.sentPacketsSummary = var11;
      this.writtenChunks = var12;
      this.readChunks = var13;
      this.fileWrites = var14;
      this.fileReads = var15;
      this.chunkGenStats = var16;
      this.structureGenStats = var17;
   }

   public List<Pair<ChunkStatus, TimedStatSummary<ChunkGenStat>>> chunkGenSummary() {
      Map var1 = (Map)this.chunkGenStats.stream().collect(Collectors.groupingBy(ChunkGenStat::status));
      return var1.entrySet().stream().map((var0) -> Pair.of((ChunkStatus)var0.getKey(), TimedStatSummary.summary((List)var0.getValue()))).filter((var0) -> ((Optional)var0.getSecond()).isPresent()).map((var0) -> Pair.of((ChunkStatus)var0.getFirst(), (TimedStatSummary)((Optional)var0.getSecond()).get())).sorted(Comparator.comparing((var0) -> ((TimedStatSummary)var0.getSecond()).totalDuration()).reversed()).toList();
   }

   public String asJson() {
      return (new JfrResultJsonSerializer()).format(this);
   }
}
