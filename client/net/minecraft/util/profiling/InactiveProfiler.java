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

   @Override
   public void startTick() {
   }

   @Override
   public void endTick() {
   }

   @Override
   public void push(String var1) {
   }

   @Override
   public void push(Supplier<String> var1) {
   }

   @Override
   public void markForCharting(MetricCategory var1) {
   }

   @Override
   public void pop() {
   }

   @Override
   public void popPush(String var1) {
   }

   @Override
   public void popPush(Supplier<String> var1) {
   }

   @Override
   public Zone zone(String var1) {
      return Zone.INACTIVE;
   }

   @Override
   public Zone zone(Supplier<String> var1) {
      return Zone.INACTIVE;
   }

   @Override
   public void incrementCounter(String var1, int var2) {
   }

   @Override
   public void incrementCounter(Supplier<String> var1, int var2) {
   }

   @Override
   public ProfileResults getResults() {
      return EmptyProfileResults.EMPTY;
   }

   @Nullable
   @Override
   public ActiveProfiler.PathEntry getEntry(String var1) {
      return null;
   }

   @Override
   public Set<Pair<String, MetricCategory>> getChartedPaths() {
      return ImmutableSet.of();
   }
}
