package net.minecraft.util.profiling.jfr.stats;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

public record FileIOStat(Duration duration, @Nullable String path, long bytes) {
   public FileIOStat {
      super();
   }

   public static Summary summary(final Duration recordingDuration, final List<FileIOStat> ioStats) {
      long totalBytes = ioStats.stream().mapToLong((it) -> it.bytes).sum();
      return new Summary(totalBytes, (double)totalBytes / (double)recordingDuration.getSeconds(), (long)ioStats.size(), (double)ioStats.size() / (double)recordingDuration.getSeconds(), (Duration)ioStats.stream().map(FileIOStat::duration).reduce(Duration.ZERO, Duration::plus), ((Map)ioStats.stream().filter((it) -> it.path != null).collect(Collectors.groupingBy((stat) -> stat.path, Collectors.summingLong((it) -> it.bytes)))).entrySet().stream().sorted(Entry.comparingByValue().reversed()).map((e) -> Pair.of((String)e.getKey(), (Long)e.getValue())).limit(10L).toList());
   }

   public static record Summary(long totalBytes, double bytesPerSecond, long counts, double countsPerSecond, Duration timeSpentInIO, List<Pair<String, Long>> topTenContributorsByTotalBytes) {
      public Summary {
         super();
      }
   }
}
