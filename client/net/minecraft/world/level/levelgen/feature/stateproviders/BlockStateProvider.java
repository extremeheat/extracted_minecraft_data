package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockStateProvider {
   public static final Codec<BlockStateProvider> CODEC;

   public BlockStateProvider() {
      super();
   }

   public static SimpleStateProvider simple(BlockState var0) {
      return new SimpleStateProvider(var0);
   }

   public static SimpleStateProvider simple(Block var0) {
      return new SimpleStateProvider(var0.defaultBlockState());
   }

   protected abstract BlockStateProviderType<?> type();

   public abstract BlockState getState(Random var1, BlockPos var2);

   static {
      CODEC = Registry.BLOCKSTATE_PROVIDER_TYPES.byNameCodec().dispatch(BlockStateProvider::type, BlockStateProviderType::codec);
   }
}
