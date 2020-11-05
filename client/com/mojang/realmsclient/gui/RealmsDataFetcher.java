package com.mojang.realmsclient.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.util.RealmsPersistence;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsDataFetcher {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
   private volatile boolean stopped = true;
   private final Runnable serverListUpdateTask = new RealmsDataFetcher.ServerListUpdateTask();
   private final Runnable pendingInviteUpdateTask = new RealmsDataFetcher.PendingInviteUpdateTask();
   private final Runnable trialAvailabilityTask = new RealmsDataFetcher.TrialAvailabilityTask();
   private final Runnable liveStatsTask = new RealmsDataFetcher.LiveStatsTask();
   private final Runnable unreadNewsTask = new RealmsDataFetcher.UnreadNewsTask();
   private final Set<RealmsServer> removedServers = Sets.newHashSet();
   private List<RealmsServer> servers = Lists.newArrayList();
   private RealmsServerPlayerLists livestats;
   private int pendingInvitesCount;
   private boolean trialAvailable;
   private boolean hasUnreadNews;
   private String newsLink;
   private ScheduledFuture<?> serverListScheduledFuture;
   private ScheduledFuture<?> pendingInviteScheduledFuture;
   private ScheduledFuture<?> trialAvailableScheduledFuture;
   private ScheduledFuture<?> liveStatsScheduledFuture;
   private ScheduledFuture<?> unreadNewsScheduledFuture;
   private final Map<RealmsDataFetcher.Task, Boolean> fetchStatus = new ConcurrentHashMap(RealmsDataFetcher.Task.values().length);

   public RealmsDataFetcher() {
      super();
   }

   public boolean isStopped() {
      return this.stopped;
   }

   public synchronized void init() {
      if (this.stopped) {
         this.stopped = false;
         this.cancelTasks();
         this.scheduleTasks();
      }

   }

   public synchronized void initWithSpecificTaskList() {
      if (this.stopped) {
         this.stopped = false;
         this.cancelTasks();
         this.fetchStatus.put(RealmsDataFetcher.Task.PENDING_INVITE, false);
         this.pendingInviteScheduledFuture = this.scheduler.scheduleAtFixedRate(this.pendingInviteUpdateTask, 0L, 10L, TimeUnit.SECONDS);
         this.fetchStatus.put(RealmsDataFetcher.Task.TRIAL_AVAILABLE, false);
         this.trialAvailableScheduledFuture = this.scheduler.scheduleAtFixedRate(this.trialAvailabilityTask, 0L, 60L, TimeUnit.SECONDS);
         this.fetchStatus.put(RealmsDataFetcher.Task.UNREAD_NEWS, false);
         this.unreadNewsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.unreadNewsTask, 0L, 300L, TimeUnit.SECONDS);
      }

   }

   public boolean isFetchedSinceLastTry(RealmsDataFetcher.Task var1) {
      Boolean var2 = (Boolean)this.fetchStatus.get(var1);
      return var2 == null ? false : var2;
   }

   public void markClean() {
      Iterator var1 = this.fetchStatus.keySet().iterator();

      while(var1.hasNext()) {
         RealmsDataFetcher.Task var2 = (RealmsDataFetcher.Task)var1.next();
         this.fetchStatus.put(var2, false);
      }

   }

   public synchronized void forceUpdate() {
      this.stop();
      this.init();
   }

   public synchronized List<RealmsServer> getServers() {
      return Lists.newArrayList(this.servers);
   }

   public synchronized int getPendingInvitesCount() {
      return this.pendingInvitesCount;
   }

   public synchronized boolean isTrialAvailable() {
      return this.trialAvailable;
   }

   public synchronized RealmsServerPlayerLists getLivestats() {
      return this.livestats;
   }

   public synchronized boolean hasUnreadNews() {
      return this.hasUnreadNews;
   }

   public synchronized String newsLink() {
      return this.newsLink;
   }

   public synchronized void stop() {
      this.stopped = true;
      this.cancelTasks();
   }

   private void scheduleTasks() {
      RealmsDataFetcher.Task[] var1 = RealmsDataFetcher.Task.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         RealmsDataFetcher.Task var4 = var1[var3];
         this.fetchStatus.put(var4, false);
      }

      this.serverListScheduledFuture = this.scheduler.scheduleAtFixedRate(this.serverListUpdateTask, 0L, 60L, TimeUnit.SECONDS);
      this.pendingInviteScheduledFuture = this.scheduler.scheduleAtFixedRate(this.pendingInviteUpdateTask, 0L, 10L, TimeUnit.SECONDS);
      this.trialAvailableScheduledFuture = this.scheduler.scheduleAtFixedRate(this.trialAvailabilityTask, 0L, 60L, TimeUnit.SECONDS);
      this.liveStatsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.liveStatsTask, 0L, 10L, TimeUnit.SECONDS);
      this.unreadNewsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.unreadNewsTask, 0L, 300L, TimeUnit.SECONDS);
   }

   private void cancelTasks() {
      try {
         if (this.serverListScheduledFuture != null) {
            this.serverListScheduledFuture.cancel(false);
         }

         if (this.pendingInviteScheduledFuture != null) {
            this.pendingInviteScheduledFuture.cancel(false);
         }

         if (this.trialAvailableScheduledFuture != null) {
            this.trialAvailableScheduledFuture.cancel(false);
         }

         if (this.liveStatsScheduledFuture != null) {
            this.liveStatsScheduledFuture.cancel(false);
         }

         if (this.unreadNewsScheduledFuture != null) {
            this.unreadNewsScheduledFuture.cancel(false);
         }
      } catch (Exception var2) {
         LOGGER.error("Failed to cancel Realms tasks", var2);
      }

   }

   private synchronized void setServers(List<RealmsServer> var1) {
      int var2 = 0;
      Iterator var3 = this.removedServers.iterator();

      while(var3.hasNext()) {
         RealmsServer var4 = (RealmsServer)var3.next();
         if (var1.remove(var4)) {
            ++var2;
         }
      }

      if (var2 == 0) {
         this.removedServers.clear();
      }

      this.servers = var1;
   }

   public synchronized void removeItem(RealmsServer var1) {
      this.servers.remove(var1);
      this.removedServers.add(var1);
   }

   private boolean isActive() {
      return !this.stopped;
   }

   public static enum Task {
      SERVER_LIST,
      PENDING_INVITE,
      TRIAL_AVAILABLE,
      LIVE_STATS,
      UNREAD_NEWS;

      private Task() {
      }
   }

   class UnreadNewsTask implements Runnable {
      private UnreadNewsTask() {
         super();
      }

      public void run() {
         if (RealmsDataFetcher.this.isActive()) {
            this.getUnreadNews();
         }

      }

      private void getUnreadNews() {
         try {
            RealmsClient var1 = RealmsClient.create();
            RealmsNews var2 = null;

            try {
               var2 = var1.getNews();
            } catch (Exception var5) {
            }

            RealmsPersistence.RealmsPersistenceData var3 = RealmsPersistence.readFile();
            if (var2 != null) {
               String var4 = var2.newsLink;
               if (var4 != null && !var4.equals(var3.newsLink)) {
                  var3.hasUnreadNews = true;
                  var3.newsLink = var4;
                  RealmsPersistence.writeFile(var3);
               }
            }

            RealmsDataFetcher.this.hasUnreadNews = var3.hasUnreadNews;
            RealmsDataFetcher.this.newsLink = var3.newsLink;
            RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.UNREAD_NEWS, true);
         } catch (Exception var6) {
            RealmsDataFetcher.LOGGER.error("Couldn't get unread news", var6);
         }

      }

      // $FF: synthetic method
      UnreadNewsTask(Object var2) {
         this();
      }
   }

   class LiveStatsTask implements Runnable {
      private LiveStatsTask() {
         super();
      }

      public void run() {
         if (RealmsDataFetcher.this.isActive()) {
            this.getLiveStats();
         }

      }

      private void getLiveStats() {
         try {
            RealmsClient var1 = RealmsClient.create();
            RealmsDataFetcher.this.livestats = var1.getLiveStats();
            RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.LIVE_STATS, true);
         } catch (Exception var2) {
            RealmsDataFetcher.LOGGER.error("Couldn't get live stats", var2);
         }

      }

      // $FF: synthetic method
      LiveStatsTask(Object var2) {
         this();
      }
   }

   class TrialAvailabilityTask implements Runnable {
      private TrialAvailabilityTask() {
         super();
      }

      public void run() {
         if (RealmsDataFetcher.this.isActive()) {
            this.getTrialAvailable();
         }

      }

      private void getTrialAvailable() {
         try {
            RealmsClient var1 = RealmsClient.create();
            RealmsDataFetcher.this.trialAvailable = var1.trialAvailable();
            RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.TRIAL_AVAILABLE, true);
         } catch (Exception var2) {
            RealmsDataFetcher.LOGGER.error("Couldn't get trial availability", var2);
         }

      }

      // $FF: synthetic method
      TrialAvailabilityTask(Object var2) {
         this();
      }
   }

   class PendingInviteUpdateTask implements Runnable {
      private PendingInviteUpdateTask() {
         super();
      }

      public void run() {
         if (RealmsDataFetcher.this.isActive()) {
            this.updatePendingInvites();
         }

      }

      private void updatePendingInvites() {
         try {
            RealmsClient var1 = RealmsClient.create();
            RealmsDataFetcher.this.pendingInvitesCount = var1.pendingInvitesCount();
            RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.PENDING_INVITE, true);
         } catch (Exception var2) {
            RealmsDataFetcher.LOGGER.error("Couldn't get pending invite count", var2);
         }

      }

      // $FF: synthetic method
      PendingInviteUpdateTask(Object var2) {
         this();
      }
   }

   class ServerListUpdateTask implements Runnable {
      private ServerListUpdateTask() {
         super();
      }

      public void run() {
         if (RealmsDataFetcher.this.isActive()) {
            this.updateServersList();
         }

      }

      private void updateServersList() {
         try {
            RealmsClient var1 = RealmsClient.create();
            List var2 = var1.listWorlds().servers;
            if (var2 != null) {
               var2.sort(new RealmsServer.McoServerComparator(Minecraft.getInstance().getUser().getName()));
               RealmsDataFetcher.this.setServers(var2);
               RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.SERVER_LIST, true);
            } else {
               RealmsDataFetcher.LOGGER.warn("Realms server list was null or empty");
            }
         } catch (Exception var3) {
            RealmsDataFetcher.this.fetchStatus.put(RealmsDataFetcher.Task.SERVER_LIST, true);
            RealmsDataFetcher.LOGGER.error("Couldn't get server list", var3);
         }

      }

      // $FF: synthetic method
      ServerListUpdateTask(Object var2) {
         this();
      }
   }
}
