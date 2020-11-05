package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ForestFlowerProvider extends BlockStateProvider {
   public static final Codec<ForestFlowerProvider> CODEC = Codec.unit(() -> {
      return INSTANCE;
   });
   private static final BlockState[] FLOWERS;
   public static final ForestFlowerProvider INSTANCE;

   public ForestFlowerProvider() {
      super();
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.FOREST_FLOWER_PROVIDER;
   }

   public BlockState getState(Random var1, BlockPos var2) {
      double var3 = Mth.clamp((1.0D + Biome.BIOME_INFO_NOISE.getValue((double)var2.getX() / 48.0D, (double)var2.getZ() / 48.0D, false)) / 2.0D, 0.0D, 0.9999D);
      return FLOWERS[(int)(var3 * (double)FLOWERS.length)];
   }

   static {
      FLOWERS = new BlockState[]{Blocks.DANDELION.defaultBlockState(), Blocks.POPPY.defaultBlockState(), Blocks.ALLIUM.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.RED_TULIP.defaultBlockState(), Blocks.ORANGE_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState(), Blocks.LILY_OF_THE_VALLEY.defaultBlockState()};
      INSTANCE = new ForestFlowerProvider();
   }
}
