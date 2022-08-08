package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;
import javax.annotation.Nullable;

public class IpBanList extends StoredUserList<String, IpBanListEntry> {
   public IpBanList(File var1) {
      super(var1);
   }

   protected StoredUserEntry<String> createEntry(JsonObject var1) {
      return new IpBanListEntry(var1);
   }

   public boolean isBanned(SocketAddress var1) {
      String var2 = this.getIpFromAddress(var1);
      return this.contains(var2);
   }

   public boolean isBanned(String var1) {
      return this.contains(var1);
   }

   @Nullable
   public IpBanListEntry get(SocketAddress var1) {
      String var2 = this.getIpFromAddress(var1);
      return (IpBanListEntry)this.get(var2);
   }

   private String getIpFromAddress(SocketAddress var1) {
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
