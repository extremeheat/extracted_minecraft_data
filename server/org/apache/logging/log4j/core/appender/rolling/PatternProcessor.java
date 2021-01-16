package org.apache.logging.log4j.core.appender.rolling;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.pattern.ArrayPatternConverter;
import org.apache.logging.log4j.core.pattern.DatePatternConverter;
import org.apache.logging.log4j.core.pattern.FormattingInfo;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.status.StatusLogger;

public class PatternProcessor {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   private static final String KEY = "FileConverter";
   private static final char YEAR_CHAR = 'y';
   private static final char MONTH_CHAR = 'M';
   private static final char[] WEEK_CHARS = new char[]{'w', 'W'};
   private static final char[] DAY_CHARS = new char[]{'D', 'd', 'F', 'E'};
   private static final char[] HOUR_CHARS = new char[]{'H', 'K', 'h', 'k'};
   private static final char MINUTE_CHAR = 'm';
   private static final char SECOND_CHAR = 's';
   private static final char MILLIS_CHAR = 'S';
   private final ArrayPatternConverter[] patternConverters;
   private final FormattingInfo[] patternFields;
   private long prevFileTime = 0L;
   private long nextFileTime = 0L;
   private long currentFileTime = 0L;
   private RolloverFrequency frequency = null;
   private final String pattern;

   public String getPattern() {
      return this.pattern;
   }

   public String toString() {
      return this.pattern;
   }

