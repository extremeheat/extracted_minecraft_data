package io.netty.handler.codec.http.websocketx.extensions;

import java.util.Collections;
import java.util.Map;

public final class WebSocketExtensionData {
   private final String name;
   private final Map<String, String> parameters;

   public WebSocketExtensionData(String var1, Map<String, String> var2) {
      super();
      if (var1 == null) {
         throw new NullPointerException("name");
      } else if (var2 == null) {
         throw new NullPointerException("parameters");
      } else {
         this.name = var1;
         this.parameters = Collections.unmodifiableMap(var2);
      }
   }

   public String name() {
      return this.name;
   }

   public Map<String, String> parameters() {
      return this.parameters;
   }
}
