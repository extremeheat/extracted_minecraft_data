package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Objects;

public class ServerOpList extends StoredUserList<GameProfile, ServerOpListEntry> {
   public ServerOpList(File var1) {
      super(var1);
   }

   protected StoredUserEntry<GameProfile> createEntry(JsonObject var1) {
      return new ServerOpListEntry(var1);
   }

   public String[] getUserList() {
      return (String[])this.getEntries().stream().map(StoredUserEntry::getUser).filter(Objects::nonNull).map(GameProfile::getName).toArray((var0) -> {
         return new String[var0];
      });
   }

   public boolean canBypassPlayerLimit(GameProfile var1) {
      ServerOpListEntry var2 = (ServerOpListEntry)this.get(var1);
      return var2 != null ? var2.getBypassesPlayerLimit() : false;
   }

   protected String getKeyForUser(GameProfile var1) {
      return var1.getId().toString();
   }

   // $FF: synthetic method
   protected String getKeyForUser(Object var1) {
      return this.getKeyForUser((GameProfile)var1);
   }
}
