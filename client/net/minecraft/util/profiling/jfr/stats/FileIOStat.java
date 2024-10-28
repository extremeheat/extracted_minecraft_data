package net.minecraft.util.profiling.jfr.stats;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public record FileIOStat(Duration duration, @Nullable String path, long bytes) {
   public FileIOStat(Duration duration, @Nullable String path, long bytes) {
      super();
      this.duration = duration;
      this.path = path;
      this.bytes = bytes;
   }

   public static Summary summary(Duration var0, List<FileIOStat> var1) {
      long var2 = var1.stream().mapToLong((var0x) -> {
         return var0x.bytes;
      }).sum();
      return new Summary(var2, (double)var2 / (double)var0.getSeconds(), (long)var1.size(), (double)var1.size() / (double)var0.getSeconds(), (Duration)var1.stream().map(FileIOStat::duration).reduce(Duration.ZERO, Duration::plus), ((Map)var1.stream().filter((var0x) -> {
         return var0x.path != null;
      }).collect(Collectors.groupingBy((var0x) -> {
         return var0x.path;
      }, Collectors.summingLong((var0x) -> {
         return var0x.bytes;
      })))).entrySet().stream().sorted(Entry.comparingByValue().reversed()).map((var0x) -> {
         return Pair.of((String)var0x.getKey(), (Long)var0x.getValue());
      }).limit(10L).toList());
   }

   public Duration duration() {
      return this.duration;
   }

   @Nullable
   public String path() {
      return this.path;
   }

   public long bytes() {
      return this.bytes;
   }

   public static record Summary(long totalBytes, double bytesPerSecond, long counts, double countsPerSecond, Duration timeSpentInIO, List<Pair<String, Long>> topTenContributorsByTotalBytes) {
      public Summary(long totalBytes, double bytesPerSecond, long counts, double countsPerSecond, Duration timeSpentInIO, List<Pair<String, Long>> topTenContributorsByTotalBytes) {
         super();
         this.totalBytes = totalBytes;
         this.bytesPerSecond = bytesPerSecond;
         this.counts = counts;
         this.countsPerSecond = countsPerSecond;
         this.timeSpentInIO = timeSpentInIO;
         this.topTenContributorsByTotalBytes = topTenContributorsByTotalBytes;
      }

      public long totalBytes() {
         return this.totalBytes;
      }

      public double bytesPerSecond() {
         return this.bytesPerSecond;
      }

      public long counts() {
         return this.counts;
      }

      public double countsPerSecond() {
         return this.countsPerSecond;
      }

      public Duration timeSpentInIO() {
         return this.timeSpentInIO;
      }

      public List<Pair<String, Long>> topTenContributorsByTotalBytes() {
         return this.topTenContributorsByTotalBytes;
      }
   }
}
