package net.minecraft.server.management;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

class UserList$Serializer implements JsonDeserializer, JsonSerializer {
   private UserList$Serializer(UserList var1) {
      super();
      this.field_152752_a = var1;
   }

   public JsonElement func_152751_a(UserListEntry var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      var1.func_152641_a(var4);
      return var4;
   }

   public UserListEntry func_152750_a(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      if (var1.isJsonObject()) {
         JsonObject var4 = var1.getAsJsonObject();
         return this.field_152752_a.func_152682_a(var4);
      } else {
         return null;
      }
   }
}
