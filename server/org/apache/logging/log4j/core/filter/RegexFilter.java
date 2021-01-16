package org.apache.logging.log4j.core.filter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.message.Message;

@Plugin(
   name = "RegexFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
public final class RegexFilter extends AbstractFilter {
   private static final int DEFAULT_PATTERN_FLAGS = 0;
   private final Pattern pattern;
   private final boolean useRawMessage;

   private RegexFilter(boolean var1, Pattern var2, Filter.Result var3, Filter.Result var4) {
      super(var3, var4);
      this.pattern = var2;
      this.useRawMessage = var1;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5) {
      return this.filter(var4);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5) {
      return var4 == null ? this.onMismatch : this.filter(var4.toString());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      if (var4 == null) {
         return this.onMismatch;
      } else {
         String var6 = this.useRawMessage ? var4.getFormat() : var4.getFormattedMessage();
         return this.filter(var6);
      }
   }

   public Filter.Result filter(LogEvent var1) {
      String var2 = this.useRawMessage ? var1.getMessage().getFormat() : var1.getMessage().getFormattedMessage();
      return this.filter(var2);
   }

   private Filter.Result filter(String var1) {
      if (var1 == null) {
         return this.onMismatch;
      } else {
         Matcher var2 = this.pattern.matcher(var1);
         return var2.matches() ? this.onMatch : this.onMismatch;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("useRaw=").append(this.useRawMessage);
      var1.append(", pattern=").append(this.pattern.toString());
      return var1.toString();
   }

   @PluginFactory
   public static RegexFilter createFilter(@PluginAttribute("regex") String var0, @PluginElement("PatternFlags") String[] var1, @PluginAttribute("useRawMsg") Boolean var2, @PluginAttribute("onMatch") Filter.Result var3, @PluginAttribute("onMismatch") Filter.Result var4) throws IllegalArgumentException, IllegalAccessException {
      if (var0 == null) {
         LOGGER.error("A regular expression must be provided for RegexFilter");
         return null;
      } else {
         return new RegexFilter(var2, Pattern.compile(var0, toPatternFlags(var1)), var3, var4);
      }
   }

   private static int toPatternFlags(String[] var0) throws IllegalArgumentException, IllegalAccessException {
      if (var0 != null && var0.length != 0) {
         Field[] var1 = Pattern.class.getDeclaredFields();
         Comparator var2 = new Comparator<Field>() {
            public int compare(Field var1, Field var2) {
               return var1.getName().compareTo(var2.getName());
            }
         };
         Arrays.sort(var1, var2);
         String[] var3 = new String[var1.length];

         int var4;
         for(var4 = 0; var4 < var1.length; ++var4) {
            var3[var4] = var1[var4].getName();
         }

         var4 = 0;
         String[] var5 = var0;
         int var6 = var0.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            int var9 = Arrays.binarySearch(var3, var8);
            if (var9 >= 0) {
               Field var10 = var1[var9];
               var4 |= var10.getInt(Pattern.class);
            }
         }

         return var4;
      } else {
         return 0;
      }
   }
}
