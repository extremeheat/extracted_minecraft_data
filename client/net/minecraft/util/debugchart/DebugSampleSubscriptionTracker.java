package net.minecraft.util.debugchart;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.network.protocol.game.ClientboundDebugSamplePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public class DebugSampleSubscriptionTracker {
   public static final int STOP_SENDING_AFTER_TICKS = 200;
   public static final int STOP_SENDING_AFTER_MS = 10000;
   private final PlayerList playerList;
   private final EnumMap<RemoteDebugSampleType, Map<ServerPlayer, SubscriptionStartedAt>> subscriptions;
   private final Queue<SubscriptionRequest> subscriptionRequestQueue = new LinkedList();

   public DebugSampleSubscriptionTracker(PlayerList var1) {
      super();
      this.playerList = var1;
      this.subscriptions = new EnumMap(RemoteDebugSampleType.class);
      RemoteDebugSampleType[] var2 = RemoteDebugSampleType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         RemoteDebugSampleType var5 = var2[var4];
         this.subscriptions.put(var5, Maps.newHashMap());
      }

   }

   public boolean shouldLogSamples(RemoteDebugSampleType var1) {
      return !((Map)this.subscriptions.get(var1)).isEmpty();
   }

   public void broadcast(ClientboundDebugSamplePacket var1) {
      Set var2 = ((Map)this.subscriptions.get(var1.debugSampleType())).keySet();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         ServerPlayer var4 = (ServerPlayer)var3.next();
         var4.connection.send(var1);
      }

   }

   public void subscribe(ServerPlayer var1, RemoteDebugSampleType var2) {
      if (this.playerList.isOp(var1.getGameProfile())) {
         this.subscriptionRequestQueue.add(new SubscriptionRequest(var1, var2));
      }

   }

   public void tick(int var1) {
      long var2 = Util.getMillis();
      this.handleSubscriptions(var2, var1);
      this.handleUnsubscriptions(var2, var1);
   }

   private void handleSubscriptions(long var1, int var3) {
      Iterator var4 = this.subscriptionRequestQueue.iterator();

      while(var4.hasNext()) {
         SubscriptionRequest var5 = (SubscriptionRequest)var4.next();
         ((Map)this.subscriptions.get(var5.sampleType())).put(var5.player(), new SubscriptionStartedAt(var1, var3));
      }

   }

   private void handleUnsubscriptions(long var1, int var3) {
      Iterator var4 = this.subscriptions.values().iterator();

      while(var4.hasNext()) {
         Map var5 = (Map)var4.next();
         var5.entrySet().removeIf((var4x) -> {
            boolean var5 = !this.playerList.isOp(((ServerPlayer)var4x.getKey()).getGameProfile());
            SubscriptionStartedAt var6 = (SubscriptionStartedAt)var4x.getValue();
            return var5 || var3 > var6.tick() + 200 && var1 > var6.millis() + 10000L;
         });
      }

   }

   static record SubscriptionRequest(ServerPlayer player, RemoteDebugSampleType sampleType) {
      SubscriptionRequest(ServerPlayer var1, RemoteDebugSampleType var2) {
         super();
         this.player = var1;
         this.sampleType = var2;
      }

      public ServerPlayer player() {
         return this.player;
      }

      public RemoteDebugSampleType sampleType() {
         return this.sampleType;
      }
   }

   static record SubscriptionStartedAt(long millis, int tick) {
      SubscriptionStartedAt(long var1, int var3) {
         super();
         this.millis = var1;
         this.tick = var3;
      }

      public long millis() {
         return this.millis;
      }

      public int tick() {
         return this.tick;
      }
   }
}
