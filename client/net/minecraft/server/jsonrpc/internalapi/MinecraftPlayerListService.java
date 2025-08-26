package net.minecraft.server.jsonrpc.internalapi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;

public class MinecraftPlayerListService {
   private final JsonRpcLogger jsonRpcLogger;
   private final DedicatedServer server;

   public MinecraftPlayerListService(DedicatedServer var1, JsonRpcLogger var2) {
      super();
      this.jsonRpcLogger = var2;
      this.server = var1;
   }

   public List<ServerPlayer> getPlayers() {
      return this.server.getPlayerList().getPlayers();
   }

   public ServerPlayer getPlayer(UUID var1) {
      return this.server.getPlayerList().getPlayer(var1);
   }

   public CompletableFuture<Optional<NameAndId>> getUser(Optional<UUID> var1, Optional<String> var2) {
      if (var1.isPresent()) {
         Optional var3 = this.server.services().nameToIdCache().get((UUID)var1.get());
         return var3.isPresent() ? CompletableFuture.completedFuture(var3) : CompletableFuture.supplyAsync(() -> this.server.services().sessionService().fetchProfile((UUID)var1.get(), true), Util.nonCriticalIoPool()).thenApply(Optional::ofNullable).thenApply((var0) -> var0.map((var0x) -> new NameAndId(var0x.profile())));
      } else {
         return var2.isPresent() ? CompletableFuture.supplyAsync(() -> this.server.services().nameToIdCache().get((String)var2.get()), Util.nonCriticalIoPool()) : CompletableFuture.completedFuture(Optional.empty());
      }
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
      this.jsonRpcLogger.log(var2, "Remove player '{}'", var1.getName().getString());
   }

   public ServerPlayer getPlayerByName(String var1) {
      return this.server.getPlayerList().getPlayerByName(var1);
   }
}
