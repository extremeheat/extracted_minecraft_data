package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.io.File;
import java.util.Objects;
import net.minecraft.server.notifications.NotificationService;

public class UserWhiteList extends StoredUserList<NameAndId, UserWhiteListEntry> {
   public UserWhiteList(File var1, NotificationService var2) {
      super(var1, var2);
   }

   protected StoredUserEntry<NameAndId> createEntry(JsonObject var1) {
      return new UserWhiteListEntry(var1);
   }

   public boolean isWhiteListed(NameAndId var1) {
      return this.contains(var1);
   }

   public boolean add(UserWhiteListEntry var1) {
      if (super.add(var1)) {
         if (var1.getUser() != null) {
            this.notificationService.playerAddedToAllowlist((NameAndId)var1.getUser());
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean remove(NameAndId var1) {
      if (super.remove(var1)) {
         this.notificationService.playerRemovedFromAllowlist(var1);
         return true;
      } else {
         return false;
      }
   }

   public void clear() {
      for(UserWhiteListEntry var2 : this.getEntries()) {
         if (var2.getUser() != null) {
            this.notificationService.playerRemovedFromAllowlist((NameAndId)var2.getUser());
         }
      }

      super.clear();
   }

   public String[] getUserList() {
      return (String[])this.getEntries().stream().map(StoredUserEntry::getUser).filter(Objects::nonNull).map(NameAndId::name).toArray((var0) -> new String[var0]);
   }

   protected String getKeyForUser(NameAndId var1) {
      return var1.id().toString();
   }

   // $FF: synthetic method
   protected String getKeyForUser(final Object var1) {
      return this.getKeyForUser((NameAndId)var1);
   }

   // $FF: synthetic method
   public boolean remove(final Object var1) {
      return this.remove((NameAndId)var1);
   }
}
