package net.minecraft.util.profiling.jfr.stats;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.List;
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

   public static FileIOStat.Summary summary(Duration var0, List<FileIOStat> var1) {
      long var2 = var1.stream().mapToLong(var0x -> var0x.bytes).sum();
      return new FileIOStat.Summary(
         var2,
         (double)var2 / (double)var0.getSeconds(),
         (long)var1.size(),
         (double)var1.size() / (double)var0.getSeconds(),
         var1.stream().map(FileIOStat::duration).reduce(Duration.ZERO, Duration::plus),
         var1.stream()
            .filter(var0x -> var0x.path != null)
            .collect(Collectors.groupingBy(var0x -> var0x.path, Collectors.summingLong(var0x -> var0x.bytes)))
            .entrySet()
            .stream()
            .sorted(Entry.<String, Long>comparingByValue().reversed())
            .map(var0x -> Pair.of(var0x.getKey(), var0x.getValue()))
            .limit(10L)
            .toList()
      );
   }

   public static record Summary(
      long totalBytes,
      double bytesPerSecond,
      long counts,
      double countsPerSecond,
      Duration timeSpentInIO,
      List<Pair<String, Long>> topTenContributorsByTotalBytes
   ) {
      public Summary(
         long totalBytes,
         double bytesPerSecond,
         long counts,
         double countsPerSecond,
         Duration timeSpentInIO,
         List<Pair<String, Long>> topTenContributorsByTotalBytes
      ) {
         super();
         this.totalBytes = totalBytes;
         this.bytesPerSecond = bytesPerSecond;
         this.counts = counts;
         this.countsPerSecond = countsPerSecond;
         this.timeSpentInIO = timeSpentInIO;
         this.topTenContributorsByTotalBytes = topTenContributorsByTotalBytes;
      }
   }
}
