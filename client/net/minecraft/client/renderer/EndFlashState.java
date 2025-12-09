package net.minecraft.client.renderer;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class EndFlashState {
   public static final int SOUND_DELAY_IN_TICKS = 30;
   private static final int FLASH_INTERVAL_IN_TICKS = 600;
   private static final int MAX_FLASH_OFFSET_IN_TICKS = 200;
   private static final int MIN_FLASH_DURATION_IN_TICKS = 100;
   private static final int MAX_FLASH_DURATION_IN_TICKS = 380;
   private long flashSeed;
   private int offset;
   private int duration;
   private float intensity;
   private float oldIntensity;
   private float xAngle;
   private float yAngle;

   public EndFlashState() {
      super();
   }

   public void tick(long var1) {
      this.calculateFlashParameters(var1);
      this.oldIntensity = this.intensity;
      this.intensity = this.calculateIntensity(var1);
   }

   private void calculateFlashParameters(long var1) {
      long var3 = var1 / 600L;
      if (var3 != this.flashSeed) {
         RandomSource var5 = RandomSource.create(var3);
         var5.nextFloat();
         this.offset = Mth.randomBetweenInclusive(var5, 0, 200);
         this.duration = Mth.randomBetweenInclusive(var5, 100, Math.min(380, 600 - this.offset));
         this.xAngle = Mth.randomBetween(var5, -60.0F, 10.0F);
         this.yAngle = Mth.randomBetween(var5, -180.0F, 180.0F);
         this.flashSeed = var3;
      }

   }

   private float calculateIntensity(long var1) {
      long var3 = var1 % 600L;
      return var3 >= (long)this.offset && var3 <= (long)(this.offset + this.duration) ? Mth.sin((double)((float)(var3 - (long)this.offset) * 3.1415927F / (float)this.duration)) : 0.0F;
   }

   public float getXAngle() {
      return this.xAngle;
   }

   public float getYAngle() {
      return this.yAngle;
   }

   public float getIntensity(float var1) {
      return Mth.lerp(var1, this.oldIntensity, this.intensity);
   }

   public boolean flashStartedThisTick() {
      return this.intensity > 0.0F && this.oldIntensity <= 0.0F;
   }
}
