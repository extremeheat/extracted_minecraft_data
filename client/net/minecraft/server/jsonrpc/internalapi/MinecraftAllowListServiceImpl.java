package net.minecraft.server.jsonrpc.internalapi;

import java.util.Collection;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserWhiteListEntry;

public class MinecraftAllowListServiceImpl implements MinecraftAllowListService {
   private final DedicatedServer server;
   private final JsonRpcLogger jsonrpcLogger;

   public MinecraftAllowListServiceImpl(DedicatedServer var1, JsonRpcLogger var2) {
      super();
      this.server = var1;
      this.jsonrpcLogger = var2;
   }

   public Collection<UserWhiteListEntry> getEntries() {
      return this.server.getPlayerList().getWhiteList().getEntries();
   }

   public boolean add(UserWhiteListEntry var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Add player '{}' to allowlist", var1.getUser());
      return this.server.getPlayerList().getWhiteList().add(var1);
   }

   public void clear(ClientInfo var1) {
      this.jsonrpcLogger.log(var1, "Clear allowlist");
      this.server.getPlayerList().getWhiteList().clear();
   }

   public void remove(NameAndId var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Remove player '{}' from allowlist", var1);
      this.server.getPlayerList().getWhiteList().remove(var1);
   }

   public void kickUnlistedPlayers(ClientInfo var1) {
      this.jsonrpcLogger.log(var1, "Kick unlisted players");
      this.server.kickUnlistedPlayers();
   }
}
