package net.minecraft.util.profiling;

import java.util.function.Supplier;
import net.minecraft.util.profiling.metrics.MetricCategory;

public interface ProfilerFiller {
   String ROOT = "root";

   void startTick();

   void endTick();

   void push(String var1);

   void push(Supplier<String> var1);

   void pop();

   void popPush(String var1);

   void popPush(Supplier<String> var1);

   void markForCharting(MetricCategory var1);

   default void incrementCounter(String var1) {
      this.incrementCounter(var1, 1);
   }

   void incrementCounter(String var1, int var2);

   default void incrementCounter(Supplier<String> var1) {
      this.incrementCounter(var1, 1);
   }

   void incrementCounter(Supplier<String> var1, int var2);

   static ProfilerFiller tee(final ProfilerFiller var0, final ProfilerFiller var1) {
      if (var0 == InactiveProfiler.INSTANCE) {
         return var1;
      } else {
         return var1 == InactiveProfiler.INSTANCE ? var0 : new ProfilerFiller() {
            @Override
            public void startTick() {
               var0.startTick();
               var1.startTick();
            }

            @Override
            public void endTick() {
               var0.endTick();
               var1.endTick();
            }

            @Override
            public void push(String var1x) {
               var0.push(var1x);
               var1.push(var1x);
            }

            @Override
            public void push(Supplier<String> var1x) {
               var0.push(var1x);
               var1.push(var1x);
            }

            @Override
            public void markForCharting(MetricCategory var1x) {
               var0.markForCharting(var1x);
               var1.markForCharting(var1x);
            }

            @Override
            public void pop() {
               var0.pop();
               var1.pop();
            }

            @Override
            public void popPush(String var1x) {
               var0.popPush(var1x);
               var1.popPush(var1x);
            }

            @Override
            public void popPush(Supplier<String> var1x) {
               var0.popPush(var1x);
               var1.popPush(var1x);
            }

            @Override
            public void incrementCounter(String var1x, int var2) {
               var0.incrementCounter(var1x, var2);
               var1.incrementCounter(var1x, var2);
            }

            @Override
            public void incrementCounter(Supplier<String> var1x, int var2) {
               var0.incrementCounter(var1x, var2);
               var1.incrementCounter(var1x, var2);
            }
         };
      }
   }
}
