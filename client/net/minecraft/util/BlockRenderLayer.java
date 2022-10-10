package net.minecraft.util;

public enum BlockRenderLayer {
   SOLID("Solid"),
   CUTOUT_MIPPED("Mipped Cutout"),
   CUTOUT("Cutout"),
   TRANSLUCENT("Translucent");

   private final String field_180338_e;

   private BlockRenderLayer(String var3) {
      this.field_180338_e = var3;
   }

   public String toString() {
      return this.field_180338_e;
   }
}
