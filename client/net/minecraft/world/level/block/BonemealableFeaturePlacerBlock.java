package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class BonemealableFeaturePlacerBlock extends Block implements BonemealableBlock {
   public static final MapCodec<BonemealableFeaturePlacerBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(ResourceKey.codec(Registries.CONFIGURED_FEATURE).fieldOf("feature").forGetter(var0x -> var0x.feature), propertiesCodec())
            .apply(var0, BonemealableFeaturePlacerBlock::new)
   );
   private final ResourceKey<ConfiguredFeature<?, ?>> feature;

   @Override
   public MapCodec<BonemealableFeaturePlacerBlock> codec() {
      return CODEC;
   }

   public BonemealableFeaturePlacerBlock(ResourceKey<ConfiguredFeature<?, ?>> var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.feature = var1;
   }

   @Override
   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return var1.getBlockState(var2.above()).isAir();
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      var1.registryAccess()
         .lookup(Registries.CONFIGURED_FEATURE)
         .flatMap(var1x -> var1x.get(this.feature))
         .ifPresent(var3x -> var3x.value().place(var1, var1.getChunkSource().getGenerator(), var2, var3.above()));
   }

   @Override
   public BonemealableBlock.Type getType() {
      return BonemealableBlock.Type.NEIGHBOR_SPREADER;
   }
}
