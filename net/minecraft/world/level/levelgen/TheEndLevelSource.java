package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.BiomeSource;

public class TheEndLevelSource extends NoiseBasedChunkGenerator {
   private final BlockPos dimensionSpawnPosition;

   public TheEndLevelSource(LevelAccessor var1, BiomeSource var2, TheEndGeneratorSettings var3) {
      super(var1, var2, 8, 4, 128, var3, true);
      this.dimensionSpawnPosition = var3.getSpawnPosition();
   }

   protected void fillNoiseColumn(double[] var1, int var2, int var3) {
      double var4 = 1368.824D;
      double var6 = 684.412D;
      double var8 = 17.110300000000002D;
      double var10 = 4.277575000000001D;
      boolean var12 = true;
      boolean var13 = true;
      this.fillNoiseColumn(var1, var2, var3, 1368.824D, 684.412D, 17.110300000000002D, 4.277575000000001D, 64, -3000);
   }

   protected double[] getDepthAndScale(int var1, int var2) {
      return new double[]{(double)this.biomeSource.getHeightValue(var1, var2), 0.0D};
   }

   protected double getYOffset(double var1, double var3, int var5) {
      return 8.0D - var1;
   }

   protected double getTopSlideStart() {
      return (double)((int)super.getTopSlideStart() / 2);
   }

   protected double getBottomSlideStart() {
      return 8.0D;
   }

   public int getSpawnHeight() {
      return 50;
   }

   public int getSeaLevel() {
      return 0;
   }
}
