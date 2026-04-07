package com.mojang.blaze3d.systems;

import java.util.OptionalLong;
import org.jspecify.annotations.Nullable;

public class TimerQuery implements AutoCloseable {
   private static final int ROTATIONS = 3;
   private @Nullable CommandEncoder activeEncoder;
   private final GpuQueryPool queryPool;
   private int currentRotationIndex;
   private Status status;
   private final long[] results;

   public TimerQuery() {
      super();
      this.status = TimerQuery.Status.NOT_RECORDING;
      this.results = new long[3];
      this.queryPool = RenderSystem.getDevice().createTimestampQueryPool(6);
   }

   public void beginProfile() {
      if (this.status != TimerQuery.Status.NOT_RECORDING) {
         throw new IllegalStateException("Current profile not ended");
      } else {
         ++this.currentRotationIndex;
         this.currentRotationIndex %= 3;
         this.activeEncoder = RenderSystem.getDevice().createCommandEncoder();
         this.activeEncoder.writeTimestamp(this.queryPool, this.currentRotationIndex * 2);
         this.status = TimerQuery.Status.STARTED;
      }
   }

   public void endProfile() {
      if (this.status == TimerQuery.Status.STARTED && this.activeEncoder != null) {
         this.activeEncoder.writeTimestamp(this.queryPool, this.currentRotationIndex * 2 + 1);
         this.activeEncoder = null;
         this.status = TimerQuery.Status.AWAITING_VALUES;
      } else {
         throw new IllegalStateException("endProfile called before beginProfile");
      }
   }

   public long get() {
      long average = 0L;

      for(int i = 0; i < this.results.length; ++i) {
         average += this.results[i];
      }

      return average / (long)this.results.length;
   }

   public void close() {
      this.queryPool.close();
   }

   public Status getStatus() {
      if (this.status == TimerQuery.Status.AWAITING_VALUES) {
         OptionalLong[] timestamps = this.queryPool.getValues(this.currentRotationIndex * 2, 2);
         OptionalLong startValue = timestamps[0];
         OptionalLong endValue = timestamps[1];
         if (startValue.isPresent() && endValue.isPresent()) {
            long delta = endValue.getAsLong() - startValue.getAsLong();
            this.results[this.currentRotationIndex] = (long)((float)delta * RenderSystem.getDevice().getDeviceInfo().timestampPeriod());
            this.status = TimerQuery.Status.NOT_RECORDING;
         }
      }

      return this.status;
   }

   public static enum Status {
      NOT_RECORDING,
      STARTED,
      AWAITING_VALUES;

      private Status() {
      }

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{NOT_RECORDING, STARTED, AWAITING_VALUES};
      }
   }
}
