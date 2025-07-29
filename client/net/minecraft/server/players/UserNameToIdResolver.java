package net.minecraft.server.players;

import com.mojang.authlib.GameProfile;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserNameToIdResolver {
   default void add(GameProfile var1) {
      this.add(new NameAndId(var1));
   }

   void add(NameAndId var1);

   Optional<NameAndId> get(String var1);

   CompletableFuture<Optional<NameAndId>> getAsync(String var1);

   Optional<NameAndId> get(UUID var1);

   void resolveOfflineUsers(boolean var1);

   void save();
}
