package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class ScatteredOreFeature extends Feature<OreConfiguration> {
   private static final int MAX_DIST_FROM_ORIGIN = 7;

   ScatteredOreFeature(Codec<OreConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<OreConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      RandomSource var3 = var1.random();
      OreConfiguration var4 = (OreConfiguration)var1.config();
      BlockPos var5 = var1.origin();
      int var6 = var3.nextInt(var4.size + 1);
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

      for(int var8 = 0; var8 < var6; ++var8) {
         this.offsetTargetPos(var7, var3, var5, Math.min(var8, 7));
         BlockState var9 = var2.getBlockState(var7);
         Iterator var10 = var4.targetStates.iterator();

         while(var10.hasNext()) {
            OreConfiguration.TargetBlockState var11 = (OreConfiguration.TargetBlockState)var10.next();
            Objects.requireNonNull(var2);
            if (OreFeature.canPlaceOre(var9, var2::getBlockState, var3, var4, var11, var7)) {
               var2.setBlock(var7, var11.state, 2);
               break;
            }
         }
      }

      return true;
   }

   private void offsetTargetPos(BlockPos.MutableBlockPos var1, RandomSource var2, BlockPos var3, int var4) {
      int var5 = this.getRandomPlacementInOneAxisRelativeToOrigin(var2, var4);
      int var6 = this.getRandomPlacementInOneAxisRelativeToOrigin(var2, var4);
      int var7 = this.getRandomPlacementInOneAxisRelativeToOrigin(var2, var4);
      var1.setWithOffset(var3, var5, var6, var7);
   }

   private int getRandomPlacementInOneAxisRelativeToOrigin(RandomSource var1, int var2) {
      return Math.round((var1.nextFloat() - var1.nextFloat()) * (float)var2);
   }
}
