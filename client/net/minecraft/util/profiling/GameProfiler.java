package net.minecraft.util.profiling;

import java.time.Duration;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameProfiler implements ProfilerFiller {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final long MAXIMUM_TICK_TIME_NANOS = Duration.ofMillis(300L).toNanos();
   private final IntSupplier getTickTime;
   private final GameProfiler.ProfilerImpl continuous = new GameProfiler.ProfilerImpl();
   private final GameProfiler.ProfilerImpl perTick = new GameProfiler.ProfilerImpl();

   public GameProfiler(IntSupplier var1) {
      super();
      this.getTickTime = var1;
   }

   public GameProfiler.Profiler continuous() {
      return this.continuous;
   }

   public void startTick() {
      this.continuous.collector.startTick();
      this.perTick.collector.startTick();
   }

   public void endTick() {
      this.continuous.collector.endTick();
      this.perTick.collector.endTick();
   }

   public void push(String var1) {
      this.continuous.collector.push(var1);
      this.perTick.collector.push(var1);
   }

   public void push(Supplier<String> var1) {
      this.continuous.collector.push(var1);
      this.perTick.collector.push(var1);
   }

   public void pop() {
      this.continuous.collector.pop();
      this.perTick.collector.pop();
   }

   public void popPush(String var1) {
      this.continuous.collector.popPush(var1);
      this.perTick.collector.popPush(var1);
   }

   public void popPush(Supplier<String> var1) {
      this.continuous.collector.popPush(var1);
      this.perTick.collector.popPush(var1);
   }

   class ProfilerImpl implements GameProfiler.Profiler {
      protected ProfileCollector collector;

      private ProfilerImpl() {
         super();
         this.collector = InactiveProfiler.INACTIVE;
      }

      public boolean isEnabled() {
         return this.collector != InactiveProfiler.INACTIVE;
      }

      public ProfileResults disable() {
         ProfileResults var1 = this.collector.getResults();
         this.collector = InactiveProfiler.INACTIVE;
         return var1;
      }

      public ProfileResults getResults() {
         return this.collector.getResults();
      }

      public void enable() {
         if (this.collector == InactiveProfiler.INACTIVE) {
            this.collector = new ActiveProfiler(Util.getNanos(), GameProfiler.this.getTickTime);
         }

      }

      // $FF: synthetic method
      ProfilerImpl(Object var2) {
         this();
      }
   }

   public interface Profiler {
      boolean isEnabled();

      ProfileResults disable();

      ProfileResults getResults();

      void enable();
   }
}
