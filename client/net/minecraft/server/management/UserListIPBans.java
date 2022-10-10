package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;

public class UserListIPBans extends UserList<String, UserListIPBansEntry> {
   public UserListIPBans(File var1) {
      super(var1);
   }

   protected UserListEntry<String> func_152682_a(JsonObject var1) {
      return new UserListIPBansEntry(var1);
   }

   public boolean func_152708_a(SocketAddress var1) {
      String var2 = this.func_152707_c(var1);
      return this.func_152692_d(var2);
   }

   public boolean func_199044_a(String var1) {
      return this.func_152692_d(var1);
   }

   public UserListIPBansEntry func_152709_b(SocketAddress var1) {
      String var2 = this.func_152707_c(var1);
      return (UserListIPBansEntry)this.func_152683_b(var2);
   }

   private String func_152707_c(SocketAddress var1) {
      String var2 = var1.toString();
      if (var2.contains("/")) {
         var2 = var2.substring(var2.indexOf(47) + 1);
      }

      if (var2.contains(":")) {
         var2 = var2.substring(0, var2.indexOf(58));
      }

      return var2;
   }
}
