package net.minecraft.util.profiling.metrics;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class MetricsRegistry {
   public static final MetricsRegistry INSTANCE = new MetricsRegistry();
   private final WeakHashMap<ProfilerMeasured, Void> measuredInstances = new WeakHashMap<>();

   private MetricsRegistry() {
      super();
   }

   public void add(ProfilerMeasured var1) {
      this.measuredInstances.put(var1, null);
   }

   public List<MetricSampler> getRegisteredSamplers() {
      Map var1 = this.measuredInstances
         .keySet()
         .stream()
         .flatMap(var0 -> var0.profiledMetrics().stream())
         .collect(Collectors.groupingBy(MetricSampler::getName));
      return aggregateDuplicates(var1);
   }

   private static List<MetricSampler> aggregateDuplicates(Map<String, List<MetricSampler>> var0) {
      return var0.entrySet().stream().map(var0x -> {
         String var1 = (String)var0x.getKey();
         List var2 = (List)var0x.getValue();
         return (MetricSampler)(var2.size() > 1 ? new MetricsRegistry.AggregatedMetricSampler(var1, var2) : (MetricSampler)var2.get(0));
      }).collect(Collectors.toList());
   }

   static class AggregatedMetricSampler extends MetricSampler {
      private final List<MetricSampler> delegates;

      AggregatedMetricSampler(String var1, List<MetricSampler> var2) {
         super(var1, ((MetricSampler)var2.get(0)).getCategory(), () -> averageValueFromDelegates(var2), () -> beforeTick(var2), thresholdTest(var2));
         this.delegates = var2;
      }

      private static MetricSampler.ThresholdTest thresholdTest(List<MetricSampler> var0) {
         return var1 -> var0.stream().anyMatch(var2 -> var2.thresholdTest != null ? var2.thresholdTest.test(var1) : false);
      }

      private static void beforeTick(List<MetricSampler> var0) {
         for (MetricSampler var2 : var0) {
            var2.onStartTick();
         }
      }

      private static double averageValueFromDelegates(List<MetricSampler> var0) {
         double var1 = 0.0;

         for (MetricSampler var4 : var0) {
            var1 += var4.getSampler().getAsDouble();
         }

         return var1 / (double)var0.size();
      }

      @Override
      public boolean equals(@Nullable Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 == null || this.getClass() != var1.getClass()) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else {
            MetricsRegistry.AggregatedMetricSampler var2 = (MetricsRegistry.AggregatedMetricSampler)var1;
            return this.delegates.equals(var2.delegates);
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(super.hashCode(), this.delegates);
      }
   }
}
