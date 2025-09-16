package net.minecraft.server.jsonrpc.internalapi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;

public class MinecraftPlayerListServiceImpl implements MinecraftPlayerListService {
   private final JsonRpcLogger jsonRpcLogger;
   private final DedicatedServer server;

   public MinecraftPlayerListServiceImpl(DedicatedServer var1, JsonRpcLogger var2) {
      super();
      this.jsonRpcLogger = var2;
      this.server = var1;
   }

   public List<ServerPlayer> getPlayers() {
      return this.server.getPlayerList().getPlayers();
   }

   @Nullable
   public ServerPlayer getPlayer(UUID var1) {
      return this.server.getPlayerList().getPlayer(var1);
   }

   public Optional<NameAndId> fetchUserByName(String var1) {
      return this.server.services().nameToIdCache().get(var1);
   }

   public Optional<NameAndId> fetchUserById(UUID var1) {
      return Optional.ofNullable(this.server.services().sessionService().fetchProfile(var1, true)).map((var0) -> new NameAndId(var0.profile()));
   }

   public Optional<NameAndId> getCachedUserById(UUID var1) {
      return this.server.services().nameToIdCache().get(var1);
   }

   public Optional<ServerPlayer> getPlayer(Optional<UUID> var1, Optional<String> var2) {
      if (var1.isPresent()) {
         return Optional.ofNullable(this.server.getPlayerList().getPlayer((UUID)var1.get()));
      } else {
         return var2.isPresent() ? Optional.ofNullable(this.server.getPlayerList().getPlayerByName((String)var2.get())) : Optional.empty();
      }
   }

   public List<ServerPlayer> getPlayersWithAddress(String var1) {
      return this.server.getPlayerList().getPlayersWithAddress(var1);
   }

   public void remove(ServerPlayer var1, ClientInfo var2) {
      this.server.getPlayerList().remove(var1);
      this.jsonRpcLogger.log(var2, "Remove player '{}'", var1.getPlainTextName());
   }

   @Nullable
   public ServerPlayer getPlayerByName(String var1) {
      return this.server.getPlayerList().getPlayerByName(var1);
   }
}
