package net.minecraft.util.profiling.jfr.parse;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

public record JfrStatsResult(
   Instant a,
   Instant b,
   Duration c,
   @Nullable Duration d,
   List<TickTimeStat> e,
   List<CpuLoadStat> f,
   GcHeapStat.Summary g,
   ThreadAllocationStat.Summary h,
   IoSummary<PacketIdentification> i,
   IoSummary<PacketIdentification> j,
   IoSummary<ChunkIdentification> k,
   IoSummary<ChunkIdentification> l,
   FileIOStat.Summary m,
   FileIOStat.Summary n,
   List<ChunkGenStat> o
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
   private final IoSummary<PacketIdentification> receivedPacketsSummary;
   private final IoSummary<PacketIdentification> sentPacketsSummary;
   private final IoSummary<ChunkIdentification> writtenChunks;
   private final IoSummary<ChunkIdentification> readChunks;
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
      IoSummary<PacketIdentification> var9,
      IoSummary<PacketIdentification> var10,
      IoSummary<ChunkIdentification> var11,
      IoSummary<ChunkIdentification> var12,
      FileIOStat.Summary var13,
      FileIOStat.Summary var14,
      List<ChunkGenStat> var15
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
      this.writtenChunks = var11;
      this.readChunks = var12;
      this.fileWrites = var13;
      this.fileReads = var14;
      this.chunkGenStats = var15;
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
