package org.apache.logging.log4j.core.net.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.core.LogEventListener;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;

public abstract class InputStreamLogEventBridge extends AbstractLogEventBridge<InputStream> {
   private final int bufferSize;
   private final Charset charset;
   private final String eventEndMarker;
   private final ObjectReader objectReader;

   public InputStreamLogEventBridge(ObjectMapper var1, int var2, Charset var3, String var4) {
      super();
      this.bufferSize = var2;
      this.charset = var3;
      this.eventEndMarker = var4;
      this.objectReader = var1.readerFor(Log4jLogEvent.class);
   }

   protected abstract int[] getEventIndices(String var1, int var2);

   public void logEvents(InputStream var1, LogEventListener var2) throws IOException {
      String var3 = "";

      try {
         byte[] var4 = new byte[this.bufferSize];
         var3 = "";
         String var5 = "";

         while(true) {
            while(true) {
               int var6 = var1.read(var4);
               if (var6 == -1) {
                  return;
               }

               String var7 = var5 + new String(var4, 0, var6, this.charset);
               int var8 = 0;

               while(true) {
                  int[] var9 = this.getEventIndices(var7, var8);
                  int var10 = var9[0];
                  if (var10 < 0) {
                     var5 = var7.substring(var8);
                     break;
                  }

                  int var11 = var9[1];
                  if (var11 <= 0) {
                     var5 = var7.substring(var8);
                     break;
                  }

                  int var12 = var11 + this.eventEndMarker.length();
                  String var13 = var7.substring(var10, var12);
                  Log4jLogEvent var14 = this.unmarshal(var13);
                  var2.log(var14);
                  var8 = var12;
               }
            }
         }
      } catch (IOException var15) {
         logger.error((String)var3, (Throwable)var15);
      }
   }

   protected Log4jLogEvent unmarshal(String var1) throws IOException {
      return (Log4jLogEvent)this.objectReader.readValue(var1);
   }
}
