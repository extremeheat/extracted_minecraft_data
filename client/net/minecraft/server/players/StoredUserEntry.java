package net.minecraft.server.players;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class StoredUserEntry<T> {
   @Nullable
   private final T user;

   public StoredUserEntry(T var1) {
      super();
      this.user = var1;
   }

   protected StoredUserEntry(@Nullable T var1, JsonObject var2) {
      super();
      this.user = var1;
   }

   @Nullable
   T getUser() {
      return this.user;
   }

   boolean hasExpired() {
      return false;
   }

   protected void serialize(JsonObject var1) {
   }
}
