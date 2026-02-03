package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockStateProvider {
   public static final Codec<BlockStateProvider> CODEC;

   public BlockStateProvider() {
      super();
   }

   public static SimpleStateProvider simple(final BlockState state) {
      return new SimpleStateProvider(state);
   }

   public static SimpleStateProvider simple(final Block block) {
      return new SimpleStateProvider(block.defaultBlockState());
   }

   protected abstract BlockStateProviderType<?> type();

   public abstract BlockState getState(final RandomSource random, final BlockPos pos);

   static {
      CODEC = BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE.byNameCodec().dispatch(BlockStateProvider::type, BlockStateProviderType::codec);
   }
}
