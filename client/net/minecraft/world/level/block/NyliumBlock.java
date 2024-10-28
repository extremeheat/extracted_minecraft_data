package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.lighting.LightEngine;

public class NyliumBlock extends Block implements BonemealableBlock {
   public static final MapCodec<NyliumBlock> CODEC = simpleCodec(NyliumBlock::new);

   public MapCodec<NyliumBlock> codec() {
      return CODEC;
   }

   protected NyliumBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   private static boolean canBeNylium(BlockState var0, LevelReader var1, BlockPos var2) {
      BlockPos var3 = var2.above();
      BlockState var4 = var1.getBlockState(var3);
      int var5 = LightEngine.getLightBlockInto(var1, var0, var2, var4, var3, Direction.UP, var4.getLightBlock(var1, var3));
      return var5 < var1.getMaxLightLevel();
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!canBeNylium(var1, var2, var3)) {
         var2.setBlockAndUpdate(var3, Blocks.NETHERRACK.defaultBlockState());
      }

   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return var1.getBlockState(var2.above()).isAir();
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      BlockState var5 = var1.getBlockState(var3);
      BlockPos var6 = var3.above();
      ChunkGenerator var7 = var1.getChunkSource().getGenerator();
      Registry var8 = var1.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
      if (var5.is(Blocks.CRIMSON_NYLIUM)) {
         this.place(var8, NetherFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL, var1, var7, var2, var6);
      } else if (var5.is(Blocks.WARPED_NYLIUM)) {
         this.place(var8, NetherFeatures.WARPED_FOREST_VEGETATION_BONEMEAL, var1, var7, var2, var6);
         this.place(var8, NetherFeatures.NETHER_SPROUTS_BONEMEAL, var1, var7, var2, var6);
         if (var2.nextInt(8) == 0) {
            this.place(var8, NetherFeatures.TWISTING_VINES_BONEMEAL, var1, var7, var2, var6);
         }
      }

   }

   private void place(Registry<ConfiguredFeature<?, ?>> var1, ResourceKey<ConfiguredFeature<?, ?>> var2, ServerLevel var3, ChunkGenerator var4, RandomSource var5, BlockPos var6) {
      var1.getHolder(var2).ifPresent((var4x) -> {
         ((ConfiguredFeature)var4x.value()).place(var3, var4, var5, var6);
      });
   }

   public BonemealableBlock.Type getType() {
      return BonemealableBlock.Type.NEIGHBOR_SPREADER;
   }
}
