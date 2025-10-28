package net.minecraft.server.notifications;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.level.gamerules.GameRule;

public class NotificationManager implements NotificationService {
   private final List<NotificationService> notificationServices = Lists.newArrayList();

   public NotificationManager() {
      super();
   }

   public void registerService(NotificationService var1) {
      this.notificationServices.add(var1);
   }

   public void playerJoined(ServerPlayer var1) {
      this.notificationServices.forEach((var1x) -> var1x.playerJoined(var1));
   }

   public void playerLeft(ServerPlayer var1) {
      this.notificationServices.forEach((var1x) -> var1x.playerLeft(var1));
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

   public void playerOped(ServerOpListEntry var1) {
      this.notificationServices.forEach((var1x) -> var1x.playerOped(var1));
   }

   public void playerDeoped(ServerOpListEntry var1) {
      this.notificationServices.forEach((var1x) -> var1x.playerDeoped(var1));
   }

   public void playerAddedToAllowlist(NameAndId var1) {
      this.notificationServices.forEach((var1x) -> var1x.playerAddedToAllowlist(var1));
   }

   public void playerRemovedFromAllowlist(NameAndId var1) {
      this.notificationServices.forEach((var1x) -> var1x.playerRemovedFromAllowlist(var1));
   }

   public void ipBanned(IpBanListEntry var1) {
      this.notificationServices.forEach((var1x) -> var1x.ipBanned(var1));
   }

   public void ipUnbanned(String var1) {
      this.notificationServices.forEach((var1x) -> var1x.ipUnbanned(var1));
   }

   public void playerBanned(UserBanListEntry var1) {
      this.notificationServices.forEach((var1x) -> var1x.playerBanned(var1));
   }

   public void playerUnbanned(NameAndId var1) {
      this.notificationServices.forEach((var1x) -> var1x.playerUnbanned(var1));
   }

   public <T> void onGameRuleChanged(GameRule<T> var1, T var2) {
      this.notificationServices.forEach((var2x) -> var2x.onGameRuleChanged(var1, var2));
   }

   public void statusHeartbeat() {
      this.notificationServices.forEach(NotificationService::statusHeartbeat);
   }
}
