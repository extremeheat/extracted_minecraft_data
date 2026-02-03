package net.minecraft.util.profiling;

import com.mojang.jtracy.Plot;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import java.lang.StackWalker.Option;
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
   private static final StackWalker STACK_WALKER;
   private final List<com.mojang.jtracy.Zone> activeZones = new ArrayList();
   private final Map<String, PlotAndValue> plots = new HashMap();
   private final String name = Thread.currentThread().getName();

   public TracyZoneFiller() {
      super();
   }

   public void startTick() {
   }

   public void endTick() {
      for(PlotAndValue plotAndValue : this.plots.values()) {
         plotAndValue.set(0);
      }

   }

   public void push(final String name) {
      String function = "";
      String file = "";
      int line = 0;
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         Optional<StackWalker.StackFrame> result = (Optional)STACK_WALKER.walk((s) -> s.filter((frame) -> frame.getDeclaringClass() != TracyZoneFiller.class && frame.getDeclaringClass() != ProfilerFiller.CombinedProfileFiller.class).findFirst());
         if (result.isPresent()) {
            StackWalker.StackFrame frame = (StackWalker.StackFrame)result.get();
            function = frame.getMethodName();
            file = frame.getFileName();
            line = frame.getLineNumber();
         }
      }

      com.mojang.jtracy.Zone zone = TracyClient.beginZone(name, function, file, line);
      this.activeZones.add(zone);
   }

   public void push(final Supplier<String> name) {
      this.push((String)name.get());
   }

   public void pop() {
      if (this.activeZones.isEmpty()) {
         LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
      } else {
         com.mojang.jtracy.Zone zone = (com.mojang.jtracy.Zone)this.activeZones.removeLast();
         zone.close();
      }
   }

   public void popPush(final String name) {
      this.pop();
      this.push(name);
   }

   public void popPush(final Supplier<String> name) {
      this.pop();
      this.push((String)name.get());
   }

   public void markForCharting(final MetricCategory category) {
   }

   public void incrementCounter(final String name, final int amount) {
      ((PlotAndValue)this.plots.computeIfAbsent(name, (s) -> new PlotAndValue(this.name + " " + name))).add(amount);
   }

   public void incrementCounter(final Supplier<String> name, final int amount) {
      this.incrementCounter((String)name.get(), amount);
   }

   private com.mojang.jtracy.Zone activeZone() {
      return (com.mojang.jtracy.Zone)this.activeZones.getLast();
   }

   public void addZoneText(final String text) {
      this.activeZone().addText(text);
   }

   public void addZoneValue(final long value) {
      this.activeZone().addValue(value);
   }

   public void setZoneColor(final int color) {
      this.activeZone().setColor(color);
   }

   static {
      STACK_WALKER = StackWalker.getInstance(Set.of(Option.RETAIN_CLASS_REFERENCE), 5);
   }

   private static final class PlotAndValue {
      private final Plot plot;
      private int value;

      private PlotAndValue(final String name) {
         super();
         this.plot = TracyClient.createPlot(name);
         this.value = 0;
      }

      void set(final int value) {
         this.value = value;
         this.plot.setValue((double)value);
      }

      void add(final int amount) {
         this.set(this.value + amount);
      }
   }
}
