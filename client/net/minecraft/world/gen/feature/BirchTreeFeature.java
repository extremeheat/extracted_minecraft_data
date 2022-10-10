package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class BirchTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState field_181629_a;
   private static final IBlockState field_181630_b;
   private final boolean field_150531_a;

   public BirchTreeFeature(boolean var1, boolean var2) {
      super(var1);
      this.field_150531_a = var2;
   }

   public boolean func_208519_a(Set<BlockPos> var1, IWorld var2, Random var3, BlockPos var4) {
      int var5 = var3.nextInt(3) + 5;
      if (this.field_150531_a) {
         var5 += var3.nextInt(7);
      }

      boolean var6 = true;
      if (var4.func_177956_o() >= 1 && var4.func_177956_o() + var5 + 1 <= 256) {
         int var10;
         int var11;
         for(int var7 = var4.func_177956_o(); var7 <= var4.func_177956_o() + 1 + var5; ++var7) {
            byte var8 = 1;
            if (var7 == var4.func_177956_o()) {
               var8 = 0;
            }

            if (var7 >= var4.func_177956_o() + 1 + var5 - 2) {
               var8 = 2;
            }

            BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

            for(var10 = var4.func_177958_n() - var8; var10 <= var4.func_177958_n() + var8 && var6; ++var10) {
               for(var11 = var4.func_177952_p() - var8; var11 <= var4.func_177952_p() + var8 && var6; ++var11) {
                  if (var7 >= 0 && var7 < 256) {
                     if (!this.func_150523_a(var2.func_180495_p(var9.func_181079_c(var10, var7, var11)).func_177230_c())) {
                        var6 = false;
                     }
                  } else {
                     var6 = false;
                  }
               }
            }
         }

         if (!var6) {
            return false;
         } else {
            Block var17 = var2.func_180495_p(var4.func_177977_b()).func_177230_c();
            if ((var17 == Blocks.field_196658_i || Block.func_196245_f(var17) || var17 == Blocks.field_150458_ak) && var4.func_177956_o() < 256 - var5 - 1) {
               this.func_175921_a(var2, var4.func_177977_b());

               int var18;
               for(var18 = var4.func_177956_o() - 3 + var5; var18 <= var4.func_177956_o() + var5; ++var18) {
                  int var19 = var18 - (var4.func_177956_o() + var5);
                  var10 = 1 - var19 / 2;

                  for(var11 = var4.func_177958_n() - var10; var11 <= var4.func_177958_n() + var10; ++var11) {
                     int var12 = var11 - var4.func_177958_n();

                     for(int var13 = var4.func_177952_p() - var10; var13 <= var4.func_177952_p() + var10; ++var13) {
                        int var14 = var13 - var4.func_177952_p();
                        if (Math.abs(var12) != var10 || Math.abs(var14) != var10 || var3.nextInt(2) != 0 && var19 != 0) {
                           BlockPos var15 = new BlockPos(var11, var18, var13);
                           IBlockState var16 = var2.func_180495_p(var15);
                           if (var16.func_196958_f() || var16.func_203425_a(BlockTags.field_206952_E)) {
                              this.func_202278_a(var2, var15, field_181630_b);
                           }
                        }
                     }
                  }
               }

               for(var18 = 0; var18 < var5; ++var18) {
                  IBlockState var20 = var2.func_180495_p(var4.func_177981_b(var18));
                  if (var20.func_196958_f() || var20.func_203425_a(BlockTags.field_206952_E)) {
                     this.func_208520_a(var1, var2, var4.func_177981_b(var18), field_181629_a);
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   static {
      field_181629_a = Blocks.field_196619_M.func_176223_P();
      field_181630_b = Blocks.field_196647_Y.func_176223_P();
   }
}
