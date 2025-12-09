package net.minecraft.server.jsonrpc.internalapi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public interface MinecraftPlayerListService {
   List<ServerPlayer> getPlayers();

   @Nullable ServerPlayer getPlayer(UUID var1);

   default CompletableFuture<Optional<NameAndId>> getUser(Optional<UUID> var1, Optional<String> var2) {
      if (var1.isPresent()) {
         Optional var3 = this.getCachedUserById((UUID)var1.get());
         return var3.isPresent() ? CompletableFuture.completedFuture(var3) : CompletableFuture.supplyAsync(() -> this.fetchUserById((UUID)var1.get()), Util.nonCriticalIoPool());
      } else {
         return var2.isPresent() ? CompletableFuture.supplyAsync(() -> this.fetchUserByName((String)var2.get()), Util.nonCriticalIoPool()) : CompletableFuture.completedFuture(Optional.empty());
      }
   }

   Optional<NameAndId> fetchUserByName(String var1);

   Optional<NameAndId> fetchUserById(UUID var1);

   Optional<NameAndId> getCachedUserById(UUID var1);

   Optional<ServerPlayer> getPlayer(Optional<UUID> var1, Optional<String> var2);

   List<ServerPlayer> getPlayersWithAddress(String var1);

   @Nullable ServerPlayer getPlayerByName(String var1);

   void remove(ServerPlayer var1, ClientInfo var2);
}
