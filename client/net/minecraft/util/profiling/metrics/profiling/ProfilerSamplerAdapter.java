package net.minecraft.util.profiling.metrics.profiling;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import org.apache.commons.lang3.tuple.Pair;

public class ProfilerSamplerAdapter {
   private final Set<String> previouslyFoundSamplerNames = new ObjectOpenHashSet();

   public ProfilerSamplerAdapter() {
      super();
   }

   public Set<MetricSampler> newSamplersFoundInProfiler(Supplier<ProfileCollector> var1) {
      Set var2 = ((ProfileCollector)var1.get())
         .getChartedPaths()
         .stream()
         .filter(var1x -> !this.previouslyFoundSamplerNames.contains(var1x.getLeft()))
         .map(var1x -> samplerForProfilingPath(var1, (String)var1x.getLeft(), (MetricCategory)var1x.getRight()))
         .collect(Collectors.toSet());

      for(MetricSampler var4 : var2) {
         this.previouslyFoundSamplerNames.add(var4.getName());
      }

      return var2;
   }

   private static MetricSampler samplerForProfilingPath(Supplier<ProfileCollector> var0, String var1, MetricCategory var2) {
      return MetricSampler.create(var1, var2, () -> {
         ActiveProfiler.PathEntry var2xx = ((ProfileCollector)var0.get()).getEntry(var1);
         return var2xx == null ? 0.0 : (double)var2xx.getMaxDuration() / (double)TimeUtil.NANOSECONDS_PER_MILLISECOND;
      });
   }
}
