package net.minecraft.util.profiling.jfr.stats;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

public final class IoSummary<T> {
   private final CountAndSize totalCountAndSize;
   private final List<Pair<T, CountAndSize>> largestSizeContributors;
   private final Duration recordingDuration;

   public IoSummary(final Duration recordingDuration, final List<Pair<T, CountAndSize>> packetStats) {
      super();
      this.recordingDuration = recordingDuration;
      this.totalCountAndSize = (CountAndSize)packetStats.stream().map(Pair::getSecond).reduce(new CountAndSize(0L, 0L), CountAndSize::add);
      this.largestSizeContributors = packetStats.stream().sorted(Comparator.comparing(Pair::getSecond, IoSummary.CountAndSize.SIZE_THEN_COUNT)).limit(10L).toList();
   }

   public double getCountsPerSecond() {
      return (double)this.totalCountAndSize.totalCount / (double)this.recordingDuration.getSeconds();
   }

   public double getSizePerSecond() {
      return (double)this.totalCountAndSize.totalSize / (double)this.recordingDuration.getSeconds();
   }

   public long getTotalCount() {
      return this.totalCountAndSize.totalCount;
   }

   public long getTotalSize() {
      return this.totalCountAndSize.totalSize;
   }

   public List<Pair<T, CountAndSize>> largestSizeContributors() {
      return this.largestSizeContributors;
   }

   public static record CountAndSize(long totalCount, long totalSize) {
      private static final Comparator<CountAndSize> SIZE_THEN_COUNT = Comparator.comparing(CountAndSize::totalSize).thenComparing(CountAndSize::totalCount).reversed();

      public CountAndSize {
         super();
      }

      public CountAndSize add(final CountAndSize that) {
         return new CountAndSize(this.totalCount + that.totalCount, this.totalSize + that.totalSize);
      }

      public float averageSize() {
         return (float)this.totalSize / (float)this.totalCount;
      }
   }
}
