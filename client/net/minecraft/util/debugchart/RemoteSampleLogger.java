package net.minecraft.util.debugchart;

import net.minecraft.network.protocol.game.ClientboundDebugSamplePacket;
import net.minecraft.util.debug.ServerDebugSubscribers;

public class RemoteSampleLogger extends AbstractSampleLogger {
   private final ServerDebugSubscribers subscribers;
   private final RemoteDebugSampleType sampleType;

   public RemoteSampleLogger(int var1, ServerDebugSubscribers var2, RemoteDebugSampleType var3) {
      this(var1, var2, var3, new long[var1]);
   }

   public RemoteSampleLogger(int var1, ServerDebugSubscribers var2, RemoteDebugSampleType var3, long[] var4) {
      super(var1, var4);
      this.subscribers = var2;
      this.sampleType = var3;
   }

   protected void useSample() {
      if (this.subscribers.hasAnySubscriberFor(this.sampleType.subscription())) {
         this.subscribers.broadcastToAll(this.sampleType.subscription(), new ClientboundDebugSamplePacket((long[])this.sample.clone(), this.sampleType));
      }

   }
}
