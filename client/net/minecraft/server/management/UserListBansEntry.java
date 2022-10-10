package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class UserListBansEntry extends UserListEntryBan<GameProfile> {
   public UserListBansEntry(GameProfile var1) {
      this(var1, (Date)null, (String)null, (Date)null, (String)null);
   }

   public UserListBansEntry(GameProfile var1, @Nullable Date var2, @Nullable String var3, @Nullable Date var4, @Nullable String var5) {
      super(var1, var4, var3, var4, var5);
   }

   public UserListBansEntry(JsonObject var1) {
      super(func_152648_b(var1), var1);
   }

   protected void func_152641_a(JsonObject var1) {
      if (this.func_152640_f() != null) {
         var1.addProperty("uuid", ((GameProfile)this.func_152640_f()).getId() == null ? "" : ((GameProfile)this.func_152640_f()).getId().toString());
         var1.addProperty("name", ((GameProfile)this.func_152640_f()).getName());
         super.func_152641_a(var1);
      }
   }

   public ITextComponent func_199041_e() {
      GameProfile var1 = (GameProfile)this.func_152640_f();
      return new TextComponentString(var1.getName() != null ? var1.getName() : Objects.toString(var1.getId(), "(Unknown)"));
   }

   private static GameProfile func_152648_b(JsonObject var0) {
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
