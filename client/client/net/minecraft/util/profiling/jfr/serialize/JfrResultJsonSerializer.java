package net.minecraft.util.profiling.jfr.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.LongSerializationPolicy;
import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import net.minecraft.Util;
import net.minecraft.util.profiling.jfr.Percentiles;
import net.minecraft.util.profiling.jfr.parse.JfrStatsResult;
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

public class JfrResultJsonSerializer {
   private static final String BYTES_PER_SECOND = "bytesPerSecond";
   private static final String COUNT = "count";
   private static final String DURATION_NANOS_TOTAL = "durationNanosTotal";
   private static final String TOTAL_BYTES = "totalBytes";
   private static final String COUNT_PER_SECOND = "countPerSecond";
   final Gson gson = new GsonBuilder().setPrettyPrinting().setLongSerializationPolicy(LongSerializationPolicy.DEFAULT).create();

   public JfrResultJsonSerializer() {
      super();
   }

   private static void serializePacketId(PacketIdentification var0, JsonObject var1) {
      var1.addProperty("protocolId", var0.protocolId());
      var1.addProperty("packetId", var0.packetId());
   }

   private static void serializeChunkId(ChunkIdentification var0, JsonObject var1) {
      var1.addProperty("level", var0.level());
      var1.addProperty("dimension", var0.dimension());
      var1.addProperty("x", var0.x());
      var1.addProperty("z", var0.z());
   }

   public String format(JfrStatsResult var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("startedEpoch", var1.recordingStarted().toEpochMilli());
      var2.addProperty("endedEpoch", var1.recordingEnded().toEpochMilli());
      var2.addProperty("durationMs", var1.recordingDuration().toMillis());
      Duration var3 = var1.worldCreationDuration();
      if (var3 != null) {
         var2.addProperty("worldGenDurationMs", var3.toMillis());
      }

      var2.add("heap", this.heap(var1.heapSummary()));
      var2.add("cpuPercent", this.cpu(var1.cpuLoadStats()));
      var2.add("network", this.network(var1));
      var2.add("fileIO", this.fileIO(var1));
      var2.add("serverTick", this.serverTicks(var1.tickTimes()));
      var2.add("threadAllocation", this.threadAllocations(var1.threadAllocationSummary()));
      var2.add("chunkGen", this.chunkGen(var1.chunkGenSummary()));
      return this.gson.toJson(var2);
   }

   private JsonElement heap(GcHeapStat.Summary var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("allocationRateBytesPerSecond", var1.allocationRateBytesPerSecond());
      var2.addProperty("gcCount", var1.totalGCs());
      var2.addProperty("gcOverHeadPercent", var1.gcOverHead());
      var2.addProperty("gcTotalDurationMs", var1.gcTotalDuration().toMillis());
      return var2;
   }

   private JsonElement chunkGen(List<Pair<ChunkStatus, TimedStatSummary<ChunkGenStat>>> var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("durationNanosTotal", var1.stream().mapToDouble(var0 -> (double)((TimedStatSummary)var0.getSecond()).totalDuration().toNanos()).sum());
      JsonArray var3 = Util.make(new JsonArray(), var1x -> var2.add("status", var1x));

      for (Pair var5 : var1) {
         TimedStatSummary var6 = (TimedStatSummary)var5.getSecond();
         JsonObject var7 = Util.make(new JsonObject(), var3::add);
         var7.addProperty("state", ((ChunkStatus)var5.getFirst()).toString());
         var7.addProperty("count", var6.count());
         var7.addProperty("durationNanosTotal", var6.totalDuration().toNanos());
         var7.addProperty("durationNanosAvg", var6.totalDuration().toNanos() / (long)var6.count());
         JsonObject var8 = Util.make(new JsonObject(), var1x -> var7.add("durationNanosPercentiles", var1x));
         var6.percentilesNanos().forEach((var1x, var2x) -> var8.addProperty("p" + var1x, var2x));
         Function var9 = var0 -> {
            JsonObject var1x = new JsonObject();
            var1x.addProperty("durationNanos", var0.duration().toNanos());
            var1x.addProperty("level", var0.level());
            var1x.addProperty("chunkPosX", var0.chunkPos().x);
            var1x.addProperty("chunkPosZ", var0.chunkPos().z);
            var1x.addProperty("worldPosX", var0.worldPos().x());
            var1x.addProperty("worldPosZ", var0.worldPos().z());
            return var1x;
         };
         var7.add("fastest", (JsonElement)var9.apply((ChunkGenStat)var6.fastest()));
         var7.add("slowest", (JsonElement)var9.apply((ChunkGenStat)var6.slowest()));
         var7.add(
            "secondSlowest", (JsonElement)(var6.secondSlowest() != null ? (JsonElement)var9.apply((ChunkGenStat)var6.secondSlowest()) : JsonNull.INSTANCE)
         );
      }

      return var2;
   }

