package net.minecraft.util;

public class BinaryAnimator {
   private final int animationLength;
   private final EasingType easing;
   private int ticks;
   private int ticksOld;

   public BinaryAnimator(final int animationLength, final EasingType easing) {
      super();
      this.animationLength = animationLength;
      this.easing = easing;
   }

   public BinaryAnimator(final int animationLength) {
      this(animationLength, EasingType.LINEAR);
   }

   public void tick(final boolean active) {
      this.ticksOld = this.ticks;
      if (active) {
         if (this.ticks < this.animationLength) {
            ++this.ticks;
         }
      } else if (this.ticks > 0) {
         --this.ticks;
      }

   }

   public float getFactor(final float partialTicks) {
      float factor = Mth.lerp(partialTicks, (float)this.ticksOld, (float)this.ticks) / (float)this.animationLength;
      return this.easing.apply(factor);
   }
}
