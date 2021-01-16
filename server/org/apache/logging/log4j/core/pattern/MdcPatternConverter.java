package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.TriConsumer;

@Plugin(
   name = "MdcPatternConverter",
   category = "Converter"
)
@ConverterKeys({"X", "mdc", "MDC"})
@PerformanceSensitive({"allocation"})
public final class MdcPatternConverter extends LogEventPatternConverter {
   private static final ThreadLocal<StringBuilder> threadLocal = new ThreadLocal();
   private static final int DEFAULT_STRING_BUILDER_SIZE = 64;
   private static final int MAX_STRING_BUILDER_SIZE;
   private final String key;
   private final String[] keys;
   private final boolean full;
   private static final TriConsumer<String, Object, StringBuilder> WRITE_KEY_VALUES_INTO;

   private MdcPatternConverter(String[] var1) {
      super(var1 != null && var1.length > 0 ? "MDC{" + var1[0] + '}' : "MDC", "mdc");
      if (var1 != null && var1.length > 0) {
         this.full = false;
         if (var1[0].indexOf(44) > 0) {
            this.keys = var1[0].split(",");

            for(int var2 = 0; var2 < this.keys.length; ++var2) {
               this.keys[var2] = this.keys[var2].trim();
            }

            this.key = null;
         } else {
            this.keys = null;
            this.key = var1[0];
         }
      } else {
         this.full = true;
         this.key = null;
         this.keys = null;
      }

   }

   public static MdcPatternConverter newInstance(String[] var0) {
      return new MdcPatternConverter(var0);
   }

   public void format(LogEvent var1, StringBuilder var2) {
      ReadOnlyStringMap var3 = var1.getContextData();
      if (this.full) {
         if (var3 == null || var3.size() == 0) {
            var2.append("{}");
            return;
         }

         appendFully(var3, var2);
      } else if (this.keys != null) {
         if (var3 == null || var3.size() == 0) {
            var2.append("{}");
            return;
         }

         appendSelectedKeys(this.keys, var3, var2);
      } else if (var3 != null) {
         Object var4 = var3.getValue(this.key);
         if (var4 != null) {
            StringBuilders.appendValue(var2, var4);
         }
      }

   }

   private static void appendFully(ReadOnlyStringMap var0, StringBuilder var1) {
      StringBuilder var2 = getStringBuilder();
      var2.append("{");
      var0.forEach(WRITE_KEY_VALUES_INTO, var2);
      var2.append('}');
      var1.append(var2);
      trimToMaxSize(var2);
   }

   private static void appendSelectedKeys(String[] var0, ReadOnlyStringMap var1, StringBuilder var2) {
      StringBuilder var3 = getStringBuilder();
      var3.append("{");

      for(int var4 = 0; var4 < var0.length; ++var4) {
         String var5 = var0[var4];
         Object var6 = var1.getValue(var5);
         if (var6 != null) {
            if (var3.length() > 1) {
               var3.append(", ");
            }

            var3.append(var5).append('=');
            StringBuilders.appendValue(var3, var6);
         }
      }

      var3.append('}');
      var2.append(var3);
      trimToMaxSize(var3);
   }

   private static StringBuilder getStringBuilder() {
      StringBuilder var0 = (StringBuilder)threadLocal.get();
      if (var0 == null) {
         var0 = new StringBuilder(64);
         threadLocal.set(var0);
      }

      var0.setLength(0);
      return var0;
   }

   private static void trimToMaxSize(StringBuilder var0) {
      if (var0.length() > MAX_STRING_BUILDER_SIZE) {
         var0.setLength(MAX_STRING_BUILDER_SIZE);
         var0.trimToSize();
      }

   }

   static {
      MAX_STRING_BUILDER_SIZE = Constants.MAX_REUSABLE_MESSAGE_SIZE;
      WRITE_KEY_VALUES_INTO = new TriConsumer<String, Object, StringBuilder>() {
         public void accept(String var1, Object var2, StringBuilder var3) {
            if (var3.length() > 1) {
               var3.append(", ");
            }

            var3.append(var1).append('=');
            StringBuilders.appendValue(var3, var2);
         }
      };
   }
}
