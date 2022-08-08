package net.minecraft.world.level.block.entity;

import net.minecraft.util.Mth;

public class ChestLidController {
   private boolean shouldBeOpen;
   private float openness;
   private float oOpenness;

   public ChestLidController() {
      super();
   }

   public void tickLid() {
      this.oOpenness = this.openness;
      float var1 = 0.1F;
      if (!this.shouldBeOpen && this.openness > 0.0F) {
         this.openness = Math.max(this.openness - 0.1F, 0.0F);
      } else if (this.shouldBeOpen && this.openness < 1.0F) {
         this.openness = Math.min(this.openness + 0.1F, 1.0F);
      }

   }

   public float getOpenness(float var1) {
      return Mth.lerp(var1, this.oOpenness, this.openness);
   }

   public void shouldBeOpen(boolean var1) {
      this.shouldBeOpen = var1;
   }
}
