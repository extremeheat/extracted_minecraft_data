package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class EndIslandFeature extends Feature<NoneFeatureConfiguration> {
   public EndIslandFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      RandomSource var3 = var1.random();
      BlockPos var4 = var1.origin();
      float var5 = (float)var3.nextInt(3) + 4.0F;

      for(int var6 = 0; var5 > 0.5F; --var6) {
         for(int var7 = Mth.floor(-var5); var7 <= Mth.ceil(var5); ++var7) {
            for(int var8 = Mth.floor(-var5); var8 <= Mth.ceil(var5); ++var8) {
               if ((float)(var7 * var7 + var8 * var8) <= (var5 + 1.0F) * (var5 + 1.0F)) {
                  this.setBlock(var2, var4.offset(var7, var6, var8), Blocks.END_STONE.defaultBlockState());
               }
            }
         }

         var5 -= (float)var3.nextInt(2) + 0.5F;
      }

      return true;
   }
}
