package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ForestFlowerFeature extends FlowerFeature {
   private static final Block[] flowers;

   public ForestFlowerFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public BlockState getRandomFlower(Random var1, BlockPos var2) {
      double var3 = Mth.clamp((1.0D + Biome.BIOME_INFO_NOISE.getValue((double)var2.getX() / 48.0D, (double)var2.getZ() / 48.0D)) / 2.0D, 0.0D, 0.9999D);
      Block var5 = flowers[(int)(var3 * (double)flowers.length)];
      return var5 == Blocks.BLUE_ORCHID ? Blocks.POPPY.defaultBlockState() : var5.defaultBlockState();
   }

   static {
      flowers = new Block[]{Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY};
   }
}
