package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Duration implements Serializable, Comparable<Duration> {
   private static final long serialVersionUID = -3756810052716342061L;
   public static final Duration ZERO = new Duration(0L);
   private static final int HOURS_PER_DAY = 24;
   private static final int MINUTES_PER_HOUR = 60;
   private static final int SECONDS_PER_MINUTE = 60;
   private static final int SECONDS_PER_HOUR = 3600;
   private static final int SECONDS_PER_DAY = 86400;
   private static final Pattern PATTERN = Pattern.compile("P?(?:([0-9]+)D)?(T?(?:([0-9]+)H)?(?:([0-9]+)M)?(?:([0-9]+)?S)?)?", 2);
   private final long seconds;

   private Duration(long var1) {
      super();
      this.seconds = var1;
   }

   public static Duration parse(CharSequence var0) {
      Objects.requireNonNull(var0, "text");
      Matcher var1 = PATTERN.matcher(var0);
      if (var1.matches() && !"T".equals(var1.group(2))) {
         String var2 = var1.group(1);
         String var3 = var1.group(3);
         String var4 = var1.group(4);
         String var5 = var1.group(5);
         if (var2 != null || var3 != null || var4 != null || var5 != null) {
            long var6 = parseNumber(var0, var2, 86400, "days");
            long var8 = parseNumber(var0, var3, 3600, "hours");
            long var10 = parseNumber(var0, var4, 60, "minutes");
            long var12 = parseNumber(var0, var5, 1, "seconds");

            try {
               return create(var6, var8, var10, var12);
            } catch (ArithmeticException var15) {
               throw new IllegalArgumentException("Text cannot be parsed to a Duration (overflow) " + var0, var15);
            }
         }
      }

      throw new IllegalArgumentException("Text cannot be parsed to a Duration: " + var0);
   }

   private static long parseNumber(CharSequence var0, String var1, int var2, String var3) {
      if (var1 == null) {
         return 0L;
      } else {
         try {
            long var4 = Long.parseLong(var1);
            return var4 * (long)var2;
         } catch (Exception var6) {
            throw new IllegalArgumentException("Text cannot be parsed to a Duration: " + var3 + " (in " + var0 + ")", var6);
         }
      }
   }

   private static Duration create(long var0, long var2, long var4, long var6) {
      return create(var0 + var2 + var4 + var6);
   }

   private static Duration create(long var0) {
      return var0 == 0L ? ZERO : new Duration(var0);
   }

   public long toMillis() {
      return this.seconds * 1000L;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Duration)) {
         return false;
      } else {
         Duration var2 = (Duration)var1;
         return var2.seconds == this.seconds;
      }
   }

   public int hashCode() {
      return (int)(this.seconds ^ this.seconds >>> 32);
   }

   public String toString() {
      if (this == ZERO) {
         return "PT0S";
      } else {
         long var1 = this.seconds / 86400L;
         long var3 = this.seconds % 86400L / 3600L;
         int var5 = (int)(this.seconds % 3600L / 60L);
         int var6 = (int)(this.seconds % 60L);
         StringBuilder var7 = new StringBuilder(24);
         var7.append("P");
         if (var1 != 0L) {
            var7.append(var1).append('D');
         }

         if ((var3 | (long)var5 | (long)var6) != 0L) {
            var7.append('T');
         }

         if (var3 != 0L) {
            var7.append(var3).append('H');
         }

         if (var5 != 0) {
            var7.append(var5).append('M');
         }

         if (var6 == 0 && var7.length() > 0) {
            return var7.toString();
         } else {
            var7.append(var6).append('S');
            return var7.toString();
         }
      }
   }

   public int compareTo(Duration var1) {
      return Long.signum(this.toMillis() - var1.toMillis());
   }
}
