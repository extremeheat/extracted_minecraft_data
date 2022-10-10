package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class DefaultSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   public DefaultSurfaceBuilder() {
      super();
   }

   public void func_205610_a_(Random var1, IChunk var2, Biome var3, int var4, int var5, int var6, double var7, IBlockState var9, IBlockState var10, int var11, long var12, SurfaceBuilderConfig var14) {
      this.func_206967_a(var1, var2, var3, var4, var5, var6, var7, var9, var10, var14.func_204108_a(), var14.func_204109_b(), var14.func_204110_c(), var11);
   }

   protected void func_206967_a(Random var1, IChunk var2, Biome var3, int var4, int var5, int var6, double var7, IBlockState var9, IBlockState var10, IBlockState var11, IBlockState var12, IBlockState var13, int var14) {
      IBlockState var15 = var11;
      IBlockState var16 = var12;
      BlockPos.MutableBlockPos var17 = new BlockPos.MutableBlockPos();
      int var18 = -1;
      int var19 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      int var20 = var4 & 15;
      int var21 = var5 & 15;

      for(int var22 = var6; var22 >= 0; --var22) {
         var17.func_181079_c(var20, var22, var21);
         IBlockState var23 = var2.func_180495_p(var17);
         if (var23.func_196958_f()) {
            var18 = -1;
         } else if (var23.func_177230_c() == var9.func_177230_c()) {
            if (var18 == -1) {
               if (var19 <= 0) {
                  var15 = Blocks.field_150350_a.func_176223_P();
                  var16 = var9;
               } else if (var22 >= var14 - 4 && var22 <= var14 + 1) {
                  var15 = var11;
                  var16 = var12;
               }

               if (var22 < var14 && (var15 == null || var15.func_196958_f())) {
                  if (var3.func_180626_a(var17.func_181079_c(var4, var22, var5)) < 0.15F) {
                     var15 = Blocks.field_150432_aD.func_176223_P();
                  } else {
                     var15 = var10;
                  }

                  var17.func_181079_c(var20, var22, var21);
               }

               var18 = var19;
               if (var22 >= var14 - 1) {
                  var2.func_177436_a(var17, var15, false);
               } else if (var22 < var14 - 7 - var19) {
                  var15 = Blocks.field_150350_a.func_176223_P();
                  var16 = var9;
                  var2.func_177436_a(var17, var13, false);
               } else {
                  var2.func_177436_a(var17, var16, false);
               }
            } else if (var18 > 0) {
               --var18;
               var2.func_177436_a(var17, var16, false);
               if (var18 == 0 && var16.func_177230_c() == Blocks.field_150354_m && var19 > 1) {
                  var18 = var1.nextInt(4) + Math.max(0, var22 - 63);
                  var16 = var16.func_177230_c() == Blocks.field_196611_F ? Blocks.field_180395_cM.func_176223_P() : Blocks.field_150322_A.func_176223_P();
               }
            }
         }
      }

   }
}
