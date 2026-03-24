package net.minecraft.server.jsonrpc.internalapi;

import java.util.Collection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.jsonrpc.JsonRpcLogger;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserBanListEntry;

public class MinecraftBanListServiceImpl implements MinecraftBanListService {
   private final MinecraftServer server;
   private final JsonRpcLogger jsonrpcLogger;

   public MinecraftBanListServiceImpl(final MinecraftServer server, final JsonRpcLogger jsonrpcLogger) {
      super();
      this.server = server;
      this.jsonrpcLogger = jsonrpcLogger;
   }

   public void addUserBan(final UserBanListEntry ban, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Add player '{}' to banlist. Reason: '{}'", ban.getDisplayName(), ban.getReasonMessage().getString());
      this.server.getPlayerList().getBans().add(ban);
   }

   public void removeUserBan(final NameAndId nameAndId, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Remove player '{}' from banlist", nameAndId);
      this.server.getPlayerList().getBans().remove(nameAndId);
   }

   public void clearUserBans(final ClientInfo clientInfo) {
      this.server.getPlayerList().getBans().clear();
   }

   public Collection<UserBanListEntry> getUserBanEntries() {
      return this.server.getPlayerList().getBans().getEntries();
   }

   public Collection<IpBanListEntry> getIpBanEntries() {
      return this.server.getPlayerList().getIpBans().getEntries();
   }

   public void addIpBan(final IpBanListEntry ipBanEntry, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Add ip '{}' to ban list", ipBanEntry.getUser());
      this.server.getPlayerList().getIpBans().add(ipBanEntry);
   }

   public void clearIpBans(final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Clear ip ban list");
      this.server.getPlayerList().getIpBans().clear();
   }

   public void removeIpBan(final String ip, final ClientInfo clientInfo) {
      this.jsonrpcLogger.log(clientInfo, "Remove ip '{}' from ban list", ip);
      this.server.getPlayerList().getIpBans().remove(ip);
   }
}