   public PatternProcessor(String var1) {
      super();
      this.pattern = var1;
      PatternParser var2 = this.createPatternParser();
      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList();
      var2.parse(var1, var3, var4, false, false, false);
      FormattingInfo[] var5 = new FormattingInfo[var4.size()];
      this.patternFields = (FormattingInfo[])var4.toArray(var5);
      ArrayPatternConverter[] var6 = new ArrayPatternConverter[var3.size()];
      this.patternConverters = (ArrayPatternConverter[])var3.toArray(var6);
      ArrayPatternConverter[] var7 = this.patternConverters;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         ArrayPatternConverter var10 = var7[var9];
         if (var10 instanceof DatePatternConverter) {
            DatePatternConverter var11 = (DatePatternConverter)var10;
            this.frequency = this.calculateFrequency(var11.getPattern());
         }
      }

   }

   public long getCurrentFileTime() {
      return this.currentFileTime;
   }

   public void setCurrentFileTime(long var1) {
      this.currentFileTime = var1;
   }

   public long getPrevFileTime() {
      return this.prevFileTime;
   }

   public void setPrevFileTime(long var1) {
      LOGGER.debug((String)"Setting prev file time to {}", (Object)(new Date(var1)));
      this.prevFileTime = var1;
   }

   public long getNextTime(long var1, int var3, boolean var4) {
      this.prevFileTime = this.nextFileTime;
      if (this.frequency == null) {
         throw new IllegalStateException("Pattern does not contain a date");
      } else {
         Calendar var7 = Calendar.getInstance();
         var7.setTimeInMillis(var1);
         Calendar var8 = Calendar.getInstance();
         var7.setMinimalDaysInFirstWeek(7);
         var8.setMinimalDaysInFirstWeek(7);
         var8.set(var7.get(1), 0, 1, 0, 0, 0);
         var8.set(14, 0);
         long var5;
         if (this.frequency == RolloverFrequency.ANNUALLY) {
            this.increment(var8, 1, var3, var4);
            var5 = var8.getTimeInMillis();
            var8.add(1, -1);
            this.nextFileTime = var8.getTimeInMillis();
            return this.debugGetNextTime(var5);
         } else {
            var8.set(2, var7.get(2));
            if (this.frequency == RolloverFrequency.MONTHLY) {
               this.increment(var8, 2, var3, var4);
               var5 = var8.getTimeInMillis();
               var8.add(2, -1);
               this.nextFileTime = var8.getTimeInMillis();
               return this.debugGetNextTime(var5);
            } else if (this.frequency == RolloverFrequency.WEEKLY) {
               var8.set(3, var7.get(3));
               this.increment(var8, 3, var3, var4);
               var8.set(7, var7.getFirstDayOfWeek());
               var5 = var8.getTimeInMillis();
               var8.add(3, -1);
               this.nextFileTime = var8.getTimeInMillis();
               return this.debugGetNextTime(var5);
            } else {
               var8.set(6, var7.get(6));
               if (this.frequency == RolloverFrequency.DAILY) {
                  this.increment(var8, 6, var3, var4);
                  var5 = var8.getTimeInMillis();
                  var8.add(6, -1);
                  this.nextFileTime = var8.getTimeInMillis();
                  return this.debugGetNextTime(var5);
               } else {
                  var8.set(11, var7.get(11));
                  if (this.frequency == RolloverFrequency.HOURLY) {
                     this.increment(var8, 11, var3, var4);
                     var5 = var8.getTimeInMillis();
                     var8.add(11, -1);
                     this.nextFileTime = var8.getTimeInMillis();
                     return this.debugGetNextTime(var5);
                  } else {
                     var8.set(12, var7.get(12));
                     if (this.frequency == RolloverFrequency.EVERY_MINUTE) {
                        this.increment(var8, 12, var3, var4);
                        var5 = var8.getTimeInMillis();
                        var8.add(12, -1);
                        this.nextFileTime = var8.getTimeInMillis();
                        return this.debugGetNextTime(var5);
                     } else {
                        var8.set(13, var7.get(13));
                        if (this.frequency == RolloverFrequency.EVERY_SECOND) {
                           this.increment(var8, 13, var3, var4);
                           var5 = var8.getTimeInMillis();
                           var8.add(13, -1);
                           this.nextFileTime = var8.getTimeInMillis();
                           return this.debugGetNextTime(var5);
                        } else {
                           var8.set(14, var7.get(14));
                           this.increment(var8, 14, var3, var4);
                           var5 = var8.getTimeInMillis();
                           var8.add(14, -1);
                           this.nextFileTime = var8.getTimeInMillis();
                           return this.debugGetNextTime(var5);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void updateTime() {
      this.prevFileTime = this.nextFileTime;
   }

   private long debugGetNextTime(long var1) {
      if (LOGGER.isTraceEnabled()) {
         LOGGER.trace((String)"PatternProcessor.getNextTime returning {}, nextFileTime={}, prevFileTime={}, current={}, freq={}", (Object)this.format(var1), this.format(this.nextFileTime), this.format(this.prevFileTime), this.format(System.currentTimeMillis()), this.frequency);
      }

      return var1;
   }

   private String format(long var1) {
      return (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss.SSS")).format(new Date(var1));
   }

   private void increment(Calendar var1, int var2, int var3, boolean var4) {
      int var5 = var4 ? var3 - var1.get(var2) % var3 : var3;
      var1.add(var2, var5);
   }

   public final void formatFileName(StringBuilder var1, boolean var2, Object var3) {
      long var4 = var2 ? this.currentFileTime : this.prevFileTime;
      if (var4 == 0L) {
         var4 = System.currentTimeMillis();
      }

      this.formatFileName(var1, new Date(var4), var3);
   }

   public final void formatFileName(StrSubstitutor var1, StringBuilder var2, Object var3) {
      this.formatFileName(var1, var2, false, var3);
   }

   public final void formatFileName(StrSubstitutor var1, StringBuilder var2, boolean var3, Object var4) {
      long var5 = var3 && this.currentFileTime != 0L ? this.currentFileTime : (this.prevFileTime != 0L ? this.prevFileTime : System.currentTimeMillis());
      this.formatFileName(var2, new Date(var5), var4);
      Log4jLogEvent var7 = (new Log4jLogEvent.Builder()).setTimeMillis(var5).build();
      String var8 = var1.replace((LogEvent)var7, (StringBuilder)var2);
      var2.setLength(0);
      var2.append(var8);
   }

   protected final void formatFileName(StringBuilder var1, Object... var2) {
      for(int var3 = 0; var3 < this.patternConverters.length; ++var3) {
         int var4 = var1.length();
         this.patternConverters[var3].format(var1, var2);
         if (this.patternFields[var3] != null) {
            this.patternFields[var3].format(var4, var1);
         }
      }

   }

   private RolloverFrequency calculateFrequency(String var1) {
      if (this.patternContains(var1, 'S')) {
         return RolloverFrequency.EVERY_MILLISECOND;
      } else if (this.patternContains(var1, 's')) {
         return RolloverFrequency.EVERY_SECOND;
      } else if (this.patternContains(var1, 'm')) {
         return RolloverFrequency.EVERY_MINUTE;
      } else if (this.patternContains(var1, HOUR_CHARS)) {
         return RolloverFrequency.HOURLY;
      } else if (this.patternContains(var1, DAY_CHARS)) {
         return RolloverFrequency.DAILY;
      } else if (this.patternContains(var1, WEEK_CHARS)) {
         return RolloverFrequency.WEEKLY;
      } else if (this.patternContains(var1, 'M')) {
         return RolloverFrequency.MONTHLY;
      } else {
         return this.patternContains(var1, 'y') ? RolloverFrequency.ANNUALLY : null;
      }
   }

   private PatternParser createPatternParser() {
      return new PatternParser((Configuration)null, "FileConverter", (Class)null);
   }

   private boolean patternContains(String var1, char... var2) {
      char[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         char var6 = var3[var5];
         if (this.patternContains(var1, var6)) {
            return true;
         }
      }

      return false;
   }

   private boolean patternContains(String var1, char var2) {
      return var1.indexOf(var2) >= 0;
   }

   public RolloverFrequency getFrequency() {
      return this.frequency;
   }

   public long getNextFileTime() {
      return this.nextFileTime;
   }
}