   private JsonElement threadAllocations(ThreadAllocationStat.Summary var1) {
      JsonArray var2 = new JsonArray();
      var1.allocationsPerSecondByThread().forEach((var1x, var2x) -> var2.add(Util.make(new JsonObject(), var2xx -> {
            var2xx.addProperty("thread", var1x);
            var2xx.addProperty("bytesPerSecond", var2x);
         })));
      return var2;
   }

   private JsonElement serverTicks(List<TickTimeStat> var1) {
      if (var1.isEmpty()) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var2 = new JsonObject();
         double[] var3 = var1.stream().mapToDouble(var0 -> (double)var0.currentAverage().toNanos() / 1000000.0).toArray();
         DoubleSummaryStatistics var4 = DoubleStream.of(var3).summaryStatistics();
         var2.addProperty("minMs", var4.getMin());
         var2.addProperty("averageMs", var4.getAverage());
         var2.addProperty("maxMs", var4.getMax());
         Map var5 = Percentiles.evaluate(var3);
         var5.forEach((var1x, var2x) -> var2.addProperty("p" + var1x, var2x));
         return var2;
      }
   }

   private JsonElement fileIO(JfrStatsResult var1) {
      JsonObject var2 = new JsonObject();
      var2.add("write", this.fileIoSummary(var1.fileWrites()));
      var2.add("read", this.fileIoSummary(var1.fileReads()));
      var2.add("chunksRead", this.ioSummary(var1.readChunks(), JfrResultJsonSerializer::serializeChunkId));
      var2.add("chunksWritten", this.ioSummary(var1.writtenChunks(), JfrResultJsonSerializer::serializeChunkId));
      return var2;
   }

   private JsonElement fileIoSummary(FileIOStat.Summary var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("totalBytes", var1.totalBytes());
      var2.addProperty("count", var1.counts());
      var2.addProperty("bytesPerSecond", var1.bytesPerSecond());
      var2.addProperty("countPerSecond", var1.countsPerSecond());
      JsonArray var3 = new JsonArray();
      var2.add("topContributors", var3);
      var1.topTenContributorsByTotalBytes().forEach(var1x -> {
         JsonObject var2x = new JsonObject();
         var3.add(var2x);
         var2x.addProperty("path", (String)var1x.getFirst());
         var2x.addProperty("totalBytes", (Number)var1x.getSecond());
      });
      return var2;
   }

   private JsonElement network(JfrStatsResult var1) {
      JsonObject var2 = new JsonObject();
      var2.add("sent", this.ioSummary(var1.sentPacketsSummary(), JfrResultJsonSerializer::serializePacketId));
      var2.add("received", this.ioSummary(var1.receivedPacketsSummary(), JfrResultJsonSerializer::serializePacketId));
      return var2;
   }

   private <T> JsonElement ioSummary(IoSummary<T> var1, BiConsumer<T, JsonObject> var2) {
      JsonObject var3 = new JsonObject();
      var3.addProperty("totalBytes", var1.getTotalSize());
      var3.addProperty("count", var1.getTotalCount());
      var3.addProperty("bytesPerSecond", var1.getSizePerSecond());
      var3.addProperty("countPerSecond", var1.getCountsPerSecond());
      JsonArray var4 = new JsonArray();
      var3.add("topContributors", var4);
      var1.largestSizeContributors().forEach(var2x -> {
         JsonObject var3x = new JsonObject();
         var4.add(var3x);
         Object var4x = var2x.getFirst();
         IoSummary.CountAndSize var5 = (IoSummary.CountAndSize)var2x.getSecond();
         var2.accept(var4x, var3x);
         var3x.addProperty("totalBytes", var5.totalSize());
         var3x.addProperty("count", var5.totalCount());
         var3x.addProperty("averageSize", var5.averageSize());
      });
      return var3;
   }

   private JsonElement cpu(List<CpuLoadStat> var1) {
      JsonObject var2 = new JsonObject();
      BiFunction var3 = (var0, var1x) -> {
         JsonObject var2x = new JsonObject();
         DoubleSummaryStatistics var3x = var0.stream().mapToDouble(var1x).summaryStatistics();
         var2x.addProperty("min", var3x.getMin());
         var2x.addProperty("average", var3x.getAverage());
         var2x.addProperty("max", var3x.getMax());
         return var2x;
      };
      var2.add("jvm", (JsonElement)var3.apply(var1, CpuLoadStat::jvm));
      var2.add("userJvm", (JsonElement)var3.apply(var1, CpuLoadStat::userJvm));
      var2.add("system", (JsonElement)var3.apply(var1, CpuLoadStat::system));
      return var2;
   }
}