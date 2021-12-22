package net.minecraft.util.profiling.metrics.storage;

import java.time.Instant;
import net.minecraft.util.profiling.ProfileResults;

public final class RecordedDeviation {
   public final Instant timestamp;
   public final int tick;
   public final ProfileResults profilerResultAtTick;

   public RecordedDeviation(Instant var1, int var2, ProfileResults var3) {
      super();
      this.timestamp = var1;
      this.tick = var2;
      this.profilerResultAtTick = var3;
   }
}
