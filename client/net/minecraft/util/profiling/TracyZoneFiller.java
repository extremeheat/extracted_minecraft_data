package net.minecraft.util.profiling;

import com.mojang.jtracy.Plot;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.util.profiling.metrics.MetricCategory;
import org.slf4j.Logger;

public class TracyZoneFiller implements ProfilerFiller {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final StackWalker STACK_WALKER = StackWalker.getInstance(Set.of(Option.RETAIN_CLASS_REFERENCE), 5);
   private final List<com.mojang.jtracy.Zone> activeZones = new ArrayList<>();
   private final Map<String, TracyZoneFiller.PlotAndValue> plots = new HashMap<>();
   private final String name = Thread.currentThread().getName();

   public TracyZoneFiller() {
      super();
   }

   @Override
   public void startTick() {
   }

   @Override
   public void endTick() {
      for (TracyZoneFiller.PlotAndValue var2 : this.plots.values()) {
         var2.set(0);
      }
   }

   @Override
   public void push(String var1) {
      String var2 = "";
      String var3 = "";
      int var4 = 0;
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         Optional var5 = STACK_WALKER.walk(
            var0 -> var0.filter(
                     var0x -> var0x.getDeclaringClass() != TracyZoneFiller.class && var0x.getDeclaringClass() != ProfilerFiller.CombinedProfileFiller.class
                  )
                  .findFirst()
         );
         if (var5.isPresent()) {
            StackFrame var6 = (StackFrame)var5.get();
            var2 = var6.getMethodName();
            var3 = var6.getFileName();
            var4 = var6.getLineNumber();
         }
      }

      com.mojang.jtracy.Zone var7 = TracyClient.beginZone(var1, var2, var3, var4);
      this.activeZones.add(var7);
   }

   @Override
   public void push(Supplier<String> var1) {
      this.push((String)var1.get());
   }

   @Override
   public void pop() {
      if (this.activeZones.isEmpty()) {
         LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
      } else {
         com.mojang.jtracy.Zone var1 = (com.mojang.jtracy.Zone)this.activeZones.removeLast();
         var1.close();
      }
   }

   @Override
   public void popPush(String var1) {
      this.pop();
      this.push(var1);
   }

   @Override
   public void popPush(Supplier<String> var1) {
      this.pop();
      this.push((String)var1.get());
   }

   @Override
   public void markForCharting(MetricCategory var1) {
   }

   @Override
   public void incrementCounter(String var1, int var2) {
      this.plots.computeIfAbsent(var1, var2x -> new TracyZoneFiller.PlotAndValue(this.name + " " + var1)).add(var2);
   }

   @Override
   public void incrementCounter(Supplier<String> var1, int var2) {
      this.incrementCounter((String)var1.get(), var2);
   }

   private com.mojang.jtracy.Zone activeZone() {
      return (com.mojang.jtracy.Zone)this.activeZones.getLast();
   }

   @Override
   public void addZoneText(String var1) {
      this.activeZone().addText(var1);
   }

   @Override
   public void addZoneValue(long var1) {
      this.activeZone().addValue(var1);
   }

   @Override
   public void setZoneColor(int var1) {
      this.activeZone().setColor(var1);
   }

   static final class PlotAndValue {
      private final Plot plot;
      private int value;

      PlotAndValue(String var1) {
         super();
         this.plot = TracyClient.createPlot(var1);
         this.value = 0;
      }

      void set(int var1) {
         this.value = var1;
         this.plot.setValue((double)var1);
      }

      void add(int var1) {
         this.set(this.value + var1);
      }
   }
}
