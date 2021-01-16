package org.apache.logging.log4j.core.pattern;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.datetime.FastDateFormat;
import org.apache.logging.log4j.core.util.datetime.FixedDateFormat;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "DatePatternConverter",
   category = "Converter"
)
@ConverterKeys({"d", "date"})
@PerformanceSensitive({"allocation"})
public final class DatePatternConverter extends LogEventPatternConverter implements ArrayPatternConverter {
   private static final String UNIX_FORMAT = "UNIX";
   private static final String UNIX_MILLIS_FORMAT = "UNIX_MILLIS";
   private final String[] options;
   private final ThreadLocal<DatePatternConverter.Formatter> threadLocalFormatter = new ThreadLocal();
   private final AtomicReference<DatePatternConverter.CachedTime> cachedTime;
   private final DatePatternConverter.Formatter formatter;

   private DatePatternConverter(String[] var1) {
      super("Date", "date");
      this.options = var1 == null ? null : (String[])Arrays.copyOf(var1, var1.length);
      this.formatter = this.createFormatter(var1);
      this.cachedTime = new AtomicReference(new DatePatternConverter.CachedTime(System.currentTimeMillis()));
   }

   private DatePatternConverter.Formatter createFormatter(String[] var1) {
      FixedDateFormat var2 = FixedDateFormat.createIfSupported(var1);
      return var2 != null ? createFixedFormatter(var2) : createNonFixedFormatter(var1);
   }

   public static DatePatternConverter newInstance(String[] var0) {
      return new DatePatternConverter(var0);
   }

   private static DatePatternConverter.Formatter createFixedFormatter(FixedDateFormat var0) {
      return new DatePatternConverter.FixedFormatter(var0);
   }

   private static DatePatternConverter.Formatter createNonFixedFormatter(String[] var0) {
      Objects.requireNonNull(var0);
      if (var0.length == 0) {
         throw new IllegalArgumentException("options array must have at least one element");
      } else {
         Objects.requireNonNull(var0[0]);
         String var1 = var0[0];
         if ("UNIX".equals(var1)) {
            return new DatePatternConverter.UnixFormatter();
         } else if ("UNIX_MILLIS".equals(var1)) {
            return new DatePatternConverter.UnixMillisFormatter();
         } else {
            FixedDateFormat.FixedFormat var2 = FixedDateFormat.FixedFormat.lookup(var1);
            String var3 = var2 == null ? var1 : var2.getPattern();
            TimeZone var4 = null;
            if (var0.length > 1 && var0[1] != null) {
               var4 = TimeZone.getTimeZone(var0[1]);
            }

            try {
               FastDateFormat var5 = FastDateFormat.getInstance(var3, var4);
               return new DatePatternConverter.PatternFormatter(var5);
            } catch (IllegalArgumentException var6) {
               LOGGER.warn((String)("Could not instantiate FastDateFormat with pattern " + var3), (Throwable)var6);
               return createFixedFormatter(FixedDateFormat.create(FixedDateFormat.FixedFormat.DEFAULT, var4));
            }
         }
      }
   }

   public void format(Date var1, StringBuilder var2) {
      this.format(var1.getTime(), var2);
   }

   public void format(LogEvent var1, StringBuilder var2) {
      this.format(var1.getTimeMillis(), var2);
   }

   public void format(long var1, StringBuilder var3) {
      if (Constants.ENABLE_THREADLOCALS) {
         this.formatWithoutAllocation(var1, var3);
      } else {
         this.formatWithoutThreadLocals(var1, var3);
      }

   }

   private void formatWithoutAllocation(long var1, StringBuilder var3) {
      this.getThreadLocalFormatter().formatToBuffer(var1, var3);
   }

   private DatePatternConverter.Formatter getThreadLocalFormatter() {
      DatePatternConverter.Formatter var1 = (DatePatternConverter.Formatter)this.threadLocalFormatter.get();
      if (var1 == null) {
         var1 = this.createFormatter(this.options);
         this.threadLocalFormatter.set(var1);
      }

      return var1;
   }

