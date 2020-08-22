package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.SmallTreeConfiguration;

public class BlobFoliagePlacer extends FoliagePlacer {
   public BlobFoliagePlacer(int var1, int var2) {
      super(var1, var2, FoliagePlacerType.BLOB_FOLIAGE_PLACER);
   }

   public BlobFoliagePlacer(Dynamic var1) {
      this(var1.get("radius").asInt(0), var1.get("radius_random").asInt(0));
   }

   public void createFoliage(LevelSimulatedRW var1, Random var2, SmallTreeConfiguration var3, int var4, int var5, int var6, BlockPos var7, Set var8) {
      for(int var9 = var4; var9 >= var5; --var9) {
         int var10 = Math.max(var6 - 1 - (var9 - var4) / 2, 0);
         this.placeLeavesRow(var1, var2, var3, var4, var7, var9, var10, var8);
      }

   }

   public int foliageRadius(Random var1, int var2, int var3, SmallTreeConfiguration var4) {
      return this.radius + var1.nextInt(this.radiusRandom + 1);
   }

   protected boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, int var6) {
      return Math.abs(var3) == var6 && Math.abs(var5) == var6 && (var1.nextInt(2) == 0 || var4 == var2);
   }

   public int getTreeRadiusForHeight(int var1, int var2, int var3, int var4) {
      return var4 == 0 ? 0 : 1;
   }
}
