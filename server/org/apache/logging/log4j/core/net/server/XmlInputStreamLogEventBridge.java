package org.apache.logging.log4j.core.net.server;

import java.nio.charset.Charset;
import org.apache.logging.log4j.core.jackson.Log4jXmlObjectMapper;

public class XmlInputStreamLogEventBridge extends InputStreamLogEventBridge {
   private static final String EVENT_END = "</Event>";
   private static final String EVENT_START_NS_N = "<Event>";
   private static final String EVENT_START_NS_Y = "<Event ";

   public XmlInputStreamLogEventBridge() {
      this(1024, Charset.defaultCharset());
   }

   public XmlInputStreamLogEventBridge(int var1, Charset var2) {
      super(new Log4jXmlObjectMapper(), var1, var2, "</Event>");
   }

   protected int[] getEventIndices(String var1, int var2) {
      int var3 = var1.indexOf("<Event ", var2);
      int var4 = "<Event ".length();
      if (var3 < 0) {
         var3 = var1.indexOf("<Event>", var2);
         var4 = "<Event>".length();
      }

      int var5 = var3 < 0 ? -1 : var1.indexOf("</Event>", var3 + var4);
      return new int[]{var3, var5};
   }
}
