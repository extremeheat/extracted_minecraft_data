package net.minecraft.util.profiling.metrics.profiling;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;

public class ProfilerSamplerAdapter {
   private final Set<String> previouslyFoundSamplerNames = new ObjectOpenHashSet();

   public ProfilerSamplerAdapter() {
      super();
   }

   public Set<MetricSampler> newSamplersFoundInProfiler(Supplier<ProfileCollector> var1) {
      Set var2 = (Set)((ProfileCollector)var1.get()).getChartedPaths().stream().filter((var1x) -> {
         return !this.previouslyFoundSamplerNames.contains(var1x.getLeft());
      }).map((var1x) -> {
         return samplerForProfilingPath(var1, (String)var1x.getLeft(), (MetricCategory)var1x.getRight());
      }).collect(Collectors.toSet());
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         MetricSampler var4 = (MetricSampler)var3.next();
         this.previouslyFoundSamplerNames.add(var4.getName());
      }

      return var2;
   }

   private static MetricSampler samplerForProfilingPath(Supplier<ProfileCollector> var0, String var1, MetricCategory var2) {
      return MetricSampler.create(var1, var2, () -> {
         ActiveProfiler.PathEntry var2 = ((ProfileCollector)var0.get()).getEntry(var1);
         return var2 == null ? 0.0D : (double)var2.getMaxDuration() / (double)TimeUtil.NANOSECONDS_PER_MILLISECOND;
      });
   }
}
