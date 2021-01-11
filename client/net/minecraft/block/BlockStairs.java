package net.minecraft.block;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStairs extends Block {
   public static final PropertyDirection field_176309_a;
   public static final PropertyEnum<BlockStairs.EnumHalf> field_176308_b;
   public static final PropertyEnum<BlockStairs.EnumShape> field_176310_M;
   private static final int[][] field_150150_a;
   private final Block field_150149_b;
   private final IBlockState field_150151_M;
   private boolean field_150152_N;
   private int field_150153_O;

   protected BlockStairs(IBlockState var1) {
      super(var1.func_177230_c().field_149764_J);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176309_a, EnumFacing.NORTH).func_177226_a(field_176308_b, BlockStairs.EnumHalf.BOTTOM).func_177226_a(field_176310_M, BlockStairs.EnumShape.STRAIGHT));
      this.field_150149_b = var1.func_177230_c();
      this.field_150151_M = var1;
      this.func_149711_c(this.field_150149_b.field_149782_v);
      this.func_149752_b(this.field_150149_b.field_149781_w / 3.0F);
      this.func_149672_a(this.field_150149_b.field_149762_H);
      this.func_149713_g(255);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      if (this.field_150152_N) {
         this.func_149676_a(0.5F * (float)(this.field_150153_O % 2), 0.5F * (float)(this.field_150153_O / 4 % 2), 0.5F * (float)(this.field_150153_O / 2 % 2), 0.5F + 0.5F * (float)(this.field_150153_O % 2), 0.5F + 0.5F * (float)(this.field_150153_O / 4 % 2), 0.5F + 0.5F * (float)(this.field_150153_O / 2 % 2));
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      }

   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public void func_176303_e(IBlockAccess var1, BlockPos var2) {
      if (var1.func_180495_p(var2).func_177229_b(field_176308_b) == BlockStairs.EnumHalf.TOP) {
         this.func_149676_a(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }

   }

   public static boolean func_150148_a(Block var0) {
      return var0 instanceof BlockStairs;
   }

   public static boolean func_176302_a(IBlockAccess var0, BlockPos var1, IBlockState var2) {
      IBlockState var3 = var0.func_180495_p(var1);
      Block var4 = var3.func_177230_c();
      return func_150148_a(var4) && var3.func_177229_b(field_176308_b) == var2.func_177229_b(field_176308_b) && var3.func_177229_b(field_176309_a) == var2.func_177229_b(field_176309_a);
   }

   public int func_176307_f(IBlockAccess var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_176309_a);
      BlockStairs.EnumHalf var5 = (BlockStairs.EnumHalf)var3.func_177229_b(field_176308_b);
      boolean var6 = var5 == BlockStairs.EnumHalf.TOP;
      IBlockState var7;
      Block var8;
      EnumFacing var9;
      if (var4 == EnumFacing.EAST) {
         var7 = var1.func_180495_p(var2.func_177974_f());
         var8 = var7.func_177230_c();
         if (func_150148_a(var8) && var5 == var7.func_177229_b(field_176308_b)) {
            var9 = (EnumFacing)var7.func_177229_b(field_176309_a);
            if (var9 == EnumFacing.NORTH && !func_176302_a(var1, var2.func_177968_d(), var3)) {
               return var6 ? 1 : 2;
            }

            if (var9 == EnumFacing.SOUTH && !func_176302_a(var1, var2.func_177978_c(), var3)) {
               return var6 ? 2 : 1;
            }
         }
      } else if (var4 == EnumFacing.WEST) {
         var7 = var1.func_180495_p(var2.func_177976_e());
         var8 = var7.func_177230_c();
         if (func_150148_a(var8) && var5 == var7.func_177229_b(field_176308_b)) {
            var9 = (EnumFacing)var7.func_177229_b(field_176309_a);
            if (var9 == EnumFacing.NORTH && !func_176302_a(var1, var2.func_177968_d(), var3)) {
               return var6 ? 2 : 1;
            }

            if (var9 == EnumFacing.SOUTH && !func_176302_a(var1, var2.func_177978_c(), var3)) {
               return var6 ? 1 : 2;
            }
         }
      } else if (var4 == EnumFacing.SOUTH) {
         var7 = var1.func_180495_p(var2.func_177968_d());
         var8 = var7.func_177230_c();
         if (func_150148_a(var8) && var5 == var7.func_177229_b(field_176308_b)) {
            var9 = (EnumFacing)var7.func_177229_b(field_176309_a);
            if (var9 == EnumFacing.WEST && !func_176302_a(var1, var2.func_177974_f(), var3)) {
               return var6 ? 2 : 1;
            }

            if (var9 == EnumFacing.EAST && !func_176302_a(var1, var2.func_177976_e(), var3)) {
               return var6 ? 1 : 2;
            }
         }
      } else if (var4 == EnumFacing.NORTH) {
         var7 = var1.func_180495_p(var2.func_177978_c());
         var8 = var7.func_177230_c();
         if (func_150148_a(var8) && var5 == var7.func_177229_b(field_176308_b)) {
            var9 = (EnumFacing)var7.func_177229_b(field_176309_a);
            if (var9 == EnumFacing.WEST && !func_176302_a(var1, var2.func_177974_f(), var3)) {
               return var6 ? 1 : 2;
            }

            if (var9 == EnumFacing.EAST && !func_176302_a(var1, var2.func_177976_e(), var3)) {
               return var6 ? 2 : 1;
            }
         }
      }

      return 0;
   }

   public int func_176305_g(IBlockAccess var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_176309_a);
      BlockStairs.EnumHalf var5 = (BlockStairs.EnumHalf)var3.func_177229_b(field_176308_b);
      boolean var6 = var5 == BlockStairs.EnumHalf.TOP;
      IBlockState var7;
      Block var8;
      EnumFacing var9;
      if (var4 == EnumFacing.EAST) {
         var7 = var1.func_180495_p(var2.func_177976_e());
         var8 = var7.func_177230_c();
         if (func_150148_a(var8) && var5 == var7.func_177229_b(field_176308_b)) {
            var9 = (EnumFacing)var7.func_177229_b(field_176309_a);
            if (var9 == EnumFacing.NORTH && !func_176302_a(var1, var2.func_177978_c(), var3)) {
               return var6 ? 1 : 2;
            }

            if (var9 == EnumFacing.SOUTH && !func_176302_a(var1, var2.func_177968_d(), var3)) {
               return var6 ? 2 : 1;
            }
         }
      } else if (var4 == EnumFacing.WEST) {
         var7 = var1.func_180495_p(var2.func_177974_f());
         var8 = var7.func_177230_c();
         if (func_150148_a(var8) && var5 == var7.func_177229_b(field_176308_b)) {
            var9 = (EnumFacing)var7.func_177229_b(field_176309_a);
            if (var9 == EnumFacing.NORTH && !func_176302_a(var1, var2.func_177978_c(), var3)) {
               return var6 ? 2 : 1;
            }

            if (var9 == EnumFacing.SOUTH && !func_176302_a(var1, var2.func_177968_d(), var3)) {
               return var6 ? 1 : 2;
            }
         }
      } else if (var4 == EnumFacing.SOUTH) {
         var7 = var1.func_180495_p(var2.func_177978_c());
         var8 = var7.func_177230_c();
         if (func_150148_a(var8) && var5 == var7.func_177229_b(field_176308_b)) {
            var9 = (EnumFacing)var7.func_177229_b(field_176309_a);
            if (var9 == EnumFacing.WEST && !func_176302_a(var1, var2.func_177976_e(), var3)) {
               return var6 ? 2 : 1;
            }

            if (var9 == EnumFacing.EAST && !func_176302_a(var1, var2.func_177974_f(), var3)) {
               return var6 ? 1 : 2;
            }
         }
      } else if (var4 == EnumFacing.NORTH) {
         var7 = var1.func_180495_p(var2.func_177968_d());
         var8 = var7.func_177230_c();
         if (func_150148_a(var8) && var5 == var7.func_177229_b(field_176308_b)) {
            var9 = (EnumFacing)var7.func_177229_b(field_176309_a);
            if (var9 == EnumFacing.WEST && !func_176302_a(var1, var2.func_177976_e(), var3)) {
               return var6 ? 1 : 2;
            }

            if (var9 == EnumFacing.EAST && !func_176302_a(var1, var2.func_177974_f(), var3)) {
               return var6 ? 2 : 1;
            }
         }
      }

      return 0;
   }

   public boolean func_176306_h(IBlockAccess var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_176309_a);
      BlockStairs.EnumHalf var5 = (BlockStairs.EnumHalf)var3.func_177229_b(field_176308_b);
      boolean var6 = var5 == BlockStairs.EnumHalf.TOP;
      float var7 = 0.5F;
      float var8 = 1.0F;
      if (var6) {
         var7 = 0.0F;
         var8 = 0.5F;
      }

      float var9 = 0.0F;
      float var10 = 1.0F;
      float var11 = 0.0F;
      float var12 = 0.5F;
      boolean var13 = true;
      IBlockState var14;
      Block var15;
      EnumFacing var16;
      if (var4 == EnumFacing.EAST) {
         var9 = 0.5F;
         var12 = 1.0F;
         var14 = var1.func_180495_p(var2.func_177974_f());
         var15 = var14.func_177230_c();
         if (func_150148_a(var15) && var5 == var14.func_177229_b(field_176308_b)) {
            var16 = (EnumFacing)var14.func_177229_b(field_176309_a);
            if (var16 == EnumFacing.NORTH && !func_176302_a(var1, var2.func_177968_d(), var3)) {
               var12 = 0.5F;
               var13 = false;
            } else if (var16 == EnumFacing.SOUTH && !func_176302_a(var1, var2.func_177978_c(), var3)) {
               var11 = 0.5F;
               var13 = false;
            }
         }
      } else if (var4 == EnumFacing.WEST) {
         var10 = 0.5F;
         var12 = 1.0F;
         var14 = var1.func_180495_p(var2.func_177976_e());
         var15 = var14.func_177230_c();
         if (func_150148_a(var15) && var5 == var14.func_177229_b(field_176308_b)) {
            var16 = (EnumFacing)var14.func_177229_b(field_176309_a);
            if (var16 == EnumFacing.NORTH && !func_176302_a(var1, var2.func_177968_d(), var3)) {
               var12 = 0.5F;
               var13 = false;
            } else if (var16 == EnumFacing.SOUTH && !func_176302_a(var1, var2.func_177978_c(), var3)) {
               var11 = 0.5F;
               var13 = false;
            }
         }
      } else if (var4 == EnumFacing.SOUTH) {
         var11 = 0.5F;
         var12 = 1.0F;
         var14 = var1.func_180495_p(var2.func_177968_d());
         var15 = var14.func_177230_c();
         if (func_150148_a(var15) && var5 == var14.func_177229_b(field_176308_b)) {
            var16 = (EnumFacing)var14.func_177229_b(field_176309_a);
            if (var16 == EnumFacing.WEST && !func_176302_a(var1, var2.func_177974_f(), var3)) {
               var10 = 0.5F;
               var13 = false;
            } else if (var16 == EnumFacing.EAST && !func_176302_a(var1, var2.func_177976_e(), var3)) {
               var9 = 0.5F;
               var13 = false;
            }
         }
      } else if (var4 == EnumFacing.NORTH) {
         var14 = var1.func_180495_p(var2.func_177978_c());
         var15 = var14.func_177230_c();
         if (func_150148_a(var15) && var5 == var14.func_177229_b(field_176308_b)) {
            var16 = (EnumFacing)var14.func_177229_b(field_176309_a);
            if (var16 == EnumFacing.WEST && !func_176302_a(var1, var2.func_177974_f(), var3)) {
               var10 = 0.5F;
               var13 = false;
            } else if (var16 == EnumFacing.EAST && !func_176302_a(var1, var2.func_177976_e(), var3)) {
               var9 = 0.5F;
               var13 = false;
            }
         }
      }

      this.func_149676_a(var9, var7, var11, var10, var8, var12);
      return var13;
   }

   public boolean func_176304_i(IBlockAccess var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_176309_a);
      BlockStairs.EnumHalf var5 = (BlockStairs.EnumHalf)var3.func_177229_b(field_176308_b);
      boolean var6 = var5 == BlockStairs.EnumHalf.TOP;
      float var7 = 0.5F;
      float var8 = 1.0F;
      if (var6) {
         var7 = 0.0F;
         var8 = 0.5F;
      }

      float var9 = 0.0F;
      float var10 = 0.5F;
      float var11 = 0.5F;
      float var12 = 1.0F;
      boolean var13 = false;
      IBlockState var14;
      Block var15;
      EnumFacing var16;
      if (var4 == EnumFacing.EAST) {
         var14 = var1.func_180495_p(var2.func_177976_e());
         var15 = var14.func_177230_c();
         if (func_150148_a(var15) && var5 == var14.func_177229_b(field_176308_b)) {
            var16 = (EnumFacing)var14.func_177229_b(field_176309_a);
            if (var16 == EnumFacing.NORTH && !func_176302_a(var1, var2.func_177978_c(), var3)) {
               var11 = 0.0F;
               var12 = 0.5F;
               var13 = true;
            } else if (var16 == EnumFacing.SOUTH && !func_176302_a(var1, var2.func_177968_d(), var3)) {
               var11 = 0.5F;
               var12 = 1.0F;
               var13 = true;
            }
         }
      } else if (var4 == EnumFacing.WEST) {
         var14 = var1.func_180495_p(var2.func_177974_f());
         var15 = var14.func_177230_c();
         if (func_150148_a(var15) && var5 == var14.func_177229_b(field_176308_b)) {
            var9 = 0.5F;
            var10 = 1.0F;
            var16 = (EnumFacing)var14.func_177229_b(field_176309_a);
            if (var16 == EnumFacing.NORTH && !func_176302_a(var1, var2.func_177978_c(), var3)) {
               var11 = 0.0F;
               var12 = 0.5F;
               var13 = true;
            } else if (var16 == EnumFacing.SOUTH && !func_176302_a(var1, var2.func_177968_d(), var3)) {
               var11 = 0.5F;
               var12 = 1.0F;
               var13 = true;
            }
         }
      } else if (var4 == EnumFacing.SOUTH) {
         var14 = var1.func_180495_p(var2.func_177978_c());
         var15 = var14.func_177230_c();
         if (func_150148_a(var15) && var5 == var14.func_177229_b(field_176308_b)) {
            var11 = 0.0F;
            var12 = 0.5F;
            var16 = (EnumFacing)var14.func_177229_b(field_176309_a);
            if (var16 == EnumFacing.WEST && !func_176302_a(var1, var2.func_177976_e(), var3)) {
               var13 = true;
            } else if (var16 == EnumFacing.EAST && !func_176302_a(var1, var2.func_177974_f(), var3)) {
               var9 = 0.5F;
               var10 = 1.0F;
               var13 = true;
            }
         }
      } else if (var4 == EnumFacing.NORTH) {
         var14 = var1.func_180495_p(var2.func_177968_d());
         var15 = var14.func_177230_c();
         if (func_150148_a(var15) && var5 == var14.func_177229_b(field_176308_b)) {
            var16 = (EnumFacing)var14.func_177229_b(field_176309_a);
            if (var16 == EnumFacing.WEST && !func_176302_a(var1, var2.func_177976_e(), var3)) {
               var13 = true;
            } else if (var16 == EnumFacing.EAST && !func_176302_a(var1, var2.func_177974_f(), var3)) {
               var9 = 0.5F;
               var10 = 1.0F;
               var13 = true;
            }
         }
      }

      if (var13) {
         this.func_149676_a(var9, var7, var11, var10, var8, var12);
      }

      return var13;
   }

   public void func_180638_a(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.func_176303_e(var1, var2);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      boolean var7 = this.func_176306_h(var1, var2);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      if (var7 && this.func_176304_i(var1, var2)) {
         super.func_180638_a(var1, var2, var3, var4, var5, var6);
      }

      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      this.field_150149_b.func_180655_c(var1, var2, var3, var4);
   }

   public void func_180649_a(World var1, BlockPos var2, EntityPlayer var3) {
      this.field_150149_b.func_180649_a(var1, var2, var3);
   }

   public void func_176206_d(World var1, BlockPos var2, IBlockState var3) {
      this.field_150149_b.func_176206_d(var1, var2, var3);
   }

   public int func_176207_c(IBlockAccess var1, BlockPos var2) {
      return this.field_150149_b.func_176207_c(var1, var2);
   }

   public float func_149638_a(Entity var1) {
      return this.field_150149_b.func_149638_a(var1);
   }

   public EnumWorldBlockLayer func_180664_k() {
      return this.field_150149_b.func_180664_k();
   }

   public int func_149738_a(World var1) {
      return this.field_150149_b.func_149738_a(var1);
   }

   public AxisAlignedBB func_180646_a(World var1, BlockPos var2) {
      return this.field_150149_b.func_180646_a(var1, var2);
   }

   public Vec3 func_176197_a(World var1, BlockPos var2, Entity var3, Vec3 var4) {
      return this.field_150149_b.func_176197_a(var1, var2, var3, var4);
   }

   public boolean func_149703_v() {
      return this.field_150149_b.func_149703_v();
   }

   public boolean func_176209_a(IBlockState var1, boolean var2) {
      return this.field_150149_b.func_176209_a(var1, var2);
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return this.field_150149_b.func_176196_c(var1, var2);
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      this.func_176204_a(var1, var2, this.field_150151_M, Blocks.field_150350_a);
      this.field_150149_b.func_176213_c(var1, var2, this.field_150151_M);
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      this.field_150149_b.func_180663_b(var1, var2, this.field_150151_M);
   }

   public void func_176199_a(World var1, BlockPos var2, Entity var3) {
      this.field_150149_b.func_176199_a(var1, var2, var3);
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      this.field_150149_b.func_180650_b(var1, var2, var3, var4);
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      return this.field_150149_b.func_180639_a(var1, var2, this.field_150151_M, var4, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
   }

   public void func_180652_a(World var1, BlockPos var2, Explosion var3) {
      this.field_150149_b.func_180652_a(var1, var2, var3);
   }

   public MapColor func_180659_g(IBlockState var1) {
      return this.field_150149_b.func_180659_g(this.field_150151_M);
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      IBlockState var9 = super.func_180642_a(var1, var2, var3, var4, var5, var6, var7, var8);
      var9 = var9.func_177226_a(field_176309_a, var8.func_174811_aO()).func_177226_a(field_176310_M, BlockStairs.EnumShape.STRAIGHT);
      return var3 != EnumFacing.DOWN && (var3 == EnumFacing.UP || (double)var5 <= 0.5D) ? var9.func_177226_a(field_176308_b, BlockStairs.EnumHalf.BOTTOM) : var9.func_177226_a(field_176308_b, BlockStairs.EnumHalf.TOP);
   }

   public MovingObjectPosition func_180636_a(World var1, BlockPos var2, Vec3 var3, Vec3 var4) {
      MovingObjectPosition[] var5 = new MovingObjectPosition[8];
      IBlockState var6 = var1.func_180495_p(var2);
      int var7 = ((EnumFacing)var6.func_177229_b(field_176309_a)).func_176736_b();
      boolean var8 = var6.func_177229_b(field_176308_b) == BlockStairs.EnumHalf.TOP;
      int[] var9 = field_150150_a[var7 + (var8 ? 4 : 0)];
      this.field_150152_N = true;

      for(int var10 = 0; var10 < 8; ++var10) {
         this.field_150153_O = var10;
         if (Arrays.binarySearch(var9, var10) < 0) {
            var5[var10] = super.func_180636_a(var1, var2, var3, var4);
         }
      }

      int[] var19 = var9;
      int var11 = var9.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         int var13 = var19[var12];
         var5[var13] = null;
      }

      MovingObjectPosition var20 = null;
      double var21 = 0.0D;
      MovingObjectPosition[] var22 = var5;
      int var14 = var5.length;

      for(int var15 = 0; var15 < var14; ++var15) {
         MovingObjectPosition var16 = var22[var15];
         if (var16 != null) {
            double var17 = var16.field_72307_f.func_72436_e(var4);
            if (var17 > var21) {
               var20 = var16;
               var21 = var17;
            }
         }
      }

      return var20;
   }

   public IBlockState func_176203_a(int var1) {
      IBlockState var2 = this.func_176223_P().func_177226_a(field_176308_b, (var1 & 4) > 0 ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM);
      var2 = var2.func_177226_a(field_176309_a, EnumFacing.func_82600_a(5 - (var1 & 3)));
      return var2;
   }

   public int func_176201_c(IBlockState var1) {
      int var2 = 0;
      if (var1.func_177229_b(field_176308_b) == BlockStairs.EnumHalf.TOP) {
         var2 |= 4;
      }

      var2 |= 5 - ((EnumFacing)var1.func_177229_b(field_176309_a)).func_176745_a();
      return var2;
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      if (this.func_176306_h(var2, var3)) {
         switch(this.func_176305_g(var2, var3)) {
         case 0:
            var1 = var1.func_177226_a(field_176310_M, BlockStairs.EnumShape.STRAIGHT);
            break;
         case 1:
            var1 = var1.func_177226_a(field_176310_M, BlockStairs.EnumShape.INNER_RIGHT);
            break;
         case 2:
            var1 = var1.func_177226_a(field_176310_M, BlockStairs.EnumShape.INNER_LEFT);
         }
      } else {
         switch(this.func_176307_f(var2, var3)) {
         case 0:
            var1 = var1.func_177226_a(field_176310_M, BlockStairs.EnumShape.STRAIGHT);
            break;
         case 1:
            var1 = var1.func_177226_a(field_176310_M, BlockStairs.EnumShape.OUTER_RIGHT);
            break;
         case 2:
            var1 = var1.func_177226_a(field_176310_M, BlockStairs.EnumShape.OUTER_LEFT);
         }
      }

      return var1;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176309_a, field_176308_b, field_176310_M});
   }

   static {
      field_176309_a = PropertyDirection.func_177712_a("facing", EnumFacing.Plane.HORIZONTAL);
      field_176308_b = PropertyEnum.func_177709_a("half", BlockStairs.EnumHalf.class);
      field_176310_M = PropertyEnum.func_177709_a("shape", BlockStairs.EnumShape.class);
      field_150150_a = new int[][]{{4, 5}, {5, 7}, {6, 7}, {4, 6}, {0, 1}, {1, 3}, {2, 3}, {0, 2}};
   }

   public static enum EnumShape implements IStringSerializable {
      STRAIGHT("straight"),
      INNER_LEFT("inner_left"),
      INNER_RIGHT("inner_right"),
      OUTER_LEFT("outer_left"),
      OUTER_RIGHT("outer_right");

      private final String field_176699_f;

      private EnumShape(String var3) {
         this.field_176699_f = var3;
      }

      public String toString() {
         return this.field_176699_f;
      }

      public String func_176610_l() {
         return this.field_176699_f;
      }
   }

   public static enum EnumHalf implements IStringSerializable {
      TOP("top"),
      BOTTOM("bottom");

      private final String field_176709_c;

      private EnumHalf(String var3) {
         this.field_176709_c = var3;
      }

      public String toString() {
         return this.field_176709_c;
      }

      public String func_176610_l() {
         return this.field_176709_c;
      }
   }
}
