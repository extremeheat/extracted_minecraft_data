package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.SmallTreeConfiguration;

public class SpruceFoliagePlacer extends FoliagePlacer {
   public SpruceFoliagePlacer(int var1, int var2) {
      super(var1, var2, FoliagePlacerType.SPRUCE_FOLIAGE_PLACER);
   }

   public SpruceFoliagePlacer(Dynamic var1) {
      this(var1.get("radius").asInt(0), var1.get("radius_random").asInt(0));
   }

   public void createFoliage(LevelSimulatedRW var1, Random var2, SmallTreeConfiguration var3, int var4, int var5, int var6, BlockPos var7, Set var8) {
      int var9 = var2.nextInt(2);
      int var10 = 1;
      byte var11 = 0;

      for(int var12 = var4; var12 >= var5; --var12) {
         this.placeLeavesRow(var1, var2, var3, var4, var7, var12, var9, var8);
         if (var9 >= var10) {
            var9 = var11;
            var11 = 1;
            var10 = Math.min(var10 + 1, var6);
         } else {
            ++var9;
         }
      }

   }

   public int foliageRadius(Random var1, int var2, int var3, SmallTreeConfiguration var4) {
      return this.radius + var1.nextInt(this.radiusRandom + 1);
   }

   protected boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, int var6) {
      return Math.abs(var3) == var6 && Math.abs(var5) == var6 && var6 > 0;
   }

   public int getTreeRadiusForHeight(int var1, int var2, int var3, int var4) {
      return var4 <= 1 ? 0 : 2;
   }
}
