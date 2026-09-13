package net.minecraft.network;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;

public class ServerStatusResponse$MinecraftProtocolVersionIdentifier$Serializer implements JsonDeserializer, JsonSerializer {
   public ServerStatusResponse$MinecraftProtocolVersionIdentifier$Serializer() {
      super();
   }

   public ServerStatusResponse$MinecraftProtocolVersionIdentifier deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      JsonObject var4 = JsonUtils.func_151210_l(var1, "version");
      return new ServerStatusResponse$MinecraftProtocolVersionIdentifier(JsonUtils.func_151200_h(var4, "name"), JsonUtils.func_151203_m(var4, "protocol"));
   }

   public JsonElement serialize(ServerStatusResponse$MinecraftProtocolVersionIdentifier var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("name", var1.func_151303_a());
      var4.addProperty("protocol", var1.func_151304_b());
      return var4;
   }
}
