package net.minecraft.client.multiplayer.signaling;

import com.google.gson.JsonElement;
import org.jspecify.annotations.Nullable;

public final class JsonRpcException extends RuntimeException {
   private final int code;
   private final String serverMessage;
   private final @Nullable JsonElement data;

   public JsonRpcException(final int code, final String message, final @Nullable JsonElement data) {
      super("JSON-RPC error " + code + ": " + message + (data == null ? "" : " (data=" + String.valueOf(data) + ")"));
      this.code = code;
      this.serverMessage = message;
      this.data = data;
   }

   public int code() {
      return this.code;
   }

   public String serverMessage() {
      return this.serverMessage;
   }

   public @Nullable JsonElement data() {
      return this.data;
   }
}
