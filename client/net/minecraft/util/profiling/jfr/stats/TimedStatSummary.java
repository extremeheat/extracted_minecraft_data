package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.jfr.Percentiles;

public record TimedStatSummary<T extends TimedStat>(T fastest, T slowest, @Nullable T secondSlowest, int count, Map<Integer, Double> percentilesNanos, Duration totalDuration) {
   public TimedStatSummary(T fastest, T slowest, @Nullable T secondSlowest, int count, Map<Integer, Double> percentilesNanos, Duration totalDuration) {
      super();
      this.fastest = fastest;
      this.slowest = slowest;
      this.secondSlowest = secondSlowest;
      this.count = count;
      this.percentilesNanos = percentilesNanos;
      this.totalDuration = totalDuration;
   }

   public static <T extends TimedStat> TimedStatSummary<T> summary(List<T> var0) {
      if (var0.isEmpty()) {
         throw new IllegalArgumentException("No values");
      } else {
         List var1 = var0.stream().sorted(Comparator.comparing(TimedStat::duration)).toList();
         Duration var2 = (Duration)var1.stream().map(TimedStat::duration).reduce(Duration::plus).orElse(Duration.ZERO);
         TimedStat var3 = (TimedStat)var1.get(0);
         TimedStat var4 = (TimedStat)var1.get(var1.size() - 1);
         TimedStat var5 = var1.size() > 1 ? (TimedStat)var1.get(var1.size() - 2) : null;
         int var6 = var1.size();
         Map var7 = Percentiles.evaluate(var1.stream().mapToLong((var0x) -> {
            return var0x.duration().toNanos();
         }).toArray());
         return new TimedStatSummary(var3, var4, var5, var6, var7, var2);
      }
   }

   public T fastest() {
      return this.fastest;
   }

   public T slowest() {
      return this.slowest;
   }

   @Nullable
   public T secondSlowest() {
      return this.secondSlowest;
   }

   public int count() {
      return this.count;
   }

   public Map<Integer, Double> percentilesNanos() {
      return this.percentilesNanos;
   }

   public Duration totalDuration() {
      return this.totalDuration;
   }
}
