package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public class UserBanListEntry extends BanListEntry<GameProfile> {
   public UserBanListEntry(@Nullable GameProfile var1) {
      this(var1, (Date)null, (String)null, (Date)null, (String)null);
   }

   public UserBanListEntry(@Nullable GameProfile var1, @Nullable Date var2, @Nullable String var3, @Nullable Date var4, @Nullable String var5) {
      super(var1, var2, var3, var4, var5);
   }

   public UserBanListEntry(JsonObject var1) {
      super(createGameProfile(var1), var1);
   }

   protected void serialize(JsonObject var1) {
      if (this.getUser() != null) {
         var1.addProperty("uuid", ((GameProfile)this.getUser()).getId().toString());
         var1.addProperty("name", ((GameProfile)this.getUser()).getName());
         super.serialize(var1);
      }
   }

   public Component getDisplayName() {
      GameProfile var1 = (GameProfile)this.getUser();
      return var1 != null ? Component.literal(var1.getName()) : Component.translatable("commands.banlist.entry.unknown");
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
