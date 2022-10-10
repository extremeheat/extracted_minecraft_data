package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class IceSpikeFeature extends Feature<NoFeatureConfig> {
   public IceSpikeFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      while(var1.func_175623_d(var4) && var4.func_177956_o() > 2) {
         var4 = var4.func_177977_b();
      }

      if (var1.func_180495_p(var4).func_177230_c() != Blocks.field_196604_cC) {
         return false;
      } else {
         var4 = var4.func_177981_b(var3.nextInt(4));
         int var6 = var3.nextInt(4) + 7;
         int var7 = var6 / 4 + var3.nextInt(2);
         if (var7 > 1 && var3.nextInt(60) == 0) {
            var4 = var4.func_177981_b(10 + var3.nextInt(30));
         }

         int var8;
         int var10;
         for(var8 = 0; var8 < var6; ++var8) {
            float var9 = (1.0F - (float)var8 / (float)var6) * (float)var7;
            var10 = MathHelper.func_76123_f(var9);

            for(int var11 = -var10; var11 <= var10; ++var11) {
               float var12 = (float)MathHelper.func_76130_a(var11) - 0.25F;

               for(int var13 = -var10; var13 <= var10; ++var13) {
                  float var14 = (float)MathHelper.func_76130_a(var13) - 0.25F;
                  if ((var11 == 0 && var13 == 0 || var12 * var12 + var14 * var14 <= var9 * var9) && (var11 != -var10 && var11 != var10 && var13 != -var10 && var13 != var10 || var3.nextFloat() <= 0.75F)) {
                     IBlockState var15 = var1.func_180495_p(var4.func_177982_a(var11, var8, var13));
                     Block var16 = var15.func_177230_c();
                     if (var15.func_196958_f() || Block.func_196245_f(var16) || var16 == Blocks.field_196604_cC || var16 == Blocks.field_150432_aD) {
                        this.func_202278_a(var1, var4.func_177982_a(var11, var8, var13), Blocks.field_150403_cj.func_176223_P());
                     }

                     if (var8 != 0 && var10 > 1) {
                        var15 = var1.func_180495_p(var4.func_177982_a(var11, -var8, var13));
                        var16 = var15.func_177230_c();
                        if (var15.func_196958_f() || Block.func_196245_f(var16) || var16 == Blocks.field_196604_cC || var16 == Blocks.field_150432_aD) {
                           this.func_202278_a(var1, var4.func_177982_a(var11, -var8, var13), Blocks.field_150403_cj.func_176223_P());
                        }
                     }
                  }
               }
            }
         }

         var8 = var7 - 1;
         if (var8 < 0) {
            var8 = 0;
         } else if (var8 > 1) {
            var8 = 1;
         }

         for(int var17 = -var8; var17 <= var8; ++var17) {
            for(var10 = -var8; var10 <= var8; ++var10) {
               BlockPos var18 = var4.func_177982_a(var17, -1, var10);
               int var19 = 50;
               if (Math.abs(var17) == 1 && Math.abs(var10) == 1) {
                  var19 = var3.nextInt(5);
               }

               while(var18.func_177956_o() > 50) {
                  IBlockState var20 = var1.func_180495_p(var18);
                  Block var21 = var20.func_177230_c();
                  if (!var20.func_196958_f() && !Block.func_196245_f(var21) && var21 != Blocks.field_196604_cC && var21 != Blocks.field_150432_aD && var21 != Blocks.field_150403_cj) {
                     break;
                  }

                  this.func_202278_a(var1, var18, Blocks.field_150403_cj.func_176223_P());
                  var18 = var18.func_177977_b();
                  --var19;
                  if (var19 <= 0) {
                     var18 = var18.func_177979_c(var3.nextInt(5) + 1);
                     var19 = var3.nextInt(5);
                  }
               }
            }
         }

         return true;
      }
   }
}
