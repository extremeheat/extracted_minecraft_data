package net.minecraft.world.level.biome;

public class CheckerboardBiomeSourceSettings implements BiomeSourceSettings {
   private Biome[] allowedBiomes;
   private int size;

   public CheckerboardBiomeSourceSettings() {
      super();
      this.allowedBiomes = new Biome[]{Biomes.PLAINS};
      this.size = 1;
   }

   public CheckerboardBiomeSourceSettings setAllowedBiomes(Biome[] var1) {
      this.allowedBiomes = var1;
      return this;
   }

   public CheckerboardBiomeSourceSettings setSize(int var1) {
      this.size = var1;
      return this;
   }

   public Biome[] getAllowedBiomes() {
      return this.allowedBiomes;
   }

   public int getSize() {
      return this.size;
   }
}
