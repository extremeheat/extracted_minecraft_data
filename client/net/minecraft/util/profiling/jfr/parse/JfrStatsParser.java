package net.minecraft.util.profiling.jfr.parse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import net.minecraft.util.profiling.jfr.stats.ChunkGenStat;
import net.minecraft.util.profiling.jfr.stats.CpuLoadStat;
import net.minecraft.util.profiling.jfr.stats.FileIOStat;
import net.minecraft.util.profiling.jfr.stats.GcHeapStat;
import net.minecraft.util.profiling.jfr.stats.NetworkPacketSummary;
import net.minecraft.util.profiling.jfr.stats.ThreadAllocationStat;
import net.minecraft.util.profiling.jfr.stats.TickTimeStat;

public class JfrStatsParser {
   private Instant recordingStarted = Instant.EPOCH;
   private Instant recordingEnded = Instant.EPOCH;
   private final List<ChunkGenStat> chunkGenStats = Lists.newArrayList();
   private final List<CpuLoadStat> cpuLoadStat = Lists.newArrayList();
   private final Map<NetworkPacketSummary.PacketIdentification, JfrStatsParser.MutableCountAndSize> receivedPackets = Maps.newHashMap();
   private final Map<NetworkPacketSummary.PacketIdentification, JfrStatsParser.MutableCountAndSize> sentPackets = Maps.newHashMap();
   private final List<FileIOStat> fileWrites = Lists.newArrayList();
   private final List<FileIOStat> fileReads = Lists.newArrayList();
   private int garbageCollections;
   private Duration gcTotalDuration = Duration.ZERO;
   private final List<GcHeapStat> gcHeapStats = Lists.newArrayList();
   private final List<ThreadAllocationStat> threadAllocationStats = Lists.newArrayList();
   private final List<TickTimeStat> tickTimes = Lists.newArrayList();
   @Nullable
   private Duration worldCreationDuration = null;

   private JfrStatsParser(Stream<RecordedEvent> var1) {
      super();
      this.capture(var1);
   }

   public static JfrStatsResult parse(Path var0) {
      try {
         JfrStatsResult var4;
         try (final RecordingFile var1 = new RecordingFile(var0)) {
            Iterator var2 = new Iterator<RecordedEvent>() {
               @Override
               public boolean hasNext() {
                  return var1.hasMoreEvents();
               }

               public RecordedEvent next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     try {
                        return var1.readEvent();
                     } catch (IOException var2) {
                        throw new UncheckedIOException(var2);
                     }
                  }
               }
            };
            Stream var3 = StreamSupport.stream(Spliterators.spliteratorUnknownSize(var2, 1297), false);
            var4 = new JfrStatsParser(var3).results();
         }

         return var4;
      } catch (IOException var7) {
         throw new UncheckedIOException(var7);
      }
   }

   private JfrStatsResult results() {
      Duration var1 = Duration.between(this.recordingStarted, this.recordingEnded);
      return new JfrStatsResult(
         this.recordingStarted,
         this.recordingEnded,
         var1,
         this.worldCreationDuration,
         this.tickTimes,
         this.cpuLoadStat,
         GcHeapStat.summary(var1, this.gcHeapStats, this.gcTotalDuration, this.garbageCollections),
         ThreadAllocationStat.summary(this.threadAllocationStats),
         collectPacketStats(var1, this.receivedPackets),
         collectPacketStats(var1, this.sentPackets),
         FileIOStat.summary(var1, this.fileWrites),
         FileIOStat.summary(var1, this.fileReads),
         this.chunkGenStats
      );
   }

   private void capture(Stream<RecordedEvent> var1) {
      var1.forEach(var1x -> {
         if (var1x.getEndTime().isAfter(this.recordingEnded) || this.recordingEnded.equals(Instant.EPOCH)) {
            this.recordingEnded = var1x.getEndTime();
         }

         if (var1x.getStartTime().isBefore(this.recordingStarted) || this.recordingStarted.equals(Instant.EPOCH)) {
            this.recordingStarted = var1x.getStartTime();
         }

         String var2 = var1x.getEventType().getName();
         switch(var2) {
            case "minecraft.ChunkGeneration":
               this.chunkGenStats.add(ChunkGenStat.from(var1x));
               break;
            case "minecraft.LoadWorld":
               this.worldCreationDuration = var1x.getDuration();
               break;
            case "minecraft.ServerTickTime":
               this.tickTimes.add(TickTimeStat.from(var1x));
               break;
            case "minecraft.PacketReceived":
               this.incrementPacket(var1x, var1x.getInt("bytes"), this.receivedPackets);
               break;
            case "minecraft.PacketSent":
               this.incrementPacket(var1x, var1x.getInt("bytes"), this.sentPackets);
               break;
            case "jdk.ThreadAllocationStatistics":
               this.threadAllocationStats.add(ThreadAllocationStat.from(var1x));
               break;
            case "jdk.GCHeapSummary":
               this.gcHeapStats.add(GcHeapStat.from(var1x));
               break;
            case "jdk.CPULoad":
               this.cpuLoadStat.add(CpuLoadStat.from(var1x));
               break;
            case "jdk.FileWrite":
               this.appendFileIO(var1x, this.fileWrites, "bytesWritten");
               break;
            case "jdk.FileRead":
               this.appendFileIO(var1x, this.fileReads, "bytesRead");
               break;
            case "jdk.GarbageCollection":
               ++this.garbageCollections;
               this.gcTotalDuration = this.gcTotalDuration.plus(var1x.getDuration());
         }
      });
   }

   private void incrementPacket(RecordedEvent var1, int var2, Map<NetworkPacketSummary.PacketIdentification, JfrStatsParser.MutableCountAndSize> var3) {
      var3.computeIfAbsent(NetworkPacketSummary.PacketIdentification.from(var1), var0 -> new JfrStatsParser.MutableCountAndSize()).increment(var2);
   }

   private void appendFileIO(RecordedEvent var1, List<FileIOStat> var2, String var3) {
      var2.add(new FileIOStat(var1.getDuration(), var1.getString("path"), var1.getLong(var3)));
   }

   private static NetworkPacketSummary collectPacketStats(
      Duration var0, Map<NetworkPacketSummary.PacketIdentification, JfrStatsParser.MutableCountAndSize> var1
   ) {
      List var2 = var1.entrySet()
         .stream()
         .map(
            var0x -> Pair.of(
                  (NetworkPacketSummary.PacketIdentification)var0x.getKey(), ((JfrStatsParser.MutableCountAndSize)var0x.getValue()).toCountAndSize()
               )
         )
         .toList();
      return new NetworkPacketSummary(var0, var2);
   }

   public static final class MutableCountAndSize {
      private long count;
      private long totalSize;

      public MutableCountAndSize() {
         super();
      }

      public void increment(int var1) {
         this.totalSize += (long)var1;
         ++this.count;
      }

      public NetworkPacketSummary.PacketCountAndSize toCountAndSize() {
         return new NetworkPacketSummary.PacketCountAndSize(this.count, this.totalSize);
      }
   }
}
