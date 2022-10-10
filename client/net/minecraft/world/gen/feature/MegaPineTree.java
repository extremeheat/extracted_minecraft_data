package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class MegaPineTree extends HugeTreesFeature<NoFeatureConfig> {
   private static final IBlockState field_181633_e;
   private static final IBlockState field_181634_f;
   private static final IBlockState field_181635_g;
   private final boolean field_150542_e;

   public MegaPineTree(boolean var1, boolean var2) {
      super(var1, 13, 15, field_181633_e, field_181634_f);
      this.field_150542_e = var2;
   }

   public boolean func_208519_a(Set<BlockPos> var1, IWorld var2, Random var3, BlockPos var4) {
      int var5 = this.func_150533_a(var3);
      if (!this.func_203427_a(var2, var4, var5)) {
         return false;
      } else {
         this.func_150541_c(var2, var4.func_177958_n(), var4.func_177952_p(), var4.func_177956_o() + var5, 0, var3);

         for(int var6 = 0; var6 < var5; ++var6) {
            IBlockState var7 = var2.func_180495_p(var4.func_177981_b(var6));
            if (var7.func_196958_f() || var7.func_203425_a(BlockTags.field_206952_E)) {
               this.func_208520_a(var1, var2, var4.func_177981_b(var6), this.field_76520_b);
            }

            if (var6 < var5 - 1) {
               var7 = var2.func_180495_p(var4.func_177982_a(1, var6, 0));
               if (var7.func_196958_f() || var7.func_203425_a(BlockTags.field_206952_E)) {
                  this.func_208520_a(var1, var2, var4.func_177982_a(1, var6, 0), this.field_76520_b);
               }

               var7 = var2.func_180495_p(var4.func_177982_a(1, var6, 1));
               if (var7.func_196958_f() || var7.func_203425_a(BlockTags.field_206952_E)) {
                  this.func_208520_a(var1, var2, var4.func_177982_a(1, var6, 1), this.field_76520_b);
               }

               var7 = var2.func_180495_p(var4.func_177982_a(0, var6, 1));
               if (var7.func_196958_f() || var7.func_203425_a(BlockTags.field_206952_E)) {
                  this.func_208520_a(var1, var2, var4.func_177982_a(0, var6, 1), this.field_76520_b);
               }
            }
         }

         this.func_180711_a(var2, var3, var4);
         return true;
      }
   }

   private void func_150541_c(IWorld var1, int var2, int var3, int var4, int var5, Random var6) {
      int var7 = var6.nextInt(5) + (this.field_150542_e ? this.field_76522_a : 3);
      int var8 = 0;

      for(int var9 = var4 - var7; var9 <= var4; ++var9) {
         int var10 = var4 - var9;
         int var11 = var5 + MathHelper.func_76141_d((float)var10 / (float)var7 * 3.5F);
         this.func_175925_a(var1, new BlockPos(var2, var9, var3), var11 + (var10 > 0 && var11 == var8 && (var9 & 1) == 0 ? 1 : 0));
         var8 = var11;
      }

   }

   public void func_180711_a(IWorld var1, Random var2, BlockPos var3) {
      this.func_175933_b(var1, var3.func_177976_e().func_177978_c());
      this.func_175933_b(var1, var3.func_177965_g(2).func_177978_c());
      this.func_175933_b(var1, var3.func_177976_e().func_177970_e(2));
      this.func_175933_b(var1, var3.func_177965_g(2).func_177970_e(2));

      for(int var4 = 0; var4 < 5; ++var4) {
         int var5 = var2.nextInt(64);
         int var6 = var5 % 8;
         int var7 = var5 / 8;
         if (var6 == 0 || var6 == 7 || var7 == 0 || var7 == 7) {
            this.func_175933_b(var1, var3.func_177982_a(-3 + var6, 0, -3 + var7));
         }
      }

   }

   private void func_175933_b(IWorld var1, BlockPos var2) {
      for(int var3 = -2; var3 <= 2; ++var3) {
         for(int var4 = -2; var4 <= 2; ++var4) {
            if (Math.abs(var3) != 2 || Math.abs(var4) != 2) {
               this.func_175934_c(var1, var2.func_177982_a(var3, 0, var4));
            }
         }
      }

   }

   private void func_175934_c(IWorld var1, BlockPos var2) {
      for(int var3 = 2; var3 >= -3; --var3) {
         BlockPos var4 = var2.func_177981_b(var3);
         IBlockState var5 = var1.func_180495_p(var4);
         Block var6 = var5.func_177230_c();
         if (var6 == Blocks.field_196658_i || Block.func_196245_f(var6)) {
            this.func_202278_a(var1, var4, field_181635_g);
            break;
         }

         if (!var5.func_196958_f() && var3 < 0) {
            break;
         }
      }

   }

   static {
      field_181633_e = Blocks.field_196618_L.func_176223_P();
      field_181634_f = Blocks.field_196645_X.func_176223_P();
      field_181635_g = Blocks.field_196661_l.func_176223_P();
   }
}
