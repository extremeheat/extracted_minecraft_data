package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CopyPropertiesProvider extends BlockStateProvider {
   public static final MapCodec<CopyPropertiesProvider> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BlockStateProvider.CODEC.fieldOf("source_block_state_provider").forGetter(CopyPropertiesProvider::getBaseBlockState)).apply(i, CopyPropertiesProvider::new));
   private final BlockStateProvider sourceBlockStateProvider;

   public CopyPropertiesProvider(final BlockStateProvider sourceBlockStateProvider) {
      super();
      this.sourceBlockStateProvider = sourceBlockStateProvider;
   }

   public CopyPropertiesProvider(final Block block) {
      super();
      this.sourceBlockStateProvider = BlockStateProvider.simple(block.defaultBlockState());
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.COPY_PROPERTIES_PROVIDER;
   }

   public BlockState getState(final LevelAccessor level, final RandomSource random, final BlockPos pos) {
      return this.sourceBlockStateProvider.getState(level, random, pos).withPropertiesOf(level.getBlockState(pos));
   }

   private BlockStateProvider getBaseBlockState() {
      return this.sourceBlockStateProvider;
   }
}
