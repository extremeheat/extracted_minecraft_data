package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.references.BlockIds;
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

public class GrassBlock extends SpreadingSnowyBlock implements BonemealableBlock {
   public static final MapCodec<GrassBlock> CODEC = simpleCodec(GrassBlock::new);

   public MapCodec<GrassBlock> codec() {
      return CODEC;
   }

   public GrassBlock(final BlockBehaviour.Properties properties) {
      super(properties, BlockIds.DIRT);
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state) {
      return level.getBlockState(pos.above()).isAir();
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state) {
      return true;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state) {
      BlockPos above = pos.above();
      BlockState grass = Blocks.SHORT_GRASS.defaultBlockState();
      Optional<Holder.Reference<PlacedFeature>> grassFeature = level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE).get(VegetationPlacements.GRASS_BONEMEAL);

      label51:
      for(int j = 0; j < 128; ++j) {
         BlockPos testPos = above;

         for(int i = 0; i < j / 16; ++i) {
            testPos = testPos.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
            if (!level.getBlockState(testPos.below()).is(this) || level.getBlockState(testPos).isCollisionShapeFullBlock(level, testPos)) {
               continue label51;
            }
         }

         BlockState testState = level.getBlockState(testPos);
         if (testState.is(grass.getBlock()) && random.nextInt(10) == 0) {
            BonemealableBlock bonemealableBlock = (BonemealableBlock)grass.getBlock();
            if (bonemealableBlock.isValidBonemealTarget(level, testPos, testState)) {
               bonemealableBlock.performBonemeal(level, random, testPos, testState);
            }
         }

         if (testState.isAir()) {
            Holder<PlacedFeature> placementFeature;
            if (random.nextInt(8) == 0) {
               List<ConfiguredFeature<?, ?>> features = ((Biome)level.getBiome(testPos).value()).getGenerationSettings().getFlowerFeatures();
               if (features.isEmpty()) {
                  continue;
               }

               int randomFlowerFeature = random.nextInt(features.size());
               placementFeature = ((RandomPatchConfiguration)((ConfiguredFeature)features.get(randomFlowerFeature)).config()).feature();
            } else {
               if (!grassFeature.isPresent()) {
                  continue;
               }

               placementFeature = (Holder)grassFeature.get();
            }

            ((PlacedFeature)placementFeature.value()).place(level, level.getChunkSource().getGenerator(), random, testPos);
         }
      }

   }

   public BonemealableBlock.Type getType() {
      return BonemealableBlock.Type.NEIGHBOR_SPREADER;
   }
}
