package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;

public class ServerOpListEntry extends StoredUserEntry<GameProfile> {
   private final int level;
   private final boolean bypassesPlayerLimit;

   public ServerOpListEntry(GameProfile var1, int var2, boolean var3) {
      super(var1);
      this.level = var2;
      this.bypassesPlayerLimit = var3;
   }

   public ServerOpListEntry(JsonObject var1) {
      super(createGameProfile(var1));
      this.level = var1.has("level") ? var1.get("level").getAsInt() : 0;
      this.bypassesPlayerLimit = var1.has("bypassesPlayerLimit") && var1.get("bypassesPlayerLimit").getAsBoolean();
   }

   public int getLevel() {
      return this.level;
   }

   public boolean getBypassesPlayerLimit() {
      return this.bypassesPlayerLimit;
   }

   protected void serialize(JsonObject var1) {
      if (this.getUser() != null) {
         var1.addProperty("uuid", ((GameProfile)this.getUser()).getId().toString());
         var1.addProperty("name", ((GameProfile)this.getUser()).getName());
         var1.addProperty("level", this.level);
         var1.addProperty("bypassesPlayerLimit", this.bypassesPlayerLimit);
      }
   }

   @Nullable
   private static GameProfile createGameProfile(JsonObject var0) {
      if (var0.has("uuid") && var0.has("name")) {
         String var1 = var0.get("uuid").getAsString();

         UUID var2;
         try {
            var2 = UUID.fromString(var1);
         } catch (Throwable var4) {
            return null;
         }

         return new GameProfile(var2, var0.get("name").getAsString());
      } else {
         return null;
      }
   }
}
