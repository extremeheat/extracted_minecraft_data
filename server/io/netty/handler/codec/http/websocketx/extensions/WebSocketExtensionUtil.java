package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebSocketExtensionUtil {
   private static final String EXTENSION_SEPARATOR = ",";
   private static final String PARAMETER_SEPARATOR = ";";
   private static final char PARAMETER_EQUAL = '=';
   private static final Pattern PARAMETER = Pattern.compile("^([^=]+)(=[\\\"]?([^\\\"]+)[\\\"]?)?$");

   static boolean isWebsocketUpgrade(HttpHeaders var0) {
      return var0.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true) && var0.contains((CharSequence)HttpHeaderNames.UPGRADE, (CharSequence)HttpHeaderValues.WEBSOCKET, true);
   }

   public static List<WebSocketExtensionData> extractExtensions(String var0) {
      String[] var1 = var0.split(",");
      if (var1.length <= 0) {
         return Collections.emptyList();
      } else {
         ArrayList var2 = new ArrayList(var1.length);
         String[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            String[] var7 = var6.split(";");
            String var8 = var7[0].trim();
            Object var9;
            if (var7.length > 1) {
               var9 = new HashMap(var7.length - 1);

               for(int var10 = 1; var10 < var7.length; ++var10) {
                  String var11 = var7[var10].trim();
                  Matcher var12 = PARAMETER.matcher(var11);
                  if (var12.matches() && var12.group(1) != null) {
                     ((Map)var9).put(var12.group(1), var12.group(3));
                  }
               }
            } else {
               var9 = Collections.emptyMap();
            }

            var2.add(new WebSocketExtensionData(var8, (Map)var9));
         }

         return var2;
      }
   }

   static String appendExtension(String var0, String var1, Map<String, String> var2) {
      StringBuilder var3 = new StringBuilder(var0 != null ? var0.length() : var1.length() + 1);
      if (var0 != null && !var0.trim().isEmpty()) {
         var3.append(var0);
         var3.append(",");
      }

      var3.append(var1);
      Iterator var4 = var2.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         var3.append(";");
         var3.append((String)var5.getKey());
         if (var5.getValue() != null) {
            var3.append('=');
            var3.append((String)var5.getValue());
         }
      }

      return var3.toString();
   }

   private WebSocketExtensionUtil() {
      super();
   }
}
