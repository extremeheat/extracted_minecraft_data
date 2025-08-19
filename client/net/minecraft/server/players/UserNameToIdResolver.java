package net.minecraft.server.players;

import com.mojang.authlib.GameProfile;
import java.util.Optional;
import java.util.UUID;

public interface UserNameToIdResolver {
   default void add(GameProfile var1) {
      this.add(new NameAndId(var1));
   }

   void add(NameAndId var1);

   Optional<NameAndId> get(String var1);

   Optional<NameAndId> get(UUID var1);

   void resolveOfflineUsers(boolean var1);

   void save();
}
