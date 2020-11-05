package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockStateProvider {
   public static final Codec<BlockStateProvider> CODEC;

   public BlockStateProvider() {
      super();
   }

   protected abstract BlockStateProviderType<?> type();

   public abstract BlockState getState(Random var1, BlockPos var2);

   static {
      CODEC = Registry.BLOCKSTATE_PROVIDER_TYPES.dispatch(BlockStateProvider::type, BlockStateProviderType::codec);
   }
}
