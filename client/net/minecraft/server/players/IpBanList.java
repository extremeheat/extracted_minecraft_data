package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;
import net.minecraft.server.notifications.NotificationService;
import org.jspecify.annotations.Nullable;

public class IpBanList extends StoredUserList<String, IpBanListEntry> {
   public IpBanList(File var1, NotificationService var2) {
      super(var1, var2);
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

   public @Nullable IpBanListEntry get(SocketAddress var1) {
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

   public boolean add(IpBanListEntry var1) {
      if (super.add(var1)) {
         if (var1.getUser() != null) {
            this.notificationService.ipBanned(var1);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean remove(String var1) {
      if (super.remove(var1)) {
         this.notificationService.ipUnbanned(var1);
         return true;
      } else {
         return false;
      }
   }

   public void clear() {
      for(IpBanListEntry var2 : this.getEntries()) {
         if (var2.getUser() != null) {
            this.notificationService.ipUnbanned((String)var2.getUser());
         }
      }

      super.clear();
   }

   // $FF: synthetic method
   public boolean remove(final Object var1) {
      return this.remove((String)var1);
   }
}
