package net.minecraft.util.profiling.jfr.stats;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

public final class IoSummary<T> {
   private final CountAndSize totalCountAndSize;
   private final List<Pair<T, CountAndSize>> largestSizeContributors;
   private final Duration recordingDuration;

   public IoSummary(Duration var1, List<Pair<T, CountAndSize>> var2) {
      super();
      this.recordingDuration = var1;
      this.totalCountAndSize = (CountAndSize)var2.stream().map(Pair::getSecond).reduce(new CountAndSize(0L, 0L), CountAndSize::add);
      this.largestSizeContributors = var2.stream().sorted(Comparator.comparing(Pair::getSecond, IoSummary.CountAndSize.SIZE_THEN_COUNT)).limit(10L).toList();
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
      final long totalCount;
      final long totalSize;
      static final Comparator<CountAndSize> SIZE_THEN_COUNT = Comparator.comparing(CountAndSize::totalSize).thenComparing(CountAndSize::totalCount).reversed();

      public CountAndSize(long var1, long var3) {
         super();
         this.totalCount = var1;
         this.totalSize = var3;
      }

      CountAndSize add(CountAndSize var1) {
         return new CountAndSize(this.totalCount + var1.totalCount, this.totalSize + var1.totalSize);
      }

      public float averageSize() {
         return (float)this.totalSize / (float)this.totalCount;
      }
   }
}
