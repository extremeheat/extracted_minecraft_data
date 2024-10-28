package net.minecraft.util.debugchart;

public enum TpsDebugDimensions {
   FULL_TICK,
   TICK_SERVER_METHOD,
   SCHEDULED_TASKS,
   IDLE;

   private TpsDebugDimensions() {
   }

   // $FF: synthetic method
   private static TpsDebugDimensions[] $values() {
      return new TpsDebugDimensions[]{FULL_TICK, TICK_SERVER_METHOD, SCHEDULED_TASKS, IDLE};
   }
}
