package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.io.File;
import java.util.Objects;

public class UserBanList extends StoredUserList<NameAndId, UserBanListEntry> {
   public UserBanList(File var1) {
      super(var1);
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

   // $FF: synthetic method
   protected String getKeyForUser(final Object var1) {
      return this.getKeyForUser((NameAndId)var1);
   }
}
