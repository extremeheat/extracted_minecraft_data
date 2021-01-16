package org.apache.logging.log4j.core.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Clock;
import org.apache.logging.log4j.core.util.ClockFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "TimeFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
@PerformanceSensitive({"allocation"})
public final class TimeFilter extends AbstractFilter {
   private static final Clock CLOCK = ClockFactory.getClock();
   private static final long HOUR_MS = 3600000L;
   private static final long MINUTE_MS = 60000L;
   private static final long SECOND_MS = 1000L;
   private final long start;
   private final long end;
   private final TimeZone timezone;
   private long midnightToday;
   private long midnightTomorrow;

   private TimeFilter(long var1, long var3, TimeZone var5, Filter.Result var6, Filter.Result var7) {
      super(var6, var7);
      this.start = var1;
      this.end = var3;
      this.timezone = var5;
      this.initMidnight(var1);
   }

   void initMidnight(long var1) {
      Calendar var3 = Calendar.getInstance(this.timezone);
      var3.setTimeInMillis(var1);
      var3.set(11, 0);
      var3.set(12, 0);
      var3.set(13, 0);
      var3.set(14, 0);
      this.midnightToday = var3.getTimeInMillis();
      var3.add(5, 1);
      this.midnightTomorrow = var3.getTimeInMillis();
   }

   Filter.Result filter(long var1) {
      if (var1 >= this.midnightTomorrow || var1 < this.midnightToday) {
         this.initMidnight(var1);
      }

      return var1 >= this.midnightToday + this.start && var1 <= this.midnightToday + this.end ? this.onMatch : this.onMismatch;
   }

   public Filter.Result filter(LogEvent var1) {
      return this.filter(var1.getTimeMillis());
   }

   private Filter.Result filter() {
      return this.filter(CLOCK.currentTimeMillis());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      return this.filter();
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5) {
      return this.filter();
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5) {
      return this.filter();
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5) {
      return this.filter();
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      return this.filter();
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      return this.filter();
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      return this.filter();
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.filter();
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.filter();
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      return this.filter();
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      return this.filter();
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13) {
      return this.filter();
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13, Object var14) {
      return this.filter();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("start=").append(this.start);
      var1.append(", end=").append(this.end);
      var1.append(", timezone=").append(this.timezone.toString());
      return var1.toString();
   }

   @PluginFactory
   public static TimeFilter createFilter(@PluginAttribute("start") String var0, @PluginAttribute("end") String var1, @PluginAttribute("timezone") String var2, @PluginAttribute("onMatch") Filter.Result var3, @PluginAttribute("onMismatch") Filter.Result var4) {
      long var5 = parseTimestamp(var0, 0L);
      long var7 = parseTimestamp(var1, 9223372036854775807L);
      TimeZone var9 = var2 == null ? TimeZone.getDefault() : TimeZone.getTimeZone(var2);
      Filter.Result var10 = var3 == null ? Filter.Result.NEUTRAL : var3;
      Filter.Result var11 = var4 == null ? Filter.Result.DENY : var4;
      return new TimeFilter(var5, var7, var9, var10, var11);
   }

   private static long parseTimestamp(String var0, long var1) {
      if (var0 == null) {
         return var1;
      } else {
         SimpleDateFormat var3 = new SimpleDateFormat("HH:mm:ss");
         var3.setTimeZone(TimeZone.getTimeZone("UTC"));

         try {
            return var3.parse(var0).getTime();
         } catch (ParseException var5) {
            LOGGER.warn((String)"Error parsing TimeFilter timestamp value {}", (Object)var0, (Object)var5);
            return var1;
         }
      }
   }
}
