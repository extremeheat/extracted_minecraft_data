package org.apache.logging.log4j.core.util.datetime;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

public class FixedDateFormat {
   private final FixedDateFormat.FixedFormat fixedFormat;
   private final TimeZone timeZone;
   private final int length;
   private final FastDateFormat fastDateFormat;
   private final char timeSeparatorChar;
   private final char millisSeparatorChar;
   private final int timeSeparatorLength;
   private final int millisSeparatorLength;
   private volatile long midnightToday = 0L;
   private volatile long midnightTomorrow = 0L;
   private char[] cachedDate;
   private int dateLength;

   FixedDateFormat(FixedDateFormat.FixedFormat var1, TimeZone var2) {
      super();
      this.fixedFormat = (FixedDateFormat.FixedFormat)Objects.requireNonNull(var1);
      this.timeZone = (TimeZone)Objects.requireNonNull(var2);
      this.timeSeparatorChar = var1.timeSeparatorChar;
      this.timeSeparatorLength = var1.timeSeparatorLength;
      this.millisSeparatorChar = var1.millisSeparatorChar;
      this.millisSeparatorLength = var1.millisSeparatorLength;
      this.length = var1.getLength();
      this.fastDateFormat = var1.getFastDateFormat(var2);
   }

   public static FixedDateFormat createIfSupported(String... var0) {
      if (var0 != null && var0.length != 0 && var0[0] != null) {
         TimeZone var1;
         if (var0.length > 1) {
            if (var0[1] != null) {
               var1 = TimeZone.getTimeZone(var0[1]);
            } else {
               var1 = TimeZone.getDefault();
            }
         } else {
            if (var0.length > 2) {
               return null;
            }

            var1 = TimeZone.getDefault();
         }

         FixedDateFormat.FixedFormat var2 = FixedDateFormat.FixedFormat.lookup(var0[0]);
         return var2 == null ? null : new FixedDateFormat(var2, var1);
      } else {
         return new FixedDateFormat(FixedDateFormat.FixedFormat.DEFAULT, TimeZone.getDefault());
      }
   }

   public static FixedDateFormat create(FixedDateFormat.FixedFormat var0) {
      return new FixedDateFormat(var0, TimeZone.getDefault());
   }

   public static FixedDateFormat create(FixedDateFormat.FixedFormat var0, TimeZone var1) {
      return new FixedDateFormat(var0, var1 != null ? var1 : TimeZone.getDefault());
   }

   public String getFormat() {
      return this.fixedFormat.getPattern();
   }

   public TimeZone getTimeZone() {
      return this.timeZone;
   }

   public long millisSinceMidnight(long var1) {
      if (var1 >= this.midnightTomorrow || var1 < this.midnightToday) {
         this.updateMidnightMillis(var1);
      }

      return var1 - this.midnightToday;
   }

   private void updateMidnightMillis(long var1) {
      if (var1 >= this.midnightTomorrow || var1 < this.midnightToday) {
         synchronized(this) {
            this.updateCachedDate(var1);
            this.midnightToday = this.calcMidnightMillis(var1, 0);
            this.midnightTomorrow = this.calcMidnightMillis(var1, 1);
         }
      }

   }

   private long calcMidnightMillis(long var1, int var3) {
      Calendar var4 = Calendar.getInstance(this.timeZone);
      var4.setTimeInMillis(var1);
      var4.set(11, 0);
      var4.set(12, 0);
      var4.set(13, 0);
      var4.set(14, 0);
      var4.add(5, var3);
      return var4.getTimeInMillis();
   }

   private void updateCachedDate(long var1) {
      if (this.fastDateFormat != null) {
         StringBuilder var3 = (StringBuilder)this.fastDateFormat.format(var1, new StringBuilder());
         this.cachedDate = var3.toString().toCharArray();
         this.dateLength = var3.length();
      }

   }

   public String format(long var1) {
      char[] var3 = new char[this.length << 1];
      int var4 = this.format(var1, var3, 0);
      return new String(var3, 0, var4);
   }

   public int format(long var1, char[] var3, int var4) {
      int var5 = (int)this.millisSinceMidnight(var1);
      this.writeDate(var3, var4);
      return this.writeTime(var5, var3, var4 + this.dateLength) - var4;
   }

   private void writeDate(char[] var1, int var2) {
      if (this.cachedDate != null) {
         System.arraycopy(this.cachedDate, 0, var1, var2, this.dateLength);
      }

   }

