package net.minecraft.world.level.biome;

public class TheEndBiomeSourceSettings implements BiomeSourceSettings {
   private long seed;

   public TheEndBiomeSourceSettings() {
      super();
   }

   public TheEndBiomeSourceSettings setSeed(long var1) {
      this.seed = var1;
      return this;
   }

   public long getSeed() {
      return this.seed;
   }
}
