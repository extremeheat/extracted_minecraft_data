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

   public void playerJoined(final ServerPlayer player) {
   }

   public void playerLeft(final ServerPlayer player) {
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

   public void worldUpgradeStarted() {
   }

   public void worldUpgradeProgress(final float progressPercentage) {
   }

   public void worldUpgradeFinished() {
   }

   public void worldUpgradeFailed(final String reason) {
   }

   public void playerOped(final ServerOpListEntry operator) {
   }

   public void playerDeoped(final ServerOpListEntry operator) {
   }

   public void playerAddedToAllowlist(final NameAndId player) {
   }

   public void playerRemovedFromAllowlist(final NameAndId player) {
   }

   public void ipBanned(final IpBanListEntry ban) {
   }

   public void ipUnbanned(final String ip) {
   }

   public void playerBanned(final UserBanListEntry ban) {
   }

   public void playerUnbanned(final NameAndId player) {
   }

   public <T> void onGameRuleChanged(final GameRule<T> gameRule, final T value) {
   }

   public void statusHeartbeat() {
   }
}
