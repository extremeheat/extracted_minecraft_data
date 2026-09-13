package net.minecraft.network;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

public class ServerStatusResponse$Serializer implements JsonDeserializer, JsonSerializer {
   public ServerStatusResponse$Serializer() {
      super();
   }

   public ServerStatusResponse deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      JsonObject var4 = JsonUtils.func_151210_l(var1, "status");
      ServerStatusResponse var5 = new ServerStatusResponse();
      if (var4.has("description")) {
         var5.func_151315_a((IChatComponent)var3.deserialize(var4.get("description"), IChatComponent.class));
      }

      if (var4.has("players")) {
         var5.func_151319_a((ServerStatusResponse$PlayerCountData)var3.deserialize(var4.get("players"), ServerStatusResponse$PlayerCountData.class));
      }

      if (var4.has("version")) {
         var5.func_151321_a(
            (ServerStatusResponse$MinecraftProtocolVersionIdentifier)var3.deserialize(
               var4.get("version"), ServerStatusResponse$MinecraftProtocolVersionIdentifier.class
            )
         );
      }

      if (var4.has("favicon")) {
         var5.func_151320_a(JsonUtils.func_151200_h(var4, "favicon"));
      }

      return var5;
   }

   public JsonElement serialize(ServerStatusResponse var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      if (var1.func_151317_a() != null) {
         var4.add("description", var3.serialize(var1.func_151317_a()));
      }

      if (var1.func_151318_b() != null) {
         var4.add("players", var3.serialize(var1.func_151318_b()));
      }

      if (var1.func_151322_c() != null) {
         var4.add("version", var3.serialize(var1.func_151322_c()));
      }

      if (var1.func_151316_d() != null) {
         var4.addProperty("favicon", var1.func_151316_d());
      }

      return var4;
   }
}
