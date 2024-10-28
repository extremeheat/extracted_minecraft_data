package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class MossBlock extends Block implements BonemealableBlock {
   public static final MapCodec<MossBlock> CODEC = simpleCodec(MossBlock::new);

   public MapCodec<MossBlock> codec() {
      return CODEC;
   }

   public MossBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return var1.getBlockState(var2.above()).isAir();
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      var1.registryAccess().registry(Registries.CONFIGURED_FEATURE).flatMap((var0) -> {
         return var0.getHolder(CaveFeatures.MOSS_PATCH_BONEMEAL);
      }).ifPresent((var3x) -> {
         ((ConfiguredFeature)var3x.value()).place(var1, var1.getChunkSource().getGenerator(), var2, var3.above());
      });
   }

   public BonemealableBlock.Type getType() {
      return BonemealableBlock.Type.NEIGHBOR_SPREADER;
   }
}
