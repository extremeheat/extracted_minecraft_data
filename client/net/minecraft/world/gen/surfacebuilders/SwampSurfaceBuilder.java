package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class SwampSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   public SwampSurfaceBuilder() {
      super();
   }

   public void func_205610_a_(Random var1, IChunk var2, Biome var3, int var4, int var5, int var6, double var7, IBlockState var9, IBlockState var10, int var11, long var12, SurfaceBuilderConfig var14) {
      double var15 = Biome.field_180281_af.func_151601_a((double)var4 * 0.25D, (double)var5 * 0.25D);
      if (var15 > 0.0D) {
         int var17 = var4 & 15;
         int var18 = var5 & 15;
         BlockPos.MutableBlockPos var19 = new BlockPos.MutableBlockPos();

         for(int var20 = var6; var20 >= 0; --var20) {
            var19.func_181079_c(var17, var20, var18);
            if (!var2.func_180495_p(var19).func_196958_f()) {
               if (var20 == 62 && var2.func_180495_p(var19).func_177230_c() != var10.func_177230_c()) {
                  var2.func_177436_a(var19, var10, false);
                  if (var15 < 0.12D) {
                     var2.func_177436_a(var19.func_196234_d(0, 1, 0), Blocks.field_196651_dG.func_176223_P(), false);
                  }
               }
               break;
            }
         }
      }

      Biome.field_203955_aj.func_205610_a_(var1, var2, var3, var4, var5, var6, var7, var9, var10, var11, var12, var14);
   }
}
