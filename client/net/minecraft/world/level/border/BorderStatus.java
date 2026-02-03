package net.minecraft.world.level.border;

public enum BorderStatus {
   GROWING(4259712),
   SHRINKING(16724016),
   STATIONARY(2138367);

   private final int color;

   private BorderStatus(final int color) {
      this.color = color;
   }

   public int getColor() {
      return this.color;
   }

   // $FF: synthetic method
   private static BorderStatus[] $values() {
      return new BorderStatus[]{GROWING, SHRINKING, STATIONARY};
   }
}
