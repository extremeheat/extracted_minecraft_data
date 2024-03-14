package net.minecraft.util.debugchart;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import net.minecraft.Util;
import net.minecraft.network.protocol.game.ClientboundDebugSamplePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public class DebugSampleSubscriptionTracker {
   public static final int STOP_SENDING_AFTER_TICKS = 200;
   public static final int STOP_SENDING_AFTER_MS = 10000;
   private final PlayerList playerList;
   private final EnumMap<RemoteDebugSampleType, Map<ServerPlayer, DebugSampleSubscriptionTracker.SubscriptionStartedAt>> subscriptions;
   private final Queue<DebugSampleSubscriptionTracker.SubscriptionRequest> subscriptionRequestQueue = new LinkedList();

   public DebugSampleSubscriptionTracker(PlayerList var1) {
      super();
      this.playerList = var1;
      this.subscriptions = new EnumMap<>(RemoteDebugSampleType.class);

      for(RemoteDebugSampleType var5 : RemoteDebugSampleType.values()) {
         this.subscriptions.put(var5, Maps.newHashMap());
      }
   }

   public boolean shouldLogSamples(RemoteDebugSampleType var1) {
      return !this.subscriptions.get(var1).isEmpty();
   }

   public void broadcast(ClientboundDebugSamplePacket var1) {
      for(ServerPlayer var4 : this.subscriptions.get(var1.debugSampleType()).keySet()) {
         var4.connection.send(var1);
      }
   }

   public void subscribe(ServerPlayer var1, RemoteDebugSampleType var2) {
      if (this.playerList.isOp(var1.getGameProfile())) {
         this.subscriptionRequestQueue.add(new DebugSampleSubscriptionTracker.SubscriptionRequest(var1, var2));
      }
   }

   public void tick(int var1) {
      long var2 = Util.getMillis();
      this.handleSubscriptions(var2, var1);
      this.handleUnsubscriptions(var2, var1);
   }

   private void handleSubscriptions(long var1, int var3) {
      for(DebugSampleSubscriptionTracker.SubscriptionRequest var5 : this.subscriptionRequestQueue) {
         this.subscriptions.get(var5.sampleType()).put(var5.player(), new DebugSampleSubscriptionTracker.SubscriptionStartedAt(var1, var3));
      }
   }

   private void handleUnsubscriptions(long var1, int var3) {
      for(Map var5 : this.subscriptions.values()) {
         var5.entrySet().removeIf(var4 -> {
            boolean var5xx = !this.playerList.isOp(((ServerPlayer)var4.getKey()).getGameProfile());
            DebugSampleSubscriptionTracker.SubscriptionStartedAt var6 = (DebugSampleSubscriptionTracker.SubscriptionStartedAt)var4.getValue();
            return var5xx || var3 > var6.tick() + 200 && var1 > var6.millis() + 10000L;
         });
      }
   }

   static record SubscriptionRequest(ServerPlayer a, RemoteDebugSampleType b) {
      private final ServerPlayer player;
      private final RemoteDebugSampleType sampleType;

      SubscriptionRequest(ServerPlayer var1, RemoteDebugSampleType var2) {
         super();
         this.player = var1;
         this.sampleType = var2;
      }
   }

   static record SubscriptionStartedAt(long a, int b) {
      private final long millis;
      private final int tick;

      SubscriptionStartedAt(long var1, int var3) {
         super();
         this.millis = var1;
         this.tick = var3;
      }
   }
}
