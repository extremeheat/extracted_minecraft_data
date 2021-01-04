package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PlainFlowerFeature extends FlowerFeature {
   public PlainFlowerFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public BlockState getRandomFlower(Random var1, BlockPos var2) {
      double var3 = Biome.BIOME_INFO_NOISE.getValue((double)var2.getX() / 200.0D, (double)var2.getZ() / 200.0D);
      int var5;
      if (var3 < -0.8D) {
         var5 = var1.nextInt(4);
         switch(var5) {
         case 0:
            return Blocks.ORANGE_TULIP.defaultBlockState();
         case 1:
            return Blocks.RED_TULIP.defaultBlockState();
         case 2:
            return Blocks.PINK_TULIP.defaultBlockState();
         case 3:
         default:
            return Blocks.WHITE_TULIP.defaultBlockState();
         }
      } else if (var1.nextInt(3) > 0) {
         var5 = var1.nextInt(4);
         switch(var5) {
         case 0:
            return Blocks.POPPY.defaultBlockState();
         case 1:
            return Blocks.AZURE_BLUET.defaultBlockState();
         case 2:
            return Blocks.OXEYE_DAISY.defaultBlockState();
         case 3:
         default:
            return Blocks.CORNFLOWER.defaultBlockState();
         }
      } else {
         return Blocks.DANDELION.defaultBlockState();
      }
   }
}
