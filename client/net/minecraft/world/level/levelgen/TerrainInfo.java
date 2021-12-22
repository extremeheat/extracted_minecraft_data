package net.minecraft.world.level.levelgen;

public record TerrainInfo(double a, double b, double c) {
   private final double offset;
   private final double factor;
   private final double jaggedness;

   public TerrainInfo(double var1, double var3, double var5) {
      super();
      this.offset = var1;
      this.factor = var3;
      this.jaggedness = var5;
   }

   public double offset() {
      return this.offset;
   }

   public double factor() {
      return this.factor;
   }

   public double jaggedness() {
      return this.jaggedness;
   }
}
