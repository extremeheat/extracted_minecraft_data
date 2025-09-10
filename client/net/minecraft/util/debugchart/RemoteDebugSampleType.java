package net.minecraft.util.debugchart;

import net.minecraft.util.debug.DebugSubscription;
import net.minecraft.util.debug.DebugSubscriptions;

public enum RemoteDebugSampleType {
   TICK_TIME(DebugSubscriptions.DEDICATED_SERVER_TICK_TIME);

   private final DebugSubscription<?> subscription;

   private RemoteDebugSampleType(final DebugSubscription<?> var3) {
      this.subscription = var3;
   }

   public DebugSubscription<?> subscription() {
      return this.subscription;
   }

   // $FF: synthetic method
   private static RemoteDebugSampleType[] $values() {
      return new RemoteDebugSampleType[]{TICK_TIME};
   }
}
