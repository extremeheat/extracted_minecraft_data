package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;

public record NameAndId(UUID id, String name) {
   public NameAndId(GameProfile var1) {
      this(var1.getId(), var1.getName());
   }

   public NameAndId(UUID var1, String var2) {
      super();
      this.id = var1;
      this.name = var2;
   }

   @Nullable
   public static NameAndId fromJson(JsonObject var0) {
      if (var0.has("uuid") && var0.has("name")) {
         String var1 = var0.get("uuid").getAsString();

         UUID var2;
         try {
            var2 = UUID.fromString(var1);
         } catch (Throwable var4) {
            return null;
         }

         return new NameAndId(var2, var0.get("name").getAsString());
      } else {
         return null;
      }
   }

   public void appendTo(JsonObject var1) {
      var1.addProperty("uuid", this.id().toString());
      var1.addProperty("name", this.name());
   }

   public static NameAndId createOffline(String var0) {
      UUID var1 = UUIDUtil.createOfflinePlayerUUID(var0);
      return new NameAndId(var1, var0);
   }
}
