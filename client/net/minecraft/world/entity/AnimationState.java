package net.minecraft.world.entity;

import java.util.function.Consumer;

public class AnimationState {
   private static final int STOPPED = -2147483648;
   private int startTick = -2147483648;

   public AnimationState() {
      super();
   }

   public void start(int var1) {
      this.startTick = var1;
   }

   public void startIfStopped(int var1) {
      if (!this.isStarted()) {
         this.start(var1);
      }

   }

   public void animateWhen(boolean var1, int var2) {
      if (var1) {
         this.startIfStopped(var2);
      } else {
         this.stop();
      }

   }

   public void stop() {
      this.startTick = -2147483648;
   }

   public void ifStarted(Consumer<AnimationState> var1) {
      if (this.isStarted()) {
         var1.accept(this);
      }

   }

   public void fastForward(int var1, float var2) {
      if (this.isStarted()) {
         this.startTick -= (int)((float)var1 * var2);
      }
   }

   public long getTimeInMillis(float var1) {
      float var2 = var1 - (float)this.startTick;
      return (long)(var2 * 50.0F);
   }

   public boolean isStarted() {
      return this.startTick != -2147483648;
   }

   public void copyFrom(AnimationState var1) {
      this.startTick = var1.startTick;
   }
}
