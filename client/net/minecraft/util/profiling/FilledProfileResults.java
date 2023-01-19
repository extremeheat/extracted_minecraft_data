package net.minecraft.util.profiling;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

public class FilledProfileResults implements ProfileResults {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ProfilerPathEntry EMPTY = new ProfilerPathEntry() {
      @Override
      public long getDuration() {
         return 0L;
      }

      @Override
      public long getMaxDuration() {
         return 0L;
      }

      @Override
      public long getCount() {
         return 0L;
      }

      @Override
      public Object2LongMap<String> getCounters() {
         return Object2LongMaps.emptyMap();
      }
   };
   private static final Splitter SPLITTER = Splitter.on('\u001e');
   private static final Comparator<Entry<String, FilledProfileResults.CounterCollector>> COUNTER_ENTRY_COMPARATOR = Entry.<String, FilledProfileResults.CounterCollector>comparingByValue(
         Comparator.comparingLong(var0 -> var0.totalValue)
      )
      .reversed();
   private final Map<String, ? extends ProfilerPathEntry> entries;
   private final long startTimeNano;
   private final int startTimeTicks;
   private final long endTimeNano;
   private final int endTimeTicks;
   private final int tickDuration;

   public FilledProfileResults(Map<String, ? extends ProfilerPathEntry> var1, long var2, int var4, long var5, int var7) {
      super();
      this.entries = var1;
      this.startTimeNano = var2;
      this.startTimeTicks = var4;
      this.endTimeNano = var5;
      this.endTimeTicks = var7;
      this.tickDuration = var7 - var4;
   }

   private ProfilerPathEntry getEntry(String var1) {
      ProfilerPathEntry var2 = this.entries.get(var1);
      return var2 != null ? var2 : EMPTY;
   }

   @Override
   public List<ResultField> getTimes(String var1) {
      String var2 = var1;
      ProfilerPathEntry var3 = this.getEntry("root");
      long var4 = var3.getDuration();
      ProfilerPathEntry var6 = this.getEntry(var1);
      long var7 = var6.getDuration();
      long var9 = var6.getCount();
      ArrayList var11 = Lists.newArrayList();
      if (!var1.isEmpty()) {
         var1 = var1 + "\u001e";
      }

      long var12 = 0L;

      for(String var15 : this.entries.keySet()) {
         if (isDirectChild(var1, var15)) {
            var12 += this.getEntry(var15).getDuration();
         }
      }

      float var25 = (float)var12;
      if (var12 < var7) {
         var12 = var7;
      }

      if (var4 < var12) {
         var4 = var12;
      }

      for(String var16 : this.entries.keySet()) {
         if (isDirectChild(var1, var16)) {
            ProfilerPathEntry var17 = this.getEntry(var16);
            long var18 = var17.getDuration();
            double var20 = (double)var18 * 100.0 / (double)var12;
            double var22 = (double)var18 * 100.0 / (double)var4;
            String var24 = var16.substring(var1.length());
            var11.add(new ResultField(var24, var20, var22, var17.getCount()));
         }
      }

      if ((float)var12 > var25) {
         var11.add(
            new ResultField("unspecified", (double)((float)var12 - var25) * 100.0 / (double)var12, (double)((float)var12 - var25) * 100.0 / (double)var4, var9)
         );
      }

      Collections.sort(var11);
      var11.add(0, new ResultField(var2, 100.0, (double)var12 * 100.0 / (double)var4, var9));
      return var11;
   }

   private static boolean isDirectChild(String var0, String var1) {
      return var1.length() > var0.length() && var1.startsWith(var0) && var1.indexOf(30, var0.length() + 1) < 0;
   }

   private Map<String, FilledProfileResults.CounterCollector> getCounterValues() {
      TreeMap var1 = Maps.newTreeMap();
      this.entries.forEach((var1x, var2) -> {
         Object2LongMap var3 = var2.getCounters();
         if (!var3.isEmpty()) {
            List var4 = SPLITTER.splitToList(var1x);
            var3.forEach((var2x, var3x) -> var1.computeIfAbsent(var2x, var0xx -> new FilledProfileResults.CounterCollector()).addValue(var4.iterator(), var3x));
         }
      });
      return var1;
   }

   @Override
   public long getStartTimeNano() {
      return this.startTimeNano;
   }

   @Override
   public int getStartTimeTicks() {
      return this.startTimeTicks;
   }

   @Override
   public long getEndTimeNano() {
      return this.endTimeNano;
   }

   @Override
   public int getEndTimeTicks() {
      return this.endTimeTicks;
   }

   @Override
   public boolean saveResults(Path var1) {
      BufferedWriter var2 = null;

      boolean var4;
      try {
         Files.createDirectories(var1.getParent());
         var2 = Files.newBufferedWriter(var1, StandardCharsets.UTF_8);
         var2.write(this.getProfilerResults(this.getNanoDuration(), this.getTickDuration()));
         return true;
      } catch (Throwable var8) {
         LOGGER.error("Could not save profiler results to {}", var1, var8);
         var4 = false;
      } finally {
         IOUtils.closeQuietly(var2);
      }

      return var4;
   }

