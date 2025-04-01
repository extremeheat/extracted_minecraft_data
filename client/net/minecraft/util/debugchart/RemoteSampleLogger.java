package net.minecraft.util.debugchart;

import net.minecraft.network.protocol.game.ClientboundDebugSamplePacket;
import net.minecraft.server.level.ServerPlayer;

public class RemoteSampleLogger extends AbstractSampleLogger {
   private final DebugSampleSubscriptionTracker subscriptionTracker;
   private final RemoteDebugSampleType sampleType;

   public RemoteSampleLogger(int var1, DebugSampleSubscriptionTracker var2, RemoteDebugSampleType var3) {
      this(var1, var2, var3, new long[var1]);
   }

   public RemoteSampleLogger(int var1, DebugSampleSubscriptionTracker var2, RemoteDebugSampleType var3, long[] var4) {
      super(var1, var4);
      this.subscriptionTracker = var2;
      this.sampleType = var3;
   }

   protected void useSample() {
      this.subscriptionTracker.broadcast(new ClientboundDebugSamplePacket((long[])this.sample.clone(), this.sampleType));
   }

   public void tick(int var1) {
      this.subscriptionTracker.tick(var1);
   }

   public boolean isEnabled() {
      return this.subscriptionTracker.shouldLogSamples(RemoteDebugSampleType.TICK_TIME);
   }

   public void subscribe(ServerPlayer var1, RemoteDebugSampleType var2) {
      this.subscriptionTracker.subscribe(var1, var2);
   }
}
