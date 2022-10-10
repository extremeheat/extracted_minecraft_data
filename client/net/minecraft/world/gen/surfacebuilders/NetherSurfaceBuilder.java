package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

public class NetherSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   private static final IBlockState field_205554_c;
   private static final IBlockState field_205555_d;
   private static final IBlockState field_205556_e;
   private static final IBlockState field_205557_f;
   protected long field_205552_a;
   protected NoiseGeneratorOctaves field_205553_b;

   public NetherSurfaceBuilder() {
      super();
   }

   public void func_205610_a_(Random var1, IChunk var2, Biome var3, int var4, int var5, int var6, double var7, IBlockState var9, IBlockState var10, int var11, long var12, SurfaceBuilderConfig var14) {
      int var15 = var11 + 1;
      int var16 = var4 & 15;
      int var17 = var5 & 15;
      double var18 = 0.03125D;
      boolean var20 = this.field_205553_b.func_205563_a((double)var4 * 0.03125D, (double)var5 * 0.03125D, 0.0D) + var1.nextDouble() * 0.2D > 0.0D;
      boolean var21 = this.field_205553_b.func_205563_a((double)var4 * 0.03125D, 109.0D, (double)var5 * 0.03125D) + var1.nextDouble() * 0.2D > 0.0D;
      int var22 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      BlockPos.MutableBlockPos var23 = new BlockPos.MutableBlockPos();
      int var24 = -1;
      IBlockState var25 = field_205555_d;
      IBlockState var26 = field_205555_d;

      for(int var27 = 127; var27 >= 0; --var27) {
         var23.func_181079_c(var16, var27, var17);
         IBlockState var28 = var2.func_180495_p(var23);
         if (var28.func_177230_c() != null && !var28.func_196958_f()) {
            if (var28.func_177230_c() == var9.func_177230_c()) {
               if (var24 == -1) {
                  if (var22 <= 0) {
                     var25 = field_205554_c;
                     var26 = field_205555_d;
                  } else if (var27 >= var15 - 4 && var27 <= var15 + 1) {
                     var25 = field_205555_d;
                     var26 = field_205555_d;
                     if (var21) {
                        var25 = field_205556_e;
                        var26 = field_205555_d;
                     }

                     if (var20) {
                        var25 = field_205557_f;
                        var26 = field_205557_f;
                     }
                  }

                  if (var27 < var15 && (var25 == null || var25.func_196958_f())) {
                     var25 = var10;
                  }

                  var24 = var22;
                  if (var27 >= var15 - 1) {
                     var2.func_177436_a(var23, var25, false);
                  } else {
                     var2.func_177436_a(var23, var26, false);
                  }
               } else if (var24 > 0) {
                  --var24;
                  var2.func_177436_a(var23, var26, false);
               }
            }
         } else {
            var24 = -1;
         }
      }

   }

   public void func_205548_a(long var1) {
      if (this.field_205552_a != var1 || this.field_205553_b == null) {
         this.field_205553_b = new NoiseGeneratorOctaves(new SharedSeedRandom(var1), 4);
      }

      this.field_205552_a = var1;
   }

   static {
      field_205554_c = Blocks.field_201941_jj.func_176223_P();
      field_205555_d = Blocks.field_150424_aL.func_176223_P();
      field_205556_e = Blocks.field_150351_n.func_176223_P();
      field_205557_f = Blocks.field_150425_aM.func_176223_P();
   }
}
