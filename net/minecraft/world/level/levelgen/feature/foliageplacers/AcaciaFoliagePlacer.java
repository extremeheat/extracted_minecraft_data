package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.SmallTreeConfiguration;

public class AcaciaFoliagePlacer extends FoliagePlacer {
   public AcaciaFoliagePlacer(int var1, int var2) {
      super(var1, var2, FoliagePlacerType.ACACIA_FOLIAGE_PLACER);
   }

   public AcaciaFoliagePlacer(Dynamic var1) {
      this(var1.get("radius").asInt(0), var1.get("radius_random").asInt(0));
   }

   public void createFoliage(LevelSimulatedRW var1, Random var2, SmallTreeConfiguration var3, int var4, int var5, int var6, BlockPos var7, Set var8) {
      var3.foliagePlacer.placeLeavesRow(var1, var2, var3, var4, var7, 0, var6, var8);
      var3.foliagePlacer.placeLeavesRow(var1, var2, var3, var4, var7, 1, 1, var8);
      BlockPos var9 = var7.above();

      int var10;
      for(var10 = -1; var10 <= 1; ++var10) {
         for(int var11 = -1; var11 <= 1; ++var11) {
            this.placeLeaf(var1, var2, var9.offset(var10, 0, var11), var3, var8);
         }
      }

      for(var10 = 2; var10 <= var6 - 1; ++var10) {
         this.placeLeaf(var1, var2, var9.east(var10), var3, var8);
         this.placeLeaf(var1, var2, var9.west(var10), var3, var8);
         this.placeLeaf(var1, var2, var9.south(var10), var3, var8);
         this.placeLeaf(var1, var2, var9.north(var10), var3, var8);
      }

   }

   public int foliageRadius(Random var1, int var2, int var3, SmallTreeConfiguration var4) {
      return this.radius + var1.nextInt(this.radiusRandom + 1);
   }

   protected boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, int var6) {
      return Math.abs(var3) == var6 && Math.abs(var5) == var6 && var6 > 0;
   }

   public int getTreeRadiusForHeight(int var1, int var2, int var3, int var4) {
      return var4 == 0 ? 0 : 2;
   }
}
