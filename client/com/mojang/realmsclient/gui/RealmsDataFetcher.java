package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsNotification;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.gui.task.DataFetcher;
import com.mojang.realmsclient.gui.task.RepeatedDelayStrategy;
import com.mojang.realmsclient.util.RealmsPersistence;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.Util;

public class RealmsDataFetcher {
   public final DataFetcher dataFetcher = new DataFetcher(Util.ioPool(), TimeUnit.MILLISECONDS, Util.timeSource);
   private final List<DataFetcher.Task<?>> tasks;
   public final DataFetcher.Task<List<RealmsNotification>> notificationsTask;
   public final DataFetcher.Task<RealmsDataFetcher.ServerListData> serverListUpdateTask;
   public final DataFetcher.Task<Integer> pendingInvitesTask;
   public final DataFetcher.Task<Boolean> trialAvailabilityTask;
   public final DataFetcher.Task<RealmsNews> newsTask;
   public final DataFetcher.Task<RealmsServerPlayerLists> onlinePlayersTask;
   public final RealmsNewsManager newsManager = new RealmsNewsManager(new RealmsPersistence());

   public RealmsDataFetcher(RealmsClient var1) {
      super();
      this.serverListUpdateTask = this.dataFetcher
         .createTask(
            "server list",
            () -> {
               com.mojang.realmsclient.dto.RealmsServerList var1x = var1.listRealms();
               return RealmsMainScreen.isSnapshot()
                  ? new RealmsDataFetcher.ServerListData(var1x.servers, var1.listSnapshotEligibleRealms())
                  : new RealmsDataFetcher.ServerListData(var1x.servers, List.of());
            },
            Duration.ofSeconds(60L),
            RepeatedDelayStrategy.CONSTANT
         );
      this.pendingInvitesTask = this.dataFetcher
         .createTask("pending invite count", var1::pendingInvitesCount, Duration.ofSeconds(10L), RepeatedDelayStrategy.exponentialBackoff(360));
      this.trialAvailabilityTask = this.dataFetcher
         .createTask("trial availablity", var1::trialAvailable, Duration.ofSeconds(60L), RepeatedDelayStrategy.exponentialBackoff(60));
      this.newsTask = this.dataFetcher.createTask("unread news", var1::getNews, Duration.ofMinutes(5L), RepeatedDelayStrategy.CONSTANT);
      this.notificationsTask = this.dataFetcher.createTask("notifications", var1::getNotifications, Duration.ofMinutes(5L), RepeatedDelayStrategy.CONSTANT);
      this.onlinePlayersTask = this.dataFetcher.createTask("online players", var1::getLiveStats, Duration.ofSeconds(10L), RepeatedDelayStrategy.CONSTANT);
      this.tasks = List.of(
         this.notificationsTask, this.serverListUpdateTask, this.pendingInvitesTask, this.trialAvailabilityTask, this.newsTask, this.onlinePlayersTask
      );
   }

   public List<DataFetcher.Task<?>> getTasks() {
      return this.tasks;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
