package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class DeadBushFeature extends Feature<NoFeatureConfig> {
   private static final BlockDeadBush field_197166_a;

   public DeadBushFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      for(IBlockState var6 = var1.func_180495_p(var4); (var6.func_196958_f() || var6.func_203425_a(BlockTags.field_206952_E)) && var4.func_177956_o() > 0; var6 = var1.func_180495_p(var4)) {
         var4 = var4.func_177977_b();
      }

      IBlockState var7 = field_197166_a.func_176223_P();

      for(int var8 = 0; var8 < 4; ++var8) {
         BlockPos var9 = var4.func_177982_a(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.func_175623_d(var9) && var7.func_196955_c(var1, var9)) {
            var1.func_180501_a(var9, var7, 2);
         }
      }

      return true;
   }

   static {
      field_197166_a = (BlockDeadBush)Blocks.field_196555_aI;
   }
}
