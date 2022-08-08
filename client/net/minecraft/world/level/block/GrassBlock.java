package net.minecraft.world.level.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class GrassBlock extends SpreadingSnowyDirtBlock implements BonemealableBlock {
   public GrassBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      return var1.getBlockState(var2.above()).isAir();
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      BlockPos var5 = var3.above();
      BlockState var6 = Blocks.GRASS.defaultBlockState();

      label46:
      for(int var7 = 0; var7 < 128; ++var7) {
         BlockPos var8 = var5;

         for(int var9 = 0; var9 < var7 / 16; ++var9) {
            var8 = var8.offset(var2.nextInt(3) - 1, (var2.nextInt(3) - 1) * var2.nextInt(3) / 2, var2.nextInt(3) - 1);
            if (!var1.getBlockState(var8.below()).is(this) || var1.getBlockState(var8).isCollisionShapeFullBlock(var1, var8)) {
               continue label46;
            }
         }

         BlockState var12 = var1.getBlockState(var8);
         if (var12.is(var6.getBlock()) && var2.nextInt(10) == 0) {
            ((BonemealableBlock)var6.getBlock()).performBonemeal(var1, var2, var8, var12);
         }

         if (var12.isAir()) {
            Holder var10;
            if (var2.nextInt(8) == 0) {
               List var11 = ((Biome)var1.getBiome(var8).value()).getGenerationSettings().getFlowerFeatures();
               if (var11.isEmpty()) {
                  continue;
               }

               var10 = ((RandomPatchConfiguration)((ConfiguredFeature)var11.get(0)).config()).feature();
            } else {
               var10 = VegetationPlacements.GRASS_BONEMEAL;
            }

            ((PlacedFeature)var10.value()).place(var1, var1.getChunkSource().getGenerator(), var2, var8);
         }
      }

   }
}
