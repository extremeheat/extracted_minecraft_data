package net.minecraft.util;

public class BinaryAnimator {
   private final int animationLength;
   private final EasingType easing;
   private int ticks;
   private int ticksOld;

   public BinaryAnimator(int var1, EasingType var2) {
      super();
      this.animationLength = var1;
      this.easing = var2;
   }

   public BinaryAnimator(int var1) {
      this(var1, EasingType.LINEAR);
   }

   public void tick(boolean var1) {
      this.ticksOld = this.ticks;
      if (var1) {
         if (this.ticks < this.animationLength) {
            ++this.ticks;
         }
      } else if (this.ticks > 0) {
         --this.ticks;
      }

   }

   public float getFactor(float var1) {
      float var2 = Mth.lerp(var1, (float)this.ticksOld, (float)this.ticks) / (float)this.animationLength;
      return this.easing.apply(var2);
   }
}
