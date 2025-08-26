package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.io.File;
import java.util.Objects;
import net.minecraft.server.notifications.NotificationService;

public class ServerOpList extends StoredUserList<NameAndId, ServerOpListEntry> {
   public ServerOpList(File var1, NotificationService var2) {
      super(var1, var2);
   }

   protected StoredUserEntry<NameAndId> createEntry(JsonObject var1) {
      return new ServerOpListEntry(var1);
   }

   public String[] getUserList() {
      return (String[])this.getEntries().stream().map(StoredUserEntry::getUser).filter(Objects::nonNull).map(NameAndId::name).toArray((var0) -> new String[var0]);
   }

   public boolean add(ServerOpListEntry var1) {
      if (super.add(var1)) {
         if (var1.getUser() != null) {
            this.notificationService.playerOped(var1);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean remove(NameAndId var1) {
      ServerOpListEntry var2 = (ServerOpListEntry)this.get(var1);
      if (super.remove(var1)) {
         if (var2 != null) {
            this.notificationService.playerDeoped(var2);
         }

         return true;
      } else {
         return false;
      }
   }

   public void clear() {
      for(ServerOpListEntry var2 : this.getEntries()) {
         if (var2.getUser() != null) {
            this.notificationService.playerDeoped(var2);
         }
      }

      super.clear();
   }

   public boolean canBypassPlayerLimit(NameAndId var1) {
      ServerOpListEntry var2 = (ServerOpListEntry)this.get(var1);
      return var2 != null ? var2.getBypassesPlayerLimit() : false;
   }

   protected String getKeyForUser(NameAndId var1) {
      return var1.id().toString();
   }

   // $FF: synthetic method
   protected String getKeyForUser(final Object var1) {
      return this.getKeyForUser((NameAndId)var1);
   }
}
