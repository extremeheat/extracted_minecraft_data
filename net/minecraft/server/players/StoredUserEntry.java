package net.minecraft.server.players;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class StoredUserEntry {
   @Nullable
   private final Object user;

   public StoredUserEntry(Object var1) {
      this.user = var1;
   }

   protected StoredUserEntry(@Nullable Object var1, JsonObject var2) {
      this.user = var1;
   }

   @Nullable
   Object getUser() {
      return this.user;
   }

   boolean hasExpired() {
      return false;
   }

   protected void serialize(JsonObject var1) {
   }
}