   private int writeTime(int var1, char[] var2, int var3) {
      int var4 = var1 / 3600000;
      var1 -= 3600000 * var4;
      int var5 = var1 / '\uea60';
      var1 -= '\uea60' * var5;
      int var6 = var1 / 1000;
      var1 -= 1000 * var6;
      int var7 = var4 / 10;
      var2[var3++] = (char)(var7 + 48);
      var2[var3++] = (char)(var4 - 10 * var7 + 48);
      var2[var3] = this.timeSeparatorChar;
      var3 += this.timeSeparatorLength;
      var7 = var5 / 10;
      var2[var3++] = (char)(var7 + 48);
      var2[var3++] = (char)(var5 - 10 * var7 + 48);
      var2[var3] = this.timeSeparatorChar;
      var3 += this.timeSeparatorLength;
      var7 = var6 / 10;
      var2[var3++] = (char)(var7 + 48);
      var2[var3++] = (char)(var6 - 10 * var7 + 48);
      var2[var3] = this.millisSeparatorChar;
      var3 += this.millisSeparatorLength;
      var7 = var1 / 100;
      var2[var3++] = (char)(var7 + 48);
      var1 -= 100 * var7;
      var7 = var1 / 10;
      var2[var3++] = (char)(var7 + 48);
      var1 -= 10 * var7;
      var2[var3++] = (char)(var1 + 48);
      return var3;
   }

   public static enum FixedFormat {
      ABSOLUTE("HH:mm:ss,SSS", (String)null, 0, ':', 1, ',', 1),
      ABSOLUTE_PERIOD("HH:mm:ss.SSS", (String)null, 0, ':', 1, '.', 1),
      COMPACT("yyyyMMddHHmmssSSS", "yyyyMMdd", 0, ' ', 0, ' ', 0),
      DATE("dd MMM yyyy HH:mm:ss,SSS", "dd MMM yyyy ", 0, ':', 1, ',', 1),
      DATE_PERIOD("dd MMM yyyy HH:mm:ss.SSS", "dd MMM yyyy ", 0, ':', 1, '.', 1),
      DEFAULT("yyyy-MM-dd HH:mm:ss,SSS", "yyyy-MM-dd ", 0, ':', 1, ',', 1),
      DEFAULT_PERIOD("yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd ", 0, ':', 1, '.', 1),
      ISO8601_BASIC("yyyyMMdd'T'HHmmss,SSS", "yyyyMMdd'T'", 2, ' ', 0, ',', 1),
      ISO8601_BASIC_PERIOD("yyyyMMdd'T'HHmmss.SSS", "yyyyMMdd'T'", 2, ' ', 0, '.', 1),
      ISO8601("yyyy-MM-dd'T'HH:mm:ss,SSS", "yyyy-MM-dd'T'", 2, ':', 1, ',', 1),
      ISO8601_PERIOD("yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'", 2, ':', 1, '.', 1);

      private final String pattern;
      private final String datePattern;
      private final int escapeCount;
      private final char timeSeparatorChar;
      private final int timeSeparatorLength;
      private final char millisSeparatorChar;
      private final int millisSeparatorLength;

      private FixedFormat(String var3, String var4, int var5, char var6, int var7, char var8, int var9) {
         this.timeSeparatorChar = var6;
         this.timeSeparatorLength = var7;
         this.millisSeparatorChar = var8;
         this.millisSeparatorLength = var9;
         this.pattern = (String)Objects.requireNonNull(var3);
         this.datePattern = var4;
         this.escapeCount = var5;
      }

      public String getPattern() {
         return this.pattern;
      }

      public String getDatePattern() {
         return this.datePattern;
      }

      public static FixedDateFormat.FixedFormat lookup(String var0) {
         FixedDateFormat.FixedFormat[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            FixedDateFormat.FixedFormat var4 = var1[var3];
            if (var4.name().equals(var0) || var4.getPattern().equals(var0)) {
               return var4;
            }
         }

         return null;
      }

      public int getLength() {
         return this.pattern.length() - this.escapeCount;
      }

      public int getDatePatternLength() {
         return this.getDatePattern() == null ? 0 : this.getDatePattern().length() - this.escapeCount;
      }

      public FastDateFormat getFastDateFormat() {
         return this.getFastDateFormat((TimeZone)null);
      }

      public FastDateFormat getFastDateFormat(TimeZone var1) {
         return this.getDatePattern() == null ? null : FastDateFormat.getInstance(this.getDatePattern(), var1);
      }
   }
}
