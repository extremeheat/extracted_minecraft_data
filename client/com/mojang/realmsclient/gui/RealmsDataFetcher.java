package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsNews;
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
   public final DataFetcher.Task<List<RealmsServer>> serverListUpdateTask;
   public final DataFetcher.Task<RealmsServerPlayerLists> liveStatsTask;
   public final DataFetcher.Task<Integer> pendingInvitesTask;
   public final DataFetcher.Task<Boolean> trialAvailabilityTask;
   public final DataFetcher.Task<RealmsNews> newsTask;
   public final RealmsNewsManager newsManager;

   public RealmsDataFetcher(RealmsClient var1) {
      super();
      this.dataFetcher = new DataFetcher(Util.ioPool(), TimeUnit.MILLISECONDS, Util.timeSource);
      this.newsManager = new RealmsNewsManager(new RealmsPersistence());
      this.serverListUpdateTask = this.dataFetcher.createTask("server list", () -> {
         return var1.listWorlds().servers;
      }, Duration.ofSeconds(60L), RepeatedDelayStrategy.CONSTANT);
      DataFetcher var10001 = this.dataFetcher;
      Objects.requireNonNull(var1);
      this.liveStatsTask = var10001.createTask("live stats", var1::getLiveStats, Duration.ofSeconds(10L), RepeatedDelayStrategy.CONSTANT);
      var10001 = this.dataFetcher;
      Objects.requireNonNull(var1);
      this.pendingInvitesTask = var10001.createTask("pending invite count", var1::pendingInvitesCount, Duration.ofSeconds(10L), RepeatedDelayStrategy.exponentialBackoff(360));
      var10001 = this.dataFetcher;
      Objects.requireNonNull(var1);
      this.trialAvailabilityTask = var10001.createTask("trial availablity", var1::trialAvailable, Duration.ofSeconds(60L), RepeatedDelayStrategy.exponentialBackoff(60));
      var10001 = this.dataFetcher;
      Objects.requireNonNull(var1);
      this.newsTask = var10001.createTask("unread news", var1::getNews, Duration.ofMinutes(5L), RepeatedDelayStrategy.CONSTANT);
   }
}
