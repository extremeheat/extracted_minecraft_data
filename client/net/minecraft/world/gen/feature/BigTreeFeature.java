package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class BigTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final IBlockState field_208530_a;
   private static final IBlockState field_208531_b;

   public BigTreeFeature(boolean var1) {
      super(var1);
   }

   private void func_208529_a(IWorld var1, BlockPos var2, float var3) {
      int var4 = (int)((double)var3 + 0.618D);

      for(int var5 = -var4; var5 <= var4; ++var5) {
         for(int var6 = -var4; var6 <= var4; ++var6) {
            if (Math.pow((double)Math.abs(var5) + 0.5D, 2.0D) + Math.pow((double)Math.abs(var6) + 0.5D, 2.0D) <= (double)(var3 * var3)) {
               BlockPos var7 = var2.func_177982_a(var5, 0, var6);
               IBlockState var8 = var1.func_180495_p(var7);
               if (var8.func_196958_f() || var8.func_185904_a() == Material.field_151584_j) {
                  this.func_202278_a(var1, var7, field_208531_b);
               }
            }
         }
      }

   }

   private float func_208527_a(int var1, int var2) {
      if ((float)var2 < (float)var1 * 0.3F) {
         return -1.0F;
      } else {
         float var3 = (float)var1 / 2.0F;
         float var4 = var3 - (float)var2;
         float var5 = MathHelper.func_76129_c(var3 * var3 - var4 * var4);
         if (var4 == 0.0F) {
            var5 = var3;
         } else if (Math.abs(var4) >= var3) {
            return 0.0F;
         }

         return var5 * 0.5F;
      }
   }

   private float func_76495_b(int var1) {
      if (var1 >= 0 && var1 < 5) {
         return var1 != 0 && var1 != 4 ? 3.0F : 2.0F;
      } else {
         return -1.0F;
      }
   }

   private void func_202393_b(IWorld var1, BlockPos var2) {
      for(int var3 = 0; var3 < 5; ++var3) {
         this.func_208529_a(var1, var2.func_177981_b(var3), this.func_76495_b(var3));
      }

   }

   private int func_208523_a(Set<BlockPos> var1, IWorld var2, BlockPos var3, BlockPos var4, boolean var5) {
      if (!var5 && Objects.equals(var3, var4)) {
         return -1;
      } else {
         BlockPos var6 = var4.func_177982_a(-var3.func_177958_n(), -var3.func_177956_o(), -var3.func_177952_p());
         int var7 = this.func_175935_b(var6);
         float var8 = (float)var6.func_177958_n() / (float)var7;
         float var9 = (float)var6.func_177956_o() / (float)var7;
         float var10 = (float)var6.func_177952_p() / (float)var7;

         for(int var11 = 0; var11 <= var7; ++var11) {
            BlockPos var12 = var3.func_177963_a((double)(0.5F + (float)var11 * var8), (double)(0.5F + (float)var11 * var9), (double)(0.5F + (float)var11 * var10));
            if (var5) {
               this.func_208520_a(var1, var2, var12, (IBlockState)field_208530_a.func_206870_a(BlockLog.field_176298_M, this.func_197170_b(var3, var12)));
            } else if (!this.func_150523_a(var2.func_180495_p(var12).func_177230_c())) {
               return var11;
            }
         }

         return -1;
      }
   }

   private int func_175935_b(BlockPos var1) {
      int var2 = MathHelper.func_76130_a(var1.func_177958_n());
      int var3 = MathHelper.func_76130_a(var1.func_177956_o());
      int var4 = MathHelper.func_76130_a(var1.func_177952_p());
      if (var4 > var2 && var4 > var3) {
         return var4;
      } else {
         return var3 > var2 ? var3 : var2;
      }
   }

   private EnumFacing.Axis func_197170_b(BlockPos var1, BlockPos var2) {
      EnumFacing.Axis var3 = EnumFacing.Axis.Y;
      int var4 = Math.abs(var2.func_177958_n() - var1.func_177958_n());
      int var5 = Math.abs(var2.func_177952_p() - var1.func_177952_p());
      int var6 = Math.max(var4, var5);
      if (var6 > 0) {
         if (var4 == var6) {
            var3 = EnumFacing.Axis.X;
         } else if (var5 == var6) {
            var3 = EnumFacing.Axis.Z;
         }
      }

      return var3;
   }

   private void func_208525_a(IWorld var1, int var2, BlockPos var3, List<BigTreeFeature.FoliageCoordinates> var4) {
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         BigTreeFeature.FoliageCoordinates var6 = (BigTreeFeature.FoliageCoordinates)var5.next();
         if (this.func_208522_b(var2, var6.func_177999_q() - var3.func_177956_o())) {
            this.func_202393_b(var1, var6);
         }
      }

   }

   private boolean func_208522_b(int var1, int var2) {
      return (double)var2 >= (double)var1 * 0.2D;
   }

   private void func_208526_a(Set<BlockPos> var1, IWorld var2, BlockPos var3, int var4) {
      this.func_208523_a(var1, var2, var3, var3.func_177981_b(var4), true);
   }

   private void func_208524_a(Set<BlockPos> var1, IWorld var2, int var3, BlockPos var4, List<BigTreeFeature.FoliageCoordinates> var5) {
      Iterator var6 = var5.iterator();

      while(var6.hasNext()) {
         BigTreeFeature.FoliageCoordinates var7 = (BigTreeFeature.FoliageCoordinates)var6.next();
         int var8 = var7.func_177999_q();
         BlockPos var9 = new BlockPos(var4.func_177958_n(), var8, var4.func_177952_p());
         if (!var9.equals(var7) && this.func_208522_b(var3, var8 - var4.func_177956_o())) {
            this.func_208523_a(var1, var2, var9, var7, true);
         }
      }

   }

   public boolean func_208519_a(Set<BlockPos> var1, IWorld var2, Random var3, BlockPos var4) {
      Random var5 = new Random(var3.nextLong());
      int var6 = this.func_208528_b(var1, var2, var4, 5 + var5.nextInt(12));
      if (var6 == -1) {
         return false;
      } else {
         this.func_175921_a(var2, var4.func_177977_b());
         int var7 = (int)((double)var6 * 0.618D);
         if (var7 >= var6) {
            var7 = var6 - 1;
         }

         double var8 = 1.0D;
         int var10 = (int)(1.382D + Math.pow(1.0D * (double)var6 / 13.0D, 2.0D));
         if (var10 < 1) {
            var10 = 1;
         }

         int var11 = var4.func_177956_o() + var7;
         int var12 = var6 - 5;
         ArrayList var13 = Lists.newArrayList();
         var13.add(new BigTreeFeature.FoliageCoordinates(var4.func_177981_b(var12), var11));

         for(; var12 >= 0; --var12) {
            float var14 = this.func_208527_a(var6, var12);
            if (var14 >= 0.0F) {
               for(int var15 = 0; var15 < var10; ++var15) {
                  double var16 = 1.0D;
                  double var18 = 1.0D * (double)var14 * ((double)var5.nextFloat() + 0.328D);
                  double var20 = (double)(var5.nextFloat() * 2.0F) * 3.141592653589793D;
                  double var22 = var18 * Math.sin(var20) + 0.5D;
                  double var24 = var18 * Math.cos(var20) + 0.5D;
                  BlockPos var26 = var4.func_177963_a(var22, (double)(var12 - 1), var24);
                  BlockPos var27 = var26.func_177981_b(5);
                  if (this.func_208523_a(var1, var2, var26, var27, false) == -1) {
                     int var28 = var4.func_177958_n() - var26.func_177958_n();
                     int var29 = var4.func_177952_p() - var26.func_177952_p();
                     double var30 = (double)var26.func_177956_o() - Math.sqrt((double)(var28 * var28 + var29 * var29)) * 0.381D;
                     int var32 = var30 > (double)var11 ? var11 : (int)var30;
                     BlockPos var33 = new BlockPos(var4.func_177958_n(), var32, var4.func_177952_p());
                     if (this.func_208523_a(var1, var2, var33, var26, false) == -1) {
                        var13.add(new BigTreeFeature.FoliageCoordinates(var26, var33.func_177956_o()));
                     }
                  }
               }
            }
         }

         this.func_208525_a(var2, var6, var4, var13);
         this.func_208526_a(var1, var2, var4, var7);
         this.func_208524_a(var1, var2, var6, var4, var13);
         return true;
      }
   }

   private int func_208528_b(Set<BlockPos> var1, IWorld var2, BlockPos var3, int var4) {
      Block var5 = var2.func_180495_p(var3.func_177977_b()).func_177230_c();
      if (!Block.func_196245_f(var5) && var5 != Blocks.field_196658_i && var5 != Blocks.field_150458_ak) {
         return -1;
      } else {
         int var6 = this.func_208523_a(var1, var2, var3, var3.func_177981_b(var4 - 1), false);
         if (var6 == -1) {
            return var4;
         } else {
            return var6 < 6 ? -1 : var6;
         }
      }
   }

   static {
      field_208530_a = Blocks.field_196617_K.func_176223_P();
      field_208531_b = Blocks.field_196642_W.func_176223_P();
   }

   static class FoliageCoordinates extends BlockPos {
      private final int field_178000_b;

      public FoliageCoordinates(BlockPos var1, int var2) {
         super(var1.func_177958_n(), var1.func_177956_o(), var1.func_177952_p());
         this.field_178000_b = var2;
      }

      public int func_177999_q() {
         return this.field_178000_b;
      }
   }
}
