package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Iterator;

public class UserListWhitelist extends UserList<GameProfile, UserListWhitelistEntry> {
   public UserListWhitelist(File var1) {
      super(var1);
   }

   protected UserListEntry<GameProfile> func_152682_a(JsonObject var1) {
      return new UserListWhitelistEntry(var1);
   }

   public boolean func_152705_a(GameProfile var1) {
      return this.func_152692_d(var1);
   }

   public String[] func_152685_a() {
      String[] var1 = new String[this.func_199043_f().size()];
      int var2 = 0;

      UserListEntry var4;
      for(Iterator var3 = this.func_199043_f().iterator(); var3.hasNext(); var1[var2++] = ((GameProfile)var4.func_152640_f()).getName()) {
         var4 = (UserListEntry)var3.next();
      }

      return var1;
   }

   protected String func_152681_a(GameProfile var1) {
      return var1.getId().toString();
   }

   // $FF: synthetic method
   protected String func_152681_a(Object var1) {
      return this.func_152681_a((GameProfile)var1);
   }
}
