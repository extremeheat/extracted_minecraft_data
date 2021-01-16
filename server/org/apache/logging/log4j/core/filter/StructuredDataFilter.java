package org.apache.logging.log4j.core.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.apache.logging.log4j.util.IndexedReadOnlyStringMap;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilders;

@Plugin(
   name = "StructuredDataFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
@PerformanceSensitive({"allocation"})
public final class StructuredDataFilter extends MapFilter {
   private static final int MAX_BUFFER_SIZE = 2048;
   private static ThreadLocal<StringBuilder> threadLocalStringBuilder = new ThreadLocal();

   private StructuredDataFilter(Map<String, List<String>> var1, boolean var2, Filter.Result var3, Filter.Result var4) {
      super(var1, var2, var3, var4);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      return var4 instanceof StructuredDataMessage ? this.filter((StructuredDataMessage)var4) : Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(LogEvent var1) {
      Message var2 = var1.getMessage();
      return var2 instanceof StructuredDataMessage ? this.filter((StructuredDataMessage)var2) : super.filter(var1);
   }

   protected Filter.Result filter(StructuredDataMessage var1) {
      boolean var2 = false;
      IndexedReadOnlyStringMap var3 = this.getStringMap();

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         StringBuilder var5 = this.getValue(var1, var3.getKeyAt(var4));
         if (var5 != null) {
            var2 = this.listContainsValue((List)var3.getValueAt(var4), var5);
         } else {
            var2 = false;
         }

         if (!this.isAnd() && var2 || this.isAnd() && !var2) {
            break;
         }
      }

      return var2 ? this.onMatch : this.onMismatch;
   }

   private StringBuilder getValue(StructuredDataMessage var1, String var2) {
      StringBuilder var3 = this.getStringBuilder();
      if (var2.equalsIgnoreCase("id")) {
         var1.getId().formatTo(var3);
         return var3;
      } else if (var2.equalsIgnoreCase("id.name")) {
         return this.appendOrNull(var1.getId().getName(), var3);
      } else if (var2.equalsIgnoreCase("type")) {
         return this.appendOrNull(var1.getType(), var3);
      } else if (var2.equalsIgnoreCase("message")) {
         var1.formatTo(var3);
         return var3;
      } else {
         return this.appendOrNull(var1.get(var2), var3);
      }
   }

   private StringBuilder getStringBuilder() {
      StringBuilder var1 = (StringBuilder)threadLocalStringBuilder.get();
      if (var1 == null) {
         var1 = new StringBuilder();
         threadLocalStringBuilder.set(var1);
      }

      if (var1.length() > 2048) {
         var1.setLength(2048);
         var1.trimToSize();
      }

      var1.setLength(0);
      return var1;
   }

   private StringBuilder appendOrNull(String var1, StringBuilder var2) {
      if (var1 == null) {
         return null;
      } else {
         var2.append(var1);
         return var2;
      }
   }

   private boolean listContainsValue(List<String> var1, StringBuilder var2) {
      int var3;
      String var4;
      if (var2 == null) {
         for(var3 = 0; var3 < var1.size(); ++var3) {
            var4 = (String)var1.get(var3);
            if (var4 == null) {
               return true;
            }
         }
      } else {
         for(var3 = 0; var3 < var1.size(); ++var3) {
            var4 = (String)var1.get(var3);
            if (var4 == null) {
               return false;
            }

            if (StringBuilders.equals(var4, 0, var4.length(), var2, 0, var2.length())) {
               return true;
            }
         }
      }

      return false;
   }

   @PluginFactory
   public static StructuredDataFilter createFilter(@PluginElement("Pairs") KeyValuePair[] var0, @PluginAttribute("operator") String var1, @PluginAttribute("onMatch") Filter.Result var2, @PluginAttribute("onMismatch") Filter.Result var3) {
      if (var0 != null && var0.length != 0) {
         HashMap var4 = new HashMap();
         KeyValuePair[] var5 = var0;
         int var6 = var0.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            KeyValuePair var8 = var5[var7];
            String var9 = var8.getKey();
            if (var9 == null) {
               LOGGER.error("A null key is not valid in MapFilter");
            } else {
               String var10 = var8.getValue();
               if (var10 == null) {
                  LOGGER.error("A null value for key " + var9 + " is not allowed in MapFilter");
               } else {
                  List var11 = (List)var4.get(var8.getKey());
                  if (var11 != null) {
                     var11.add(var10);
                  } else {
                     ArrayList var12 = new ArrayList();
                     var12.add(var10);
                     var4.put(var8.getKey(), var12);
                  }
               }
            }
         }

         if (var4.isEmpty()) {
            LOGGER.error("StructuredDataFilter is not configured with any valid key value pairs");
            return null;
         } else {
            boolean var13 = var1 == null || !var1.equalsIgnoreCase("or");
            return new StructuredDataFilter(var4, var13, var2, var3);
         }
      } else {
         LOGGER.error("keys and values must be specified for the StructuredDataFilter");
         return null;
      }
   }
}
