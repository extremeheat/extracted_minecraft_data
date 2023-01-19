package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Objects;

public class UserWhiteList extends StoredUserList<GameProfile, UserWhiteListEntry> {
   public UserWhiteList(File var1) {
      super(var1);
   }

   @Override
   protected StoredUserEntry<GameProfile> createEntry(JsonObject var1) {
      return new UserWhiteListEntry(var1);
   }

   public boolean isWhiteListed(GameProfile var1) {
      return this.contains(var1);
   }

   @Override
   public String[] getUserList() {
      return this.getEntries().stream().map(StoredUserEntry::getUser).filter(Objects::nonNull).map(GameProfile::getName).toArray(var0 -> new String[var0]);
   }

   protected String getKeyForUser(GameProfile var1) {
      return var1.getId().toString();
   }
}
