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

   public MinecraftBanListServiceImpl(MinecraftServer var1, JsonRpcLogger var2) {
      super();
      this.server = var1;
      this.jsonrpcLogger = var2;
   }

   public void addUserBan(UserBanListEntry var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Add player '{}' to banlist. Reason: '{}'", var1.getDisplayName(), var1.getReasonMessage().getString());
      this.server.getPlayerList().getBans().add(var1);
   }

   public void removeUserBan(NameAndId var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Remove player '{}' from banlist", var1);
      this.server.getPlayerList().getBans().remove(var1);
   }

   public void clearUserBans(ClientInfo var1) {
      this.server.getPlayerList().getBans().clear();
   }

   public Collection<UserBanListEntry> getUserBanEntries() {
      return this.server.getPlayerList().getBans().getEntries();
   }

   public Collection<IpBanListEntry> getIpBanEntries() {
      return this.server.getPlayerList().getIpBans().getEntries();
   }

   public void addIpBan(IpBanListEntry var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Add ip '{}' to ban list", var1.getUser());
      this.server.getPlayerList().getIpBans().add(var1);
   }

   public void clearIpBans(ClientInfo var1) {
      this.jsonrpcLogger.log(var1, "Clear ip ban list");
      this.server.getPlayerList().getIpBans().clear();
   }

   public void removeIpBan(String var1, ClientInfo var2) {
      this.jsonrpcLogger.log(var2, "Remove ip '{}' from ban list", var1);
      this.server.getPlayerList().getIpBans().remove(var1);
   }
}
