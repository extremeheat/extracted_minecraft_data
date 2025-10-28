package net.minecraft.server.notifications;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.level.gamerules.GameRule;

public class EmptyNotificationService implements NotificationService {
   public EmptyNotificationService() {
      super();
   }

   public void playerJoined(ServerPlayer var1) {
   }

   public void playerLeft(ServerPlayer var1) {
   }

   public void serverStarted() {
   }

   public void serverShuttingDown() {
   }

   public void serverSaveStarted() {
   }

   public void serverSaveCompleted() {
   }

   public void serverActivityOccured() {
   }

   public void playerOped(ServerOpListEntry var1) {
   }

   public void playerDeoped(ServerOpListEntry var1) {
   }

   public void playerAddedToAllowlist(NameAndId var1) {
   }

   public void playerRemovedFromAllowlist(NameAndId var1) {
   }

   public void ipBanned(IpBanListEntry var1) {
   }

   public void ipUnbanned(String var1) {
   }

   public void playerBanned(UserBanListEntry var1) {
   }

   public void playerUnbanned(NameAndId var1) {
   }

   public <T> void onGameRuleChanged(GameRule<T> var1, T var2) {
   }

   public void statusHeartbeat() {
   }
}
