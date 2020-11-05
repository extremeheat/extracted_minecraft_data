package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PlainFlowerProvider extends BlockStateProvider {
   public static final Codec<PlainFlowerProvider> CODEC = Codec.unit(() -> {
      return INSTANCE;
   });
   public static final PlainFlowerProvider INSTANCE = new PlainFlowerProvider();
   private static final BlockState[] LOW_NOISE_FLOWERS;
   private static final BlockState[] HIGH_NOISE_FLOWERS;

   public PlainFlowerProvider() {
      super();
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.PLAIN_FLOWER_PROVIDER;
   }

   public BlockState getState(Random var1, BlockPos var2) {
      double var3 = Biome.BIOME_INFO_NOISE.getValue((double)var2.getX() / 200.0D, (double)var2.getZ() / 200.0D, false);
      if (var3 < -0.8D) {
         return (BlockState)Util.getRandom((Object[])LOW_NOISE_FLOWERS, var1);
      } else {
         return var1.nextInt(3) > 0 ? (BlockState)Util.getRandom((Object[])HIGH_NOISE_FLOWERS, var1) : Blocks.DANDELION.defaultBlockState();
      }
   }

   static {
      LOW_NOISE_FLOWERS = new BlockState[]{Blocks.ORANGE_TULIP.defaultBlockState(), Blocks.RED_TULIP.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState()};
      HIGH_NOISE_FLOWERS = new BlockState[]{Blocks.POPPY.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState()};
   }
}