   protected String getProfilerResults(long var1, int var3) {
      StringBuilder var4 = new StringBuilder();
      var4.append("---- Minecraft Profiler Results ----\n");
      var4.append("// ");
      var4.append(getComment());
      var4.append("\n\n");
      var4.append("Version: ").append(SharedConstants.getCurrentVersion().getId()).append('\n');
      var4.append("Time span: ").append(var1 / 1000000L).append(" ms\n");
      var4.append("Tick span: ").append(var3).append(" ticks\n");
      var4.append("// This is approximately ")
         .append(String.format(Locale.ROOT, "%.2f", (float)var3 / ((float)var1 / 1.0E9F)))
         .append(" ticks per second. It should be ")
         .append(20)
         .append(" ticks per second\n\n");
      var4.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.appendProfilerResults(0, "root", var4);
      var4.append("--- END PROFILE DUMP ---\n\n");
      Map var5 = this.getCounterValues();
      if (!var5.isEmpty()) {
         var4.append("--- BEGIN COUNTER DUMP ---\n\n");
         this.appendCounters(var5, var4, var3);
         var4.append("--- END COUNTER DUMP ---\n\n");
      }

      return var4.toString();
   }

   @Override
   public String getProfilerResults() {
      StringBuilder var1 = new StringBuilder();
      this.appendProfilerResults(0, "root", var1);
      return var1.toString();
   }

   private static StringBuilder indentLine(StringBuilder var0, int var1) {
      var0.append(String.format(Locale.ROOT, "[%02d] ", var1));

      for(int var2 = 0; var2 < var1; ++var2) {
         var0.append("|   ");
      }

      return var0;
   }

   private void appendProfilerResults(int var1, String var2, StringBuilder var3) {
      List var4 = this.getTimes(var2);
      Object2LongMap var5 = ((ProfilerPathEntry)ObjectUtils.firstNonNull(new ProfilerPathEntry[]{this.entries.get(var2), EMPTY})).getCounters();
      var5.forEach(
         (var3x, var4x) -> indentLine(var3, var1)
               .append('#')
               .append(var3x)
               .append(' ')
               .append(var4x)
               .append('/')
               .append(var4x / (long)this.tickDuration)
               .append('\n')
      );
      if (var4.size() >= 3) {
         for(int var6 = 1; var6 < var4.size(); ++var6) {
            ResultField var7 = (ResultField)var4.get(var6);
            indentLine(var3, var1)
               .append(var7.name)
               .append('(')
               .append(var7.count)
               .append('/')
               .append(String.format(Locale.ROOT, "%.0f", (float)var7.count / (float)this.tickDuration))
               .append(')')
               .append(" - ")
               .append(String.format(Locale.ROOT, "%.2f", var7.percentage))
               .append("%/")
               .append(String.format(Locale.ROOT, "%.2f", var7.globalPercentage))
               .append("%\n");
            if (!"unspecified".equals(var7.name)) {
               try {
                  this.appendProfilerResults(var1 + 1, var2 + "\u001e" + var7.name, var3);
               } catch (Exception var9) {
                  var3.append("[[ EXCEPTION ").append(var9).append(" ]]");
               }
            }
         }
      }
   }

   private void appendCounterResults(int var1, String var2, FilledProfileResults.CounterCollector var3, int var4, StringBuilder var5) {
      indentLine(var5, var1)
         .append(var2)
         .append(" total:")
         .append(var3.selfValue)
         .append('/')
         .append(var3.totalValue)
         .append(" average: ")
         .append(var3.selfValue / (long)var4)
         .append('/')
         .append(var3.totalValue / (long)var4)
         .append('\n');
      var3.children
         .entrySet()
         .stream()
         .sorted(COUNTER_ENTRY_COMPARATOR)
         .forEach(var4x -> this.appendCounterResults(var1 + 1, var4x.getKey(), var4x.getValue(), var4, var5));
   }

   private void appendCounters(Map<String, FilledProfileResults.CounterCollector> var1, StringBuilder var2, int var3) {
      var1.forEach((var3x, var4) -> {
         var2.append("-- Counter: ").append(var3x).append(" --\n");
         this.appendCounterResults(0, "root", var4.children.get("root"), var3, var2);
         var2.append("\n\n");
      });
   }

   private static String getComment() {
      String[] var0 = new String[]{
         "I'd Rather Be Surfing",
         "Shiny numbers!",
         "Am I not running fast enough? :(",
         "I'm working as hard as I can!",
         "Will I ever be good enough for you? :(",
         "Speedy. Zoooooom!",
         "Hello world",
         "40% better than a crash report.",
         "Now with extra numbers",
         "Now with less numbers",
         "Now with the same numbers",
         "You should add flames to things, it makes them go faster!",
         "Do you feel the need for... optimization?",
         "*cracks redstone whip*",
         "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."
      };

      try {
         return var0[(int)(Util.getNanos() % (long)var0.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   @Override
   public int getTickDuration() {
      return this.tickDuration;
   }

   static class CounterCollector {
      long selfValue;
      long totalValue;
      final Map<String, FilledProfileResults.CounterCollector> children = Maps.newHashMap();

      CounterCollector() {
         super();
      }

      public void addValue(Iterator<String> var1, long var2) {
         this.totalValue += var2;
         if (!var1.hasNext()) {
            this.selfValue += var2;
         } else {
            this.children.computeIfAbsent((String)var1.next(), var0 -> new FilledProfileResults.CounterCollector()).addValue(var1, var2);
         }
      }
   }
}
