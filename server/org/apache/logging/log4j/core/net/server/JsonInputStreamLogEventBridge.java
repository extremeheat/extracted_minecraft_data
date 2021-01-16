package org.apache.logging.log4j.core.net.server;

import java.nio.charset.Charset;
import org.apache.logging.log4j.core.jackson.Log4jJsonObjectMapper;

public class JsonInputStreamLogEventBridge extends InputStreamLogEventBridge {
   private static final int[] END_PAIR = new int[]{-1, -1};
   private static final char EVENT_END_MARKER = '}';
   private static final char EVENT_START_MARKER = '{';
   private static final char JSON_ESC = '\\';
   private static final char JSON_STR_DELIM = '"';
   private static final boolean THREAD_CONTEXT_MAP_AS_LIST = false;

   public JsonInputStreamLogEventBridge() {
      this(1024, Charset.defaultCharset());
   }

   public JsonInputStreamLogEventBridge(int var1, Charset var2) {
      super(new Log4jJsonObjectMapper(false, true), var1, var2, String.valueOf('}'));
   }

   protected int[] getEventIndices(String var1, int var2) {
      int var3 = var1.indexOf(123, var2);
      if (var3 == -1) {
         return END_PAIR;
      } else {
         char[] var4 = var1.toCharArray();
         int var5 = 0;
         boolean var6 = false;
         boolean var7 = false;

         for(int var8 = var3; var8 < var4.length; ++var8) {
            char var9 = var4[var8];
            if (var7) {
               var7 = false;
            } else {
               switch(var9) {
               case '"':
                  var6 = !var6;
                  break;
               case '\\':
                  var7 = true;
                  break;
               case '{':
                  if (!var6) {
                     ++var5;
                  }
                  break;
               case '}':
                  if (!var6) {
                     --var5;
                  }
               }

               if (var5 == 0) {
                  return new int[]{var3, var8};
               }
            }
         }

         return END_PAIR;
      }
   }
}
