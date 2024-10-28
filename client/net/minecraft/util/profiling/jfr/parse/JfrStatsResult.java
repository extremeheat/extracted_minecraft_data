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
import net.minecraft.util.profiling.jfr.stats.ChunkIdentification;
import net.minecraft.util.profiling.jfr.stats.CpuLoadStat;
import net.minecraft.util.profiling.jfr.stats.FileIOStat;
import net.minecraft.util.profiling.jfr.stats.GcHeapStat;
import net.minecraft.util.profiling.jfr.stats.IoSummary;
import net.minecraft.util.profiling.jfr.stats.PacketIdentification;
import net.minecraft.util.profiling.jfr.stats.ThreadAllocationStat;
import net.minecraft.util.profiling.jfr.stats.TickTimeStat;
import net.minecraft.util.profiling.jfr.stats.TimedStatSummary;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public record JfrStatsResult(Instant recordingStarted, Instant recordingEnded, Duration recordingDuration, @Nullable Duration worldCreationDuration, List<TickTimeStat> tickTimes, List<CpuLoadStat> cpuLoadStats, GcHeapStat.Summary heapSummary, ThreadAllocationStat.Summary threadAllocationSummary, IoSummary<PacketIdentification> receivedPacketsSummary, IoSummary<PacketIdentification> sentPacketsSummary, IoSummary<ChunkIdentification> writtenChunks, IoSummary<ChunkIdentification> readChunks, FileIOStat.Summary fileWrites, FileIOStat.Summary fileReads, List<ChunkGenStat> chunkGenStats) {
   public JfrStatsResult(Instant recordingStarted, Instant recordingEnded, Duration recordingDuration, @Nullable Duration worldCreationDuration, List<TickTimeStat> tickTimes, List<CpuLoadStat> cpuLoadStats, GcHeapStat.Summary heapSummary, ThreadAllocationStat.Summary threadAllocationSummary, IoSummary<PacketIdentification> receivedPacketsSummary, IoSummary<PacketIdentification> sentPacketsSummary, IoSummary<ChunkIdentification> writtenChunks, IoSummary<ChunkIdentification> readChunks, FileIOStat.Summary fileWrites, FileIOStat.Summary fileReads, List<ChunkGenStat> chunkGenStats) {
      super();
      this.recordingStarted = recordingStarted;
      this.recordingEnded = recordingEnded;
      this.recordingDuration = recordingDuration;
      this.worldCreationDuration = worldCreationDuration;
      this.tickTimes = tickTimes;
      this.cpuLoadStats = cpuLoadStats;
      this.heapSummary = heapSummary;
      this.threadAllocationSummary = threadAllocationSummary;
      this.receivedPacketsSummary = receivedPacketsSummary;
      this.sentPacketsSummary = sentPacketsSummary;
      this.writtenChunks = writtenChunks;
      this.readChunks = readChunks;
      this.fileWrites = fileWrites;
      this.fileReads = fileReads;
      this.chunkGenStats = chunkGenStats;
   }

   public List<Pair<ChunkStatus, TimedStatSummary<ChunkGenStat>>> chunkGenSummary() {
      Map var1 = (Map)this.chunkGenStats.stream().collect(Collectors.groupingBy(ChunkGenStat::status));
      return var1.entrySet().stream().map((var0) -> {
         return Pair.of((ChunkStatus)var0.getKey(), TimedStatSummary.summary((List)var0.getValue()));
      }).sorted(Comparator.comparing((var0) -> {
         return ((TimedStatSummary)var0.getSecond()).totalDuration();
      }).reversed()).toList();
   }

   public String asJson() {
      return (new JfrResultJsonSerializer()).format(this);
   }

   public Instant recordingStarted() {
      return this.recordingStarted;
   }

   public Instant recordingEnded() {
      return this.recordingEnded;
   }

   public Duration recordingDuration() {
      return this.recordingDuration;
   }

   @Nullable
   public Duration worldCreationDuration() {
      return this.worldCreationDuration;
   }

   public List<TickTimeStat> tickTimes() {
      return this.tickTimes;
   }

   public List<CpuLoadStat> cpuLoadStats() {
      return this.cpuLoadStats;
   }

   public GcHeapStat.Summary heapSummary() {
      return this.heapSummary;
   }

   public ThreadAllocationStat.Summary threadAllocationSummary() {
      return this.threadAllocationSummary;
   }

   public IoSummary<PacketIdentification> receivedPacketsSummary() {
      return this.receivedPacketsSummary;
   }

   public IoSummary<PacketIdentification> sentPacketsSummary() {
      return this.sentPacketsSummary;
   }

   public IoSummary<ChunkIdentification> writtenChunks() {
      return this.writtenChunks;
   }

   public IoSummary<ChunkIdentification> readChunks() {
      return this.readChunks;
   }

   public FileIOStat.Summary fileWrites() {
      return this.fileWrites;
   }

   public FileIOStat.Summary fileReads() {
      return this.fileReads;
   }

   public List<ChunkGenStat> chunkGenStats() {
      return this.chunkGenStats;
   }
}
