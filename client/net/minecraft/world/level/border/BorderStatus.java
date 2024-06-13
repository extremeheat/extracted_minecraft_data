package net.minecraft.world.level.border;

public enum BorderStatus {
   GROWING(4259712),
   SHRINKING(16724016),
   STATIONARY(2138367);

   private final int color;

   private BorderStatus(final int nullxx) {
      this.color = nullxx;
   }

   public int getColor() {
      return this.color;
   }
}
