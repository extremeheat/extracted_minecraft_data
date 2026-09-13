package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListOps extends UserList {
   public UserListOps(File var1) {
      super(var1);
   }

   @Override
   protected UserListEntry func_152682_a(JsonObject var1) {
      return new UserListOpsEntry(var1);
   }

   @Override
   public String[] func_152685_a() {
      String[] var1 = new String[this.func_152688_e().size()];
      int var2 = 0;

      for(UserListOpsEntry var4 : this.func_152688_e().values()) {
         var1[var2++] = ((GameProfile)var4.func_152640_f()).getName();
      }

      return var1;
   }

   protected String func_152699_b(GameProfile var1) {
      return var1.getId().toString();
   }

   public GameProfile func_152700_a(String var1) {
      for(UserListOpsEntry var3 : this.func_152688_e().values()) {
         if (var1.equalsIgnoreCase(((GameProfile)var3.func_152640_f()).getName())) {
            return (GameProfile)var3.func_152640_f();
         }
      }

      return null;
   }
}
