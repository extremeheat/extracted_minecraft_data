package net.minecraft.client;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;

public interface DeltaTracker {
   DeltaTracker ZERO = new DefaultValue(0.0F);
   DeltaTracker ONE = new DefaultValue(1.0F);

   float getGameTimeDeltaTicks();

   float getGameTimeDeltaPartialTick(boolean var1);

   float getRealtimeDeltaTicks();

   public static class DefaultValue implements DeltaTracker {
      private final float value;

      DefaultValue(float var1) {
         super();
         this.value = var1;
      }

      public float getGameTimeDeltaTicks() {
         return this.value;
      }

      public float getGameTimeDeltaPartialTick(boolean var1) {
         return this.value;
      }

      public float getRealtimeDeltaTicks() {
         return this.value;
      }
   }

   public static class Timer implements DeltaTracker {
      private float deltaTicks;
      private float deltaTickResidual;
      private float realtimeDeltaTicks;
      private float pausedDeltaTickResidual;
      private long lastMs;
      private long lastUiMs;
      private final float msPerTick;
      private final FloatUnaryOperator targetMsptProvider;
      private boolean paused;
      private boolean frozen;

      public Timer(float var1, long var2, FloatUnaryOperator var4) {
         super();
         this.msPerTick = 1000.0F / var1;
         this.lastUiMs = this.lastMs = var2;
         this.targetMsptProvider = var4;
      }

      public int advanceTime(long var1, boolean var3) {
         this.advanceRealTime(var1);
         return var3 ? this.advanceGameTime(var1) : 0;
      }

      private int advanceGameTime(long var1) {
         this.deltaTicks = (float)(var1 - this.lastMs) / this.targetMsptProvider.apply(this.msPerTick);
         this.lastMs = var1;
         this.deltaTickResidual += this.deltaTicks;
         int var3 = (int)this.deltaTickResidual;
         this.deltaTickResidual -= (float)var3;
         return var3;
      }

      private void advanceRealTime(long var1) {
         this.realtimeDeltaTicks = (float)(var1 - this.lastUiMs) / this.msPerTick;
         this.lastUiMs = var1;
      }

      public void updatePauseState(boolean var1) {
         if (var1) {
            this.pause();
         } else {
            this.unPause();
         }

      }

      private void pause() {
         if (!this.paused) {
            this.pausedDeltaTickResidual = this.deltaTickResidual;
         }

         this.paused = true;
      }

      private void unPause() {
         if (this.paused) {
            this.deltaTickResidual = this.pausedDeltaTickResidual;
         }

         this.paused = false;
      }

      public void updateFrozenState(boolean var1) {
         this.frozen = var1;
      }

      public float getGameTimeDeltaTicks() {
         return this.deltaTicks;
      }

      public float getGameTimeDeltaPartialTick(boolean var1) {
         if (!var1 && this.frozen) {
            return 1.0F;
         } else {
            return this.paused ? this.pausedDeltaTickResidual : this.deltaTickResidual;
         }
      }

      public float getRealtimeDeltaTicks() {
         return this.realtimeDeltaTicks > 7.0F ? 0.5F : this.realtimeDeltaTicks;
      }
   }
}
