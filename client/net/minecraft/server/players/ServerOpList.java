package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Objects;

public class ServerOpList extends StoredUserList<GameProfile, ServerOpListEntry> {
   public ServerOpList(File var1) {
      super(var1);
   }

   @Override
   protected StoredUserEntry<GameProfile> createEntry(JsonObject var1) {
      return new ServerOpListEntry(var1);
   }

   @Override
   public String[] getUserList() {
      return this.getEntries().stream().map(StoredUserEntry::getUser).filter(Objects::nonNull).map(GameProfile::getName).toArray(var0 -> new String[var0]);
   }

   public boolean canBypassPlayerLimit(GameProfile var1) {
      ServerOpListEntry var2 = this.get(var1);
      return var2 != null ? var2.getBypassesPlayerLimit() : false;
   }

   protected String getKeyForUser(GameProfile var1) {
      return var1.getId().toString();
   }
}
