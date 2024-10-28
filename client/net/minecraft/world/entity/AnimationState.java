package net.minecraft.world.entity;

import java.util.function.Consumer;
import net.minecraft.util.Mth;

public class AnimationState {
   private static final long STOPPED = 9223372036854775807L;
   private long lastTime = 9223372036854775807L;
   private long accumulatedTime;

   public AnimationState() {
      super();
   }

   public void start(int var1) {
      this.lastTime = (long)var1 * 1000L / 20L;
      this.accumulatedTime = 0L;
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
      this.lastTime = 9223372036854775807L;
   }

   public void ifStarted(Consumer<AnimationState> var1) {
      if (this.isStarted()) {
         var1.accept(this);
      }

   }

   public void updateTime(float var1, float var2) {
      if (this.isStarted()) {
         long var3 = Mth.lfloor((double)(var1 * 1000.0F / 20.0F));
         this.accumulatedTime += (long)((float)(var3 - this.lastTime) * var2);
         this.lastTime = var3;
      }
   }

   public void fastForward(int var1, float var2) {
      if (this.isStarted()) {
         this.accumulatedTime += (long)((float)(var1 * 1000) * var2) / 20L;
      }
   }

   public long getAccumulatedTime() {
      return this.accumulatedTime;
   }

   public boolean isStarted() {
      return this.lastTime != 9223372036854775807L;
   }
}
