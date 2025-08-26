package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.io.File;
import java.util.Objects;
import net.minecraft.server.notifications.NotificationService;

public class UserBanList extends StoredUserList<NameAndId, UserBanListEntry> {
   public UserBanList(File var1, NotificationService var2) {
      super(var1, var2);
   }

   protected StoredUserEntry<NameAndId> createEntry(JsonObject var1) {
      return new UserBanListEntry(var1);
   }

   public boolean isBanned(NameAndId var1) {
      return this.contains(var1);
   }

   public String[] getUserList() {
      return (String[])this.getEntries().stream().map(StoredUserEntry::getUser).filter(Objects::nonNull).map(NameAndId::name).toArray((var0) -> new String[var0]);
   }

   protected String getKeyForUser(NameAndId var1) {
      return var1.id().toString();
   }

   public boolean add(UserBanListEntry var1) {
      if (super.add(var1)) {
         if (var1.getUser() != null) {
            this.notificationService.playerBanned(var1);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean remove(NameAndId var1) {
      if (super.remove(var1)) {
         this.notificationService.playerUnbanned(var1);
         return true;
      } else {
         return false;
      }
   }

   public void clear() {
      for(UserBanListEntry var2 : this.getEntries()) {
         if (var2.getUser() != null) {
            this.notificationService.playerUnbanned((NameAndId)var2.getUser());
         }
      }

      super.clear();
   }

   // $FF: synthetic method
   public boolean remove(final Object var1) {
      return this.remove((NameAndId)var1);
   }
}
