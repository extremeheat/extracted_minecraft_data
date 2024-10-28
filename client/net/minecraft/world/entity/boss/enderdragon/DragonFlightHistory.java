package net.minecraft.world.entity.boss.enderdragon;

import java.util.Arrays;
import net.minecraft.util.Mth;

public class DragonFlightHistory {
   public static final int LENGTH = 64;
   private static final int MASK = 63;
   private final Sample[] samples = new Sample[64];
   private int head = -1;

   public DragonFlightHistory() {
      super();
      Arrays.fill(this.samples, new Sample(0.0, 0.0F));
   }

   public void copyFrom(DragonFlightHistory var1) {
      System.arraycopy(var1.samples, 0, this.samples, 0, 64);
      this.head = var1.head;
   }

   public void record(double var1, float var3) {
      Sample var4 = new Sample(var1, var3);
      if (this.head < 0) {
         Arrays.fill(this.samples, var4);
      }

      if (++this.head == 64) {
         this.head = 0;
      }

      this.samples[this.head] = var4;
   }

   public Sample get(int var1) {
      return this.samples[this.head - var1 & 63];
   }

   public Sample get(int var1, float var2) {
      Sample var3 = this.get(var1);
      Sample var4 = this.get(var1 + 1);
      return new Sample(Mth.lerp((double)var2, var4.y, var3.y), Mth.rotLerp(var2, var4.yRot, var3.yRot));
   }

   public static record Sample(double y, float yRot) {
      final double y;
      final float yRot;

      public Sample(double var1, float var3) {
         super();
         this.y = var1;
         this.yRot = var3;
      }

      public double y() {
         return this.y;
      }

      public float yRot() {
         return this.yRot;
      }
   }
}
