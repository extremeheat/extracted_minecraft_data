package net.minecraft.server.jsonrpc;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public enum JsonRPCErrors {
   PARSE_ERROR(-32700, "Parse error"),
   INVALID_REQUEST(-32600, "Invalid Request"),
   METHOD_NOT_FOUND(-32601, "Method not found"),
   INVALID_PARAMS(-32602, "Invalid params"),
   INTERNAL_ERROR(-32603, "Internal error");

   private final int errorCode;
   private final String message;

   private JsonRPCErrors(final int var3, final String var4) {
      this.errorCode = var3;
      this.message = var4;
   }

   public JsonObject createWithUnknownId(@Nullable String var1) {
      return JsonRPCUtils.createError(JsonNull.INSTANCE, this.message, this.errorCode, var1);
   }

   public JsonObject createWithoutData(JsonElement var1) {
      return JsonRPCUtils.createError(var1, this.message, this.errorCode, (String)null);
   }

   public JsonObject create(JsonElement var1, String var2) {
      return JsonRPCUtils.createError(var1, this.message, this.errorCode, var2);
   }

   // $FF: synthetic method
   private static JsonRPCErrors[] $values() {
      return new JsonRPCErrors[]{PARSE_ERROR, INVALID_REQUEST, METHOD_NOT_FOUND, INVALID_PARAMS, INTERNAL_ERROR};
   }
}
