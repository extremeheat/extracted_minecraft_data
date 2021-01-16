package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Iterator;

public class UserWhiteList extends StoredUserList<GameProfile, UserWhiteListEntry> {
   public UserWhiteList(File var1) {
      super(var1);
   }

   protected StoredUserEntry<GameProfile> createEntry(JsonObject var1) {
      return new UserWhiteListEntry(var1);
   }

   public boolean isWhiteListed(GameProfile var1) {
      return this.contains(var1);
   }

   public String[] getUserList() {
      String[] var1 = new String[this.getEntries().size()];
      int var2 = 0;

      StoredUserEntry var4;
      for(Iterator var3 = this.getEntries().iterator(); var3.hasNext(); var1[var2++] = ((GameProfile)var4.getUser()).getName()) {
         var4 = (StoredUserEntry)var3.next();
      }

      return var1;
   }

   protected String getKeyForUser(GameProfile var1) {
      return var1.getId().toString();
   }

   // $FF: synthetic method
   protected String getKeyForUser(Object var1) {
      return this.getKeyForUser((GameProfile)var1);
   }
}
