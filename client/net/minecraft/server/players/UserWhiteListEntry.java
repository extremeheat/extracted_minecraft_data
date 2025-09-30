package net.minecraft.server.players;

import com.google.gson.JsonObject;

public class UserWhiteListEntry extends StoredUserEntry<NameAndId> {
   public UserWhiteListEntry(NameAndId var1) {
      super(var1);
   }

   public UserWhiteListEntry(JsonObject var1) {
      super(NameAndId.fromJson(var1));
   }

   protected void serialize(JsonObject var1) {
      if (this.getUser() != null) {
         ((NameAndId)this.getUser()).appendTo(var1);
      }
   }
}
