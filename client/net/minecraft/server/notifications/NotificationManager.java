package net.minecraft.server.notifications;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.level.gamerules.GameRule;
import org.jspecify.annotations.Nullable;

public class NotificationManager implements NotificationService {
   private final List<NotificationService> notificationServices = Lists.newArrayList();
   private @Nullable DedicatedServer server;

   public NotificationManager() {
      super();
   }

   public void registerService(final NotificationService notificationService) {
      this.notificationServices.add(notificationService);
   }

   public void setServer(final DedicatedServer server) {
      if (this.server != null) {
         throw new IllegalStateException("Server already set");
      } else {
         this.server = server;
      }
   }

   public @Nullable DedicatedServer server() {
      return this.server;
   }

   public void playerJoined(final ServerPlayer player) {
      this.notificationServices.forEach((notificationService) -> notificationService.playerJoined(player));
   }

   public void playerLeft(final ServerPlayer player) {
      this.notificationServices.forEach((notificationService) -> notificationService.playerLeft(player));
   }

   public void serverStarted() {
      this.notificationServices.forEach(NotificationService::serverStarted);
   }

   public void serverShuttingDown() {
      this.notificationServices.forEach(NotificationService::serverShuttingDown);
   }

   public void serverSaveStarted() {
      this.notificationServices.forEach(NotificationService::serverSaveStarted);
   }

   public void serverSaveCompleted() {
      this.notificationServices.forEach(NotificationService::serverSaveCompleted);
   }

   public void serverActivityOccured() {
      this.notificationServices.forEach(NotificationService::serverActivityOccured);
   }

   public void worldUpgradeStarted() {
      this.notificationServices.forEach((notificationService) -> notificationService.worldUpgradeStarted());
   }

   public void worldUpgradeProgress(final float progressPercentage) {
      this.notificationServices.forEach((notificationService) -> notificationService.worldUpgradeProgress(progressPercentage));
   }

   public void worldUpgradeFinished() {
      this.notificationServices.forEach((notificationService) -> notificationService.worldUpgradeFinished());
   }

   public void worldUpgradeFailed(final String reason) {
      this.notificationServices.forEach((notificationService) -> notificationService.worldUpgradeFailed(reason));
   }

   public void playerOped(final ServerOpListEntry operator) {
      this.notificationServices.forEach((notificationService) -> notificationService.playerOped(operator));
   }

   public void playerDeoped(final ServerOpListEntry operator) {
      this.notificationServices.forEach((notificationService) -> notificationService.playerDeoped(operator));
   }

   public void playerAddedToAllowlist(final NameAndId player) {
      this.notificationServices.forEach((notificationService) -> notificationService.playerAddedToAllowlist(player));
   }

   public void playerRemovedFromAllowlist(final NameAndId player) {
      this.notificationServices.forEach((notificationService) -> notificationService.playerRemovedFromAllowlist(player));
   }

   public void ipBanned(final IpBanListEntry ban) {
      this.notificationServices.forEach((notificationService) -> notificationService.ipBanned(ban));
   }

   public void ipUnbanned(final String ip) {
      this.notificationServices.forEach((notificationService) -> notificationService.ipUnbanned(ip));
   }

   public void playerBanned(final UserBanListEntry ban) {
      this.notificationServices.forEach((notificationService) -> notificationService.playerBanned(ban));
   }

   public void playerUnbanned(final NameAndId player) {
      this.notificationServices.forEach((notificationService) -> notificationService.playerUnbanned(player));
   }

   public <T> void onGameRuleChanged(final GameRule<T> gameRule, final T value) {
      this.notificationServices.forEach((notificationService) -> notificationService.onGameRuleChanged(gameRule, value));
   }

   public void statusHeartbeat() {
      this.notificationServices.forEach(NotificationService::statusHeartbeat);
   }
}
