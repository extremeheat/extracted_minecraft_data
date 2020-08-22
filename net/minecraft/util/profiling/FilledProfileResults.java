package net.minecraft.util.profiling;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FilledProfileResults implements ProfileResults {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map times;
   private final Map counts;
   private final long startTimeNano;
   private final int startTimeTicks;
   private final long endTimeNano;
   private final int endTimeTicks;
   private final int tickDuration;

   public FilledProfileResults(Map var1, Map var2, long var3, int var5, long var6, int var8) {
      this.times = var1;
      this.counts = var2;
      this.startTimeNano = var3;
      this.startTimeTicks = var5;
      this.endTimeNano = var6;
      this.endTimeTicks = var8;
      this.tickDuration = var8 - var5;
   }

   public List getTimes(String var1) {
      long var3 = this.times.containsKey("root") ? (Long)this.times.get("root") : 0L;
      long var5 = (Long)this.times.getOrDefault(var1, -1L);
      long var7 = (Long)this.counts.getOrDefault(var1, 0L);
      ArrayList var9 = Lists.newArrayList();
      if (!var1.isEmpty()) {
         var1 = var1 + '\u001e';
      }

      long var10 = 0L;
      Iterator var12 = this.times.keySet().iterator();

      while(var12.hasNext()) {
         String var13 = (String)var12.next();
         if (var13.length() > var1.length() && var13.startsWith(var1) && var13.indexOf(30, var1.length() + 1) < 0) {
            var10 += (Long)this.times.get(var13);
         }
      }

      float var25 = (float)var10;
      if (var10 < var5) {
         var10 = var5;
      }

      if (var3 < var10) {
         var3 = var10;
      }

      HashSet var26 = Sets.newHashSet(this.times.keySet());
      var26.addAll(this.counts.keySet());
      Iterator var14 = var26.iterator();

      String var15;
      while(var14.hasNext()) {
         var15 = (String)var14.next();
         if (var15.length() > var1.length() && var15.startsWith(var1) && var15.indexOf(30, var1.length() + 1) < 0) {
            long var16 = (Long)this.times.getOrDefault(var15, 0L);
            double var18 = (double)var16 * 100.0D / (double)var10;
            double var20 = (double)var16 * 100.0D / (double)var3;
            String var22 = var15.substring(var1.length());
            long var23 = (Long)this.counts.getOrDefault(var15, 0L);
            var9.add(new ResultField(var22, var18, var20, var23));
         }
      }

      var14 = this.times.keySet().iterator();

      while(var14.hasNext()) {
         var15 = (String)var14.next();
         this.times.put(var15, (Long)this.times.get(var15) * 999L / 1000L);
      }

      if ((float)var10 > var25) {
         var9.add(new ResultField("unspecified", (double)((float)var10 - var25) * 100.0D / (double)var10, (double)((float)var10 - var25) * 100.0D / (double)var3, var7));
      }

      Collections.sort(var9);
      var9.add(0, new ResultField(var1, 100.0D, (double)var10 * 100.0D / (double)var3, var7));
      return var9;
   }

   public long getStartTimeNano() {
      return this.startTimeNano;
   }

   public int getStartTimeTicks() {
      return this.startTimeTicks;
   }

   public long getEndTimeNano() {
      return this.endTimeNano;
   }

   public int getEndTimeTicks() {
      return this.endTimeTicks;
   }

   public boolean saveResults(File var1) {
      var1.getParentFile().mkdirs();
      OutputStreamWriter var2 = null;

      boolean var4;
      try {
         var2 = new OutputStreamWriter(new FileOutputStream(var1), StandardCharsets.UTF_8);
         var2.write(this.getProfilerResults(this.getNanoDuration(), this.getTickDuration()));
         boolean var3 = true;
         return var3;
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
      var4.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", (float)var3 / ((float)var1 / 1.0E9F))).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
      var4.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.appendProfilerResults(0, "root", var4);
      var4.append("--- END PROFILE DUMP ---\n\n");
      return var4.toString();
   }

   private void appendProfilerResults(int var1, String var2, StringBuilder var3) {
      List var4 = this.getTimes(var2);
      if (var4.size() >= 3) {
         for(int var5 = 1; var5 < var4.size(); ++var5) {
            ResultField var6 = (ResultField)var4.get(var5);
            var3.append(String.format("[%02d] ", var1));

            for(int var7 = 0; var7 < var1; ++var7) {
               var3.append("|   ");
            }

            var3.append(var6.name).append('(').append(var6.count).append('/').append(String.format(Locale.ROOT, "%.0f", (float)var6.count / (float)this.tickDuration)).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", var6.percentage)).append("%/").append(String.format(Locale.ROOT, "%.2f", var6.globalPercentage)).append("%\n");
            if (!"unspecified".equals(var6.name)) {
               try {
                  this.appendProfilerResults(var1 + 1, var2 + '\u001e' + var6.name, var3);
               } catch (Exception var8) {
                  var3.append("[[ EXCEPTION ").append(var8).append(" ]]");
               }
            }
         }

      }
   }

   private static String getComment() {
      String[] var0 = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

      try {
         return var0[(int)(Util.getNanos() % (long)var0.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   public int getTickDuration() {
      return this.tickDuration;
   }
}
