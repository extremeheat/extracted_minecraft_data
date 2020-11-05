package net.minecraft.server.players;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public abstract class StoredUserEntry<T> {
   @Nullable
   private final T user;

   public StoredUserEntry(@Nullable T var1) {
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

   protected abstract void serialize(JsonObject var1);
}
