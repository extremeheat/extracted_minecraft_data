package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class GrassBlock extends SpreadingSnowyDirtBlock implements BonemealableBlock {
   public static final MapCodec<GrassBlock> CODEC = simpleCodec(GrassBlock::new);

   public MapCodec<GrassBlock> codec() {
      return CODEC;
   }

   public GrassBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return var1.getBlockState(var2.above()).isAir();
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      BlockPos var5 = var3.above();
      BlockState var6 = Blocks.SHORT_GRASS.defaultBlockState();
      Optional var7 = var1.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getHolder(VegetationPlacements.GRASS_BONEMEAL);

      label49:
      for(int var8 = 0; var8 < 128; ++var8) {
         BlockPos var9 = var5;

         for(int var10 = 0; var10 < var8 / 16; ++var10) {
            var9 = var9.offset(var2.nextInt(3) - 1, (var2.nextInt(3) - 1) * var2.nextInt(3) / 2, var2.nextInt(3) - 1);
            if (!var1.getBlockState(var9.below()).is(this) || var1.getBlockState(var9).isCollisionShapeFullBlock(var1, var9)) {
               continue label49;
            }
         }

         BlockState var13 = var1.getBlockState(var9);
         if (var13.is(var6.getBlock()) && var2.nextInt(10) == 0) {
            ((BonemealableBlock)var6.getBlock()).performBonemeal(var1, var2, var9, var13);
         }

         if (var13.isAir()) {
            Holder var11;
            if (var2.nextInt(8) == 0) {
               List var12 = ((Biome)var1.getBiome(var9).value()).getGenerationSettings().getFlowerFeatures();
               if (var12.isEmpty()) {
                  continue;
               }

               var11 = ((RandomPatchConfiguration)((ConfiguredFeature)var12.get(0)).config()).feature();
            } else {
               if (!var7.isPresent()) {
                  continue;
               }

               var11 = (Holder)var7.get();
            }

            ((PlacedFeature)var11.value()).place(var1, var1.getChunkSource().getGenerator(), var2, var9);
         }
      }

   }

   public BonemealableBlock.Type getType() {
      return BonemealableBlock.Type.NEIGHBOR_SPREADER;
   }
}
