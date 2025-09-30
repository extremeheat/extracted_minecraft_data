package net.minecraft.server.jsonrpc.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RemoteRpcErrorException extends RuntimeException {
   private final JsonElement id;
   private final JsonObject error;

   public RemoteRpcErrorException(JsonElement var1, JsonObject var2) {
      super();
      this.id = var1;
      this.error = var2;
   }

   private JsonObject getError() {
      return this.error;
   }

   private JsonElement getId() {
      return this.id;
   }
}
