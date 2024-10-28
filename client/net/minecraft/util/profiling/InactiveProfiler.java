package net.minecraft.util.profiling;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.metrics.MetricCategory;
import org.apache.commons.lang3.tuple.Pair;

public class InactiveProfiler implements ProfileCollector {
   public static final InactiveProfiler INSTANCE = new InactiveProfiler();

   private InactiveProfiler() {
      super();
   }

   public void startTick() {
   }

   public void endTick() {
   }

   public void push(String var1) {
   }

   public void push(Supplier<String> var1) {
   }

   public void markForCharting(MetricCategory var1) {
   }

   public void pop() {
   }

   public void popPush(String var1) {
   }

   public void popPush(Supplier<String> var1) {
   }

   public void incrementCounter(String var1, int var2) {
   }

   public void incrementCounter(Supplier<String> var1, int var2) {
   }

   public ProfileResults getResults() {
      return EmptyProfileResults.EMPTY;
   }

   @Nullable
   public ActiveProfiler.PathEntry getEntry(String var1) {
      return null;
   }

   public Set<Pair<String, MetricCategory>> getChartedPaths() {
      return ImmutableSet.of();
   }
}
