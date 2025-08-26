package net.minecraft.server.jsonrpc;

import net.minecraft.server.jsonrpc.api.PlayerDto;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.jsonrpc.methods.BanlistService;
import net.minecraft.server.jsonrpc.methods.GameRulesService;
import net.minecraft.server.jsonrpc.methods.IpBanlistService;
import net.minecraft.server.jsonrpc.methods.OperatorService;
import net.minecraft.server.jsonrpc.methods.ServerStateService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.notifications.NotificationService;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.level.GameRules;

public class JsonRpcNotificationService implements NotificationService {
   private final ManagementServer managementServer;
   private final MinecraftApi minecraftApi;

   public JsonRpcNotificationService(MinecraftApi var1, ManagementServer var2) {
      super();
      this.minecraftApi = var1;
      this.managementServer = var2;
   }

   public void playerJoined(ServerPlayer var1) {
      this.broadcastNotification(OutgoingRpcMethods.PLAYER_JOINED, PlayerDto.from(var1));
   }

   public void playerLeft(ServerPlayer var1) {
      this.broadcastNotification(OutgoingRpcMethods.PLAYER_LEFT, PlayerDto.from(var1));
   }

   public void serverStarted() {
      this.broadcastNotification(OutgoingRpcMethods.SERVER_STARTED);
   }

   public void serverShuttingDown() {
      this.broadcastNotification(OutgoingRpcMethods.SERVER_SHUTTING_DOWN);
   }

   public void serverSaveStarted() {
      this.broadcastNotification(OutgoingRpcMethods.SERVER_SAVE_STARTED);
   }

   public void serverSaveCompleted() {
      this.broadcastNotification(OutgoingRpcMethods.SERVER_SAVE_COMPLETED);
   }

   public void playerOped(ServerOpListEntry var1) {
      this.broadcastNotification(OutgoingRpcMethods.PLAYER_OPED, OperatorService.OperatorDto.from(var1));
   }

   public void playerDeoped(ServerOpListEntry var1) {
      this.broadcastNotification(OutgoingRpcMethods.PLAYER_DEOPED, OperatorService.OperatorDto.from(var1));
   }

   public void playerAddedToAllowlist(NameAndId var1) {
      this.broadcastNotification(OutgoingRpcMethods.PLAYER_ADDED_TO_ALLOWLIST, PlayerDto.from(var1));
   }

   public void playerRemovedFromAllowlist(NameAndId var1) {
      this.broadcastNotification(OutgoingRpcMethods.PLAYER_REMOVED_FROM_ALLOWLIST, PlayerDto.from(var1));
   }

   public void ipBanned(IpBanListEntry var1) {
      this.broadcastNotification(OutgoingRpcMethods.IP_BANNED, IpBanlistService.IpBanDto.from(var1));
   }

   public void ipUnbanned(String var1) {
      this.broadcastNotification(OutgoingRpcMethods.IP_UNBANNED, var1);
   }

   public void playerBanned(UserBanListEntry var1) {
      this.broadcastNotification(OutgoingRpcMethods.PLAYER_BANNED, BanlistService.UserBanDto.from(var1));
   }

   public void playerUnbanned(NameAndId var1) {
      this.broadcastNotification(OutgoingRpcMethods.PLAYER_UNBANNED, PlayerDto.from(var1));
   }

   public void onGameRuleChanged(String var1, GameRules.Value<?> var2) {
      this.broadcastNotification(OutgoingRpcMethods.GAMERULE_CHANGED, GameRulesService.getTypedRule(this.minecraftApi, var1, var2));
   }

   public void statusHeartbeat() {
      this.broadcastNotification(OutgoingRpcMethods.STATUS_HEARTBEAT, ServerStateService.status(this.minecraftApi));
   }

   private void broadcastNotification(OutgoingRpcMethod<Void, ?> var1) {
      this.managementServer.forEachConnection((var1x) -> var1x.sendNotification(var1));
   }

   private <Params> void broadcastNotification(OutgoingRpcMethod<Params, ?> var1, Params var2) {
      this.managementServer.forEachConnection((var2x) -> var2x.sendNotification(var1, var2));
   }
}
