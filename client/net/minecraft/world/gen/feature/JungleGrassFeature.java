package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class JungleGrassFeature extends Feature<NoFeatureConfig> {
   public JungleGrassFeature() {
      super();
   }

   public IBlockState func_202357_a(Random var1) {
      return var1.nextInt(4) == 0 ? Blocks.field_196554_aH.func_176223_P() : Blocks.field_150349_c.func_176223_P();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      IBlockState var6 = this.func_202357_a(var3);

      for(IBlockState var7 = var1.func_180495_p(var4); (var7.func_196958_f() || var7.func_203425_a(BlockTags.field_206952_E)) && var4.func_177956_o() > 0; var7 = var1.func_180495_p(var4)) {
         var4 = var4.func_177977_b();
      }

      int var8 = 0;

      for(int var9 = 0; var9 < 128; ++var9) {
         BlockPos var10 = var4.func_177982_a(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.func_175623_d(var10) && var6.func_196955_c(var1, var10)) {
            var1.func_180501_a(var10, var6, 2);
            ++var8;
         }
      }

      return var8 > 0;
   }
}