   private void formatWithoutThreadLocals(long var1, StringBuilder var3) {
      DatePatternConverter.CachedTime var4 = (DatePatternConverter.CachedTime)this.cachedTime.get();
      if (var1 != var4.timestampMillis) {
         DatePatternConverter.CachedTime var5 = new DatePatternConverter.CachedTime(var1);
         if (this.cachedTime.compareAndSet(var4, var5)) {
            var4 = var5;
         } else {
            var4 = (DatePatternConverter.CachedTime)this.cachedTime.get();
         }
      }

      var3.append(var4.formatted);
   }

   public void format(Object var1, StringBuilder var2) {
      if (var1 instanceof Date) {
         this.format((Date)var1, var2);
      }

      super.format(var1, var2);
   }

   public void format(StringBuilder var1, Object... var2) {
      Object[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Object var6 = var3[var5];
         if (var6 instanceof Date) {
            this.format(var6, var1);
            break;
         }
      }

   }

   public String getPattern() {
      return this.formatter.toPattern();
   }

   private final class CachedTime {
      public long timestampMillis;
      public String formatted;

      public CachedTime(long var2) {
         super();
         this.timestampMillis = var2;
         this.formatted = DatePatternConverter.this.formatter.format(this.timestampMillis);
      }
   }

   private static final class UnixMillisFormatter extends DatePatternConverter.Formatter {
      private UnixMillisFormatter() {
         super(null);
      }

      String format(long var1) {
         return Long.toString(var1);
      }

      void formatToBuffer(long var1, StringBuilder var3) {
         var3.append(var1);
      }

      // $FF: synthetic method
      UnixMillisFormatter(Object var1) {
         this();
      }
   }

   private static final class UnixFormatter extends DatePatternConverter.Formatter {
      private UnixFormatter() {
         super(null);
      }

      String format(long var1) {
         return Long.toString(var1 / 1000L);
      }

      void formatToBuffer(long var1, StringBuilder var3) {
         var3.append(var1 / 1000L);
      }

      // $FF: synthetic method
      UnixFormatter(Object var1) {
         this();
      }
   }

   private static final class FixedFormatter extends DatePatternConverter.Formatter {
      private final FixedDateFormat fixedDateFormat;
      private final char[] cachedBuffer = new char[64];
      private int length = 0;

      FixedFormatter(FixedDateFormat var1) {
         super(null);
         this.fixedDateFormat = var1;
      }

      String format(long var1) {
         return this.fixedDateFormat.format(var1);
      }

      void formatToBuffer(long var1, StringBuilder var3) {
         if (this.previousTime != var1) {
            this.length = this.fixedDateFormat.format(var1, this.cachedBuffer, 0);
         }

         var3.append(this.cachedBuffer, 0, this.length);
      }

      public String toPattern() {
         return this.fixedDateFormat.getFormat();
      }
   }

   private static final class PatternFormatter extends DatePatternConverter.Formatter {
      private final FastDateFormat fastDateFormat;
      private final StringBuilder cachedBuffer = new StringBuilder(64);

      PatternFormatter(FastDateFormat var1) {
         super(null);
         this.fastDateFormat = var1;
      }

      String format(long var1) {
         return this.fastDateFormat.format(var1);
      }

      void formatToBuffer(long var1, StringBuilder var3) {
         if (this.previousTime != var1) {
            this.cachedBuffer.setLength(0);
            this.fastDateFormat.format(var1, this.cachedBuffer);
         }

         var3.append(this.cachedBuffer);
      }

      public String toPattern() {
         return this.fastDateFormat.getPattern();
      }
   }

   private abstract static class Formatter {
      long previousTime;

      private Formatter() {
         super();
      }

      abstract String format(long var1);

      abstract void formatToBuffer(long var1, StringBuilder var3);

      public String toPattern() {
         return null;
      }

      // $FF: synthetic method
      Formatter(Object var1) {
         this();
      }
   }
}
