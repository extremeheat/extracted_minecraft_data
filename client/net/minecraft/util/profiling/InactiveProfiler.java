package net.minecraft.util.profiling;

import java.util.function.Supplier;

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

   public void pop() {
   }

   public void popPush(String var1) {
   }

   public void popPush(Supplier<String> var1) {
   }

   public void incrementCounter(String var1) {
   }

   public void incrementCounter(Supplier<String> var1) {
   }

   public ProfileResults getResults() {
      return EmptyProfileResults.EMPTY;
   }
}
