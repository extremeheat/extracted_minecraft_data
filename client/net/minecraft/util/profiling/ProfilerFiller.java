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

   default void addZoneText(String var1) {
   }

   default void addZoneValue(long var1) {
   }

   default void setZoneColor(int var1) {
   }

   default Zone zone(String var1) {
      this.push(var1);
      return new Zone(this);
   }

   default Zone zone(Supplier<String> var1) {
      this.push(var1);
      return new Zone(this);
   }

   void markForCharting(MetricCategory var1);

   default void incrementCounter(String var1) {
      this.incrementCounter((String)var1, 1);
   }

   void incrementCounter(String var1, int var2);

   default void incrementCounter(Supplier<String> var1) {
      this.incrementCounter((Supplier)var1, 1);
   }

   void incrementCounter(Supplier<String> var1, int var2);

   static ProfilerFiller combine(ProfilerFiller var0, ProfilerFiller var1) {
      if (var0 == InactiveProfiler.INSTANCE) {
         return var1;
      } else {
         return (ProfilerFiller)(var1 == InactiveProfiler.INSTANCE ? var0 : new CombinedProfileFiller(var0, var1));
      }
   }

   public static class CombinedProfileFiller implements ProfilerFiller {
      private final ProfilerFiller first;
      private final ProfilerFiller second;

      public CombinedProfileFiller(ProfilerFiller var1, ProfilerFiller var2) {
         super();
         this.first = var1;
         this.second = var2;
      }

      public void startTick() {
         this.first.startTick();
         this.second.startTick();
      }

      public void endTick() {
         this.first.endTick();
         this.second.endTick();
      }

      public void push(String var1) {
         this.first.push(var1);
         this.second.push(var1);
      }

      public void push(Supplier<String> var1) {
         this.first.push(var1);
         this.second.push(var1);
      }

      public void markForCharting(MetricCategory var1) {
         this.first.markForCharting(var1);
         this.second.markForCharting(var1);
      }

      public void pop() {
         this.first.pop();
         this.second.pop();
      }

      public void popPush(String var1) {
         this.first.popPush(var1);
         this.second.popPush(var1);
      }

      public void popPush(Supplier<String> var1) {
         this.first.popPush(var1);
         this.second.popPush(var1);
      }

      public void incrementCounter(String var1, int var2) {
         this.first.incrementCounter(var1, var2);
         this.second.incrementCounter(var1, var2);
      }

      public void incrementCounter(Supplier<String> var1, int var2) {
         this.first.incrementCounter(var1, var2);
         this.second.incrementCounter(var1, var2);
      }

      public void addZoneText(String var1) {
         this.first.addZoneText(var1);
         this.second.addZoneText(var1);
      }

      public void addZoneValue(long var1) {
         this.first.addZoneValue(var1);
         this.second.addZoneValue(var1);
      }

      public void setZoneColor(int var1) {
         this.first.setZoneColor(var1);
         this.second.setZoneColor(var1);
      }
   }
}
