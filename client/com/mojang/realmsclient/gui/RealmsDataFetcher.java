package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.gui.task.DataFetcher;
import com.mojang.realmsclient.gui.task.RepeatedDelayStrategy;
import com.mojang.realmsclient.util.RealmsPersistence;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import net.minecraft.Util;

public class RealmsDataFetcher {
   public final DataFetcher dataFetcher;
   private final List<DataFetcher.Task<?>> tasks;
   public final DataFetcher.Task<List<RealmsNotification>> notificationsTask;
   public final DataFetcher.Task<ServerListData> serverListUpdateTask;
   public final DataFetcher.Task<Integer> pendingInvitesTask;
   public final DataFetcher.Task<Boolean> trialAvailabilityTask;
   public final DataFetcher.Task<RealmsNews> newsTask;
   public final DataFetcher.Task<RealmsServerPlayerLists> onlinePlayersTask;
   public final RealmsNewsManager newsManager;

   public RealmsDataFetcher(RealmsClient var1) {
      super();
      this.dataFetcher = new DataFetcher(Util.ioPool(), TimeUnit.MILLISECONDS, Util.timeSource);
      this.newsManager = new RealmsNewsManager(new RealmsPersistence());
      this.serverListUpdateTask = this.dataFetcher.<ServerListData>createTask("server list", () -> {
         com.mojang.realmsclient.dto.RealmsServerList var1x = var1.listRealms();
         return RealmsMainScreen.isSnapshot() ? new ServerListData(var1x.servers, var1.listSnapshotEligibleRealms()) : new ServerListData(var1x.servers, List.of());
      }, Duration.ofSeconds(60L), RepeatedDelayStrategy.CONSTANT);
      DataFetcher var10001 = this.dataFetcher;
      Objects.requireNonNull(var1);
      this.pendingInvitesTask = var10001.<Integer>createTask("pending invite count", var1::pendingInvitesCount, Duration.ofSeconds(10L), RepeatedDelayStrategy.exponentialBackoff(360));
      var10001 = this.dataFetcher;
      Objects.requireNonNull(var1);
      this.trialAvailabilityTask = var10001.<Boolean>createTask("trial availablity", var1::trialAvailable, Duration.ofSeconds(60L), RepeatedDelayStrategy.exponentialBackoff(60));
      var10001 = this.dataFetcher;
      Objects.requireNonNull(var1);
      this.newsTask = var10001.<RealmsNews>createTask("unread news", var1::getNews, Duration.ofMinutes(5L), RepeatedDelayStrategy.CONSTANT);
      var10001 = this.dataFetcher;
      Objects.requireNonNull(var1);
      this.notificationsTask = var10001.<List<RealmsNotification>>createTask("notifications", var1::getNotifications, Duration.ofMinutes(5L), RepeatedDelayStrategy.CONSTANT);
      var10001 = this.dataFetcher;
      Objects.requireNonNull(var1);
      this.onlinePlayersTask = var10001.<RealmsServerPlayerLists>createTask("online players", var1::getLiveStats, Duration.ofSeconds(10L), RepeatedDelayStrategy.CONSTANT);
      this.tasks = List.of(this.notificationsTask, this.serverListUpdateTask, this.pendingInvitesTask, this.trialAvailabilityTask, this.newsTask, this.onlinePlayersTask);
   }

   public List<DataFetcher.Task<?>> getTasks() {
      return this.tasks;
   }

   public static record ServerListData(List<RealmsServer> serverList, List<RealmsServer> availableSnapshotServers) {
      public ServerListData(List<RealmsServer> var1, List<RealmsServer> var2) {
         super();
         this.serverList = var1;
         this.availableSnapshotServers = var2;
      }
   }
}
