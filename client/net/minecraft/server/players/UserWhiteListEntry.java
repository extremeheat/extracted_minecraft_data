package net.minecraft.server.players;

import com.google.gson.JsonObject;

public class UserWhiteListEntry extends StoredUserEntry<NameAndId> {
   public UserWhiteListEntry(final NameAndId user) {
      super(user);
   }

   public UserWhiteListEntry(final JsonObject object) {
      super(NameAndId.fromJson(object));
   }

   protected void serialize(final JsonObject object) {
      if (this.getUser() != null) {
         ((NameAndId)this.getUser()).appendTo(object);
      }
   }
}
