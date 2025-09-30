package net.minecraft.server.jsonrpc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class JsonRPCUtils {
   public static final String JSON_RPC_VERSION = "2.0";
   public static final String OPEN_RPC_VERSION = "1.3.2";

   public JsonRPCUtils() {
      super();
   }

   public static JsonObject createSuccessResult(JsonElement var0, JsonElement var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("jsonrpc", "2.0");
      var2.add("id", var0);
      var2.add("result", var1);
      return var2;
   }

   public static JsonObject createRequest(@Nullable Integer var0, ResourceLocation var1, List<JsonElement> var2) {
      JsonObject var3 = new JsonObject();
      var3.addProperty("jsonrpc", "2.0");
      if (var0 != null) {
         var3.addProperty("id", var0);
      }

      var3.addProperty("method", var1.toString());
      if (!var2.isEmpty()) {
         JsonArray var4 = new JsonArray(var2.size());

         for(JsonElement var6 : var2) {
            var4.add(var6);
         }

         var3.add("params", var4);
      }

      return var3;
   }

   public static JsonObject createError(JsonElement var0, String var1, int var2, @Nullable String var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("jsonrpc", "2.0");
      var4.add("id", var0);
      JsonObject var5 = new JsonObject();
      var5.addProperty("code", var2);
      var5.addProperty("message", var1);
      if (var3 != null && !var3.isBlank()) {
         var5.addProperty("data", var3);
      }

      var4.add("error", var5);
      return var4;
   }

   @Nullable
   public static JsonElement getRequestId(JsonObject var0) {
      return var0.get("id");
   }

   @Nullable
   public static String getMethodName(JsonObject var0) {
      return GsonHelper.getAsString(var0, "method", (String)null);
   }

   @Nullable
   public static JsonElement getParams(JsonObject var0) {
      return var0.get("params");
   }

   @Nullable
   public static JsonElement getResult(JsonObject var0) {
      return var0.get("result");
   }

   @Nullable
   public static JsonObject getError(JsonObject var0) {
      return GsonHelper.getAsJsonObject(var0, "error", (JsonObject)null);
   }
}
