package net.minecraft.util.debugchart;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import net.minecraft.Util;
import net.minecraft.network.protocol.game.ClientboundDebugSamplePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

public class DebugSampleSubscriptionTracker {
   public static final int STOP_SENDING_AFTER_TICKS = 200;
   public static final int STOP_SENDING_AFTER_MS = 10000;
   private final PlayerList playerList;
   private final EnumMap<RemoteDebugSampleType, Map<ServerPlayer, DebugSampleSubscriptionTracker.SubscriptionStartedAt>> subscriptions;
   private final Queue<DebugSampleSubscriptionTracker.SubscriptionRequest> subscriptionRequestQueue = new LinkedList<>();

   public DebugSampleSubscriptionTracker(PlayerList var1) {
      super();
      this.playerList = var1;
      this.subscriptions = new EnumMap<>(RemoteDebugSampleType.class);

      for (RemoteDebugSampleType var5 : RemoteDebugSampleType.values()) {
         this.subscriptions.put(var5, Maps.newHashMap());
      }
   }

   public boolean shouldLogSamples(RemoteDebugSampleType var1) {
      return !this.subscriptions.get(var1).isEmpty();
   }

   public void broadcast(ClientboundDebugSamplePacket var1) {
      for (ServerPlayer var4 : this.subscriptions.get(var1.debugSampleType()).keySet()) {
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
      for (DebugSampleSubscriptionTracker.SubscriptionRequest var5 : this.subscriptionRequestQueue) {
         this.subscriptions.get(var5.sampleType()).put(var5.player(), new DebugSampleSubscriptionTracker.SubscriptionStartedAt(var1, var3));
      }
   }

   private void handleUnsubscriptions(long var1, int var3) {
      for (Map var5 : this.subscriptions.values()) {
         var5.entrySet().removeIf(var4 -> {
            boolean var5x = !this.playerList.isOp(((ServerPlayer)var4.getKey()).getGameProfile());
            DebugSampleSubscriptionTracker.SubscriptionStartedAt var6 = (DebugSampleSubscriptionTracker.SubscriptionStartedAt)var4.getValue();
            return var5x || var3 > var6.tick() + 200 && var1 > var6.millis() + 10000L;
         });
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
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
