package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public abstract class HugeTreesFeature<T extends IFeatureConfig> extends AbstractTreeFeature<T> {
   protected final int field_76522_a;
   protected final IBlockState field_76520_b;
   protected final IBlockState field_76521_c;
   protected int field_150538_d;

   public HugeTreesFeature(boolean var1, int var2, int var3, IBlockState var4, IBlockState var5) {
      super(var1);
      this.field_76522_a = var2;
      this.field_150538_d = var3;
      this.field_76520_b = var4;
      this.field_76521_c = var5;
   }

   protected int func_150533_a(Random var1) {
      int var2 = var1.nextInt(3) + this.field_76522_a;
      if (this.field_150538_d > 1) {
         var2 += var1.nextInt(this.field_150538_d);
      }

      return var2;
   }

   private boolean func_175926_c(IBlockReader var1, BlockPos var2, int var3) {
      boolean var4 = true;
      if (var2.func_177956_o() >= 1 && var2.func_177956_o() + var3 + 1 <= 256) {
         for(int var5 = 0; var5 <= 1 + var3; ++var5) {
            byte var6 = 2;
            if (var5 == 0) {
               var6 = 1;
            } else if (var5 >= 1 + var3 - 2) {
               var6 = 2;
            }

            for(int var7 = -var6; var7 <= var6 && var4; ++var7) {
               for(int var8 = -var6; var8 <= var6 && var4; ++var8) {
                  if (var2.func_177956_o() + var5 < 0 || var2.func_177956_o() + var5 >= 256 || !this.func_150523_a(var1.func_180495_p(var2.func_177982_a(var7, var5, var8)).func_177230_c())) {
                     var4 = false;
                  }
               }
            }
         }

         return var4;
      } else {
         return false;
      }
   }

   private boolean func_202405_b(IWorld var1, BlockPos var2) {
      BlockPos var3 = var2.func_177977_b();
      Block var4 = var1.func_180495_p(var3).func_177230_c();
      if ((var4 == Blocks.field_196658_i || Block.func_196245_f(var4)) && var2.func_177956_o() >= 2) {
         this.func_175921_a(var1, var3);
         this.func_175921_a(var1, var3.func_177974_f());
         this.func_175921_a(var1, var3.func_177968_d());
         this.func_175921_a(var1, var3.func_177968_d().func_177974_f());
         return true;
      } else {
         return false;
      }
   }

   protected boolean func_203427_a(IWorld var1, BlockPos var2, int var3) {
      return this.func_175926_c(var1, var2, var3) && this.func_202405_b(var1, var2);
   }

   protected void func_175925_a(IWorld var1, BlockPos var2, int var3) {
      int var4 = var3 * var3;

      for(int var5 = -var3; var5 <= var3 + 1; ++var5) {
         for(int var6 = -var3; var6 <= var3 + 1; ++var6) {
            int var7 = Math.min(Math.abs(var5), Math.abs(var5 - 1));
            int var8 = Math.min(Math.abs(var6), Math.abs(var6 - 1));
            if (var7 + var8 < 7 && var7 * var7 + var8 * var8 <= var4) {
               BlockPos var9 = var2.func_177982_a(var5, 0, var6);
               IBlockState var10 = var1.func_180495_p(var9);
               if (var10.func_196958_f() || var10.func_203425_a(BlockTags.field_206952_E)) {
                  this.func_202278_a(var1, var9, this.field_76521_c);
               }
            }
         }
      }

   }

   protected void func_175928_b(IWorld var1, BlockPos var2, int var3) {
      int var4 = var3 * var3;

      for(int var5 = -var3; var5 <= var3; ++var5) {
         for(int var6 = -var3; var6 <= var3; ++var6) {
            if (var5 * var5 + var6 * var6 <= var4) {
               BlockPos var7 = var2.func_177982_a(var5, 0, var6);
               IBlockState var8 = var1.func_180495_p(var7);
               if (var8.func_196958_f() || var8.func_203425_a(BlockTags.field_206952_E)) {
                  this.func_202278_a(var1, var7, this.field_76521_c);
               }
            }
         }
      }

   }
}
