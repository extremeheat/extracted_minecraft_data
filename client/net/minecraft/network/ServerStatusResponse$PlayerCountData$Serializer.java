package net.minecraft.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.util.JsonUtils;

public class ServerStatusResponse$PlayerCountData$Serializer implements JsonDeserializer, JsonSerializer {
   public ServerStatusResponse$PlayerCountData$Serializer() {
      super();
   }

   public ServerStatusResponse$PlayerCountData deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      JsonObject var4 = JsonUtils.func_151210_l(var1, "players");
      ServerStatusResponse$PlayerCountData var5 = new ServerStatusResponse$PlayerCountData(
         JsonUtils.func_151203_m(var4, "max"), JsonUtils.func_151203_m(var4, "online")
      );
      if (JsonUtils.func_151202_d(var4, "sample")) {
         JsonArray var6 = JsonUtils.func_151214_t(var4, "sample");
         if (var6.size() > 0) {
            GameProfile[] var7 = new GameProfile[var6.size()];

            for(int var8 = 0; var8 < var7.length; ++var8) {
               JsonObject var9 = JsonUtils.func_151210_l(var6.get(var8), "player[" + var8 + "]");
               String var10 = JsonUtils.func_151200_h(var9, "id");
               var7[var8] = new GameProfile(UUID.fromString(var10), JsonUtils.func_151200_h(var9, "name"));
            }

            var5.func_151330_a(var7);
         }
      }

      return var5;
   }

   public JsonElement serialize(ServerStatusResponse$PlayerCountData var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("max", var1.func_151332_a());
      var4.addProperty("online", var1.func_151333_b());
      if (var1.func_151331_c() != null && var1.func_151331_c().length > 0) {
         JsonArray var5 = new JsonArray();

         for(int var6 = 0; var6 < var1.func_151331_c().length; ++var6) {
            JsonObject var7 = new JsonObject();
            UUID var8 = var1.func_151331_c()[var6].getId();
            var7.addProperty("id", var8 == null ? "" : var8.toString());
            var7.addProperty("name", var1.func_151331_c()[var6].getName());
            var5.add(var7);
         }

         var4.add("sample", var5);
      }

      return var4;
   }
}
