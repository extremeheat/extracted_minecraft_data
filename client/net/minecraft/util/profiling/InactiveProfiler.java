package net.minecraft.util.profiling;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.util.profiling.metrics.MetricCategory;
import org.jspecify.annotations.Nullable;

public class InactiveProfiler implements ProfileCollector {
   public static final InactiveProfiler INSTANCE = new InactiveProfiler();

   private InactiveProfiler() {
      super();
   }

   public void startTick() {
   }

   public void endTick() {
   }

   public void push(final String name) {
   }

   public void push(final Supplier<String> name) {
   }

   public void markForCharting(final MetricCategory category) {
   }

   public void pop() {
   }

   public void popPush(final String name) {
   }

   public void popPush(final Supplier<String> name) {
   }

   public Zone zone(final String name) {
      return Zone.INACTIVE;
   }

   public Zone zone(final Supplier<String> name) {
      return Zone.INACTIVE;
   }

   public void incrementCounter(final String name, final int amount) {
   }

   public void incrementCounter(final Supplier<String> name, final int amount) {
   }

   public ProfileResults getResults() {
      return EmptyProfileResults.EMPTY;
   }

   public ActiveProfiler.@Nullable PathEntry getEntry(final String path) {
      return null;
   }

   public Set<Pair<String, MetricCategory>> getChartedPaths() {
      return ImmutableSet.of();
   }
}
