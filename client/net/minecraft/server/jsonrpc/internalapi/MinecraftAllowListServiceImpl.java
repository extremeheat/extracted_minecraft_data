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

   public MinecraftAllowListServiceImpl(final DedicatedServer server, final JsonRpcLogger jsonrpcLogger) {
      super();
      this.server = server;
      this.jsonrpcLogger = jsonrpcLogger;
   }

   public Collection<UserWhiteListEntry> getEntries() {
      return this.server.getPlayerList().getWhiteList().getEntries();
   }

   public boolean add(final UserWhiteListEntry infos, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Add player '{}' to allowlist", infos.getUser());
      return this.server.getPlayerList().getWhiteList().add(infos);
   }

   public void clear(final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Clear allowlist");
      this.server.getPlayerList().getWhiteList().clear();
   }

   public void remove(final NameAndId nameAndId, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Remove player '{}' from allowlist", nameAndId);
      this.server.getPlayerList().getWhiteList().remove(nameAndId);
   }

   public void kickUnlistedPlayers(final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Kick unlisted players");
      this.server.kickUnlistedPlayers();
   }
}
