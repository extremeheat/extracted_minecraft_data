package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStairs extends Block {
   private static final int[][] field_150150_a = new int[][]{{2, 6}, {3, 7}, {2, 3}, {6, 7}, {0, 4}, {1, 5}, {0, 1}, {4, 5}};
   private final Block field_150149_b;
   private final int field_150151_M;
   private boolean field_150152_N;
   private int field_150153_O;

   protected BlockStairs(Block var1, int var2) {
      super(var1.field_149764_J);
      this.field_150149_b = var1;
      this.field_150151_M = var2;
      this.func_149711_c(var1.field_149782_v);
      this.func_149752_b(var1.field_149781_w / 3.0F);
      this.func_149672_a(var1.field_149762_H);
      this.func_149713_g(255);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      if (this.field_150152_N) {
         this.func_149676_a(
            0.5F * (float)(this.field_150153_O % 2),
            0.5F * (float)(this.field_150153_O / 2 % 2),
            0.5F * (float)(this.field_150153_O / 4 % 2),
            0.5F + 0.5F * (float)(this.field_150153_O % 2),
            0.5F + 0.5F * (float)(this.field_150153_O / 2 % 2),
            0.5F + 0.5F * (float)(this.field_150153_O / 4 % 2)
         );
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public int func_149645_b() {
      return 10;
   }

   public void func_150147_e(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      if ((var5 & 4) != 0) {
         this.func_149676_a(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }
   }

   public static boolean func_150148_a(Block var0) {
      return var0 instanceof BlockStairs;
   }

   private boolean func_150146_f(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      Block var6 = var1.func_147439_a(var2, var3, var4);
      return func_150148_a(var6) && var1.func_72805_g(var2, var3, var4) == var5;
   }

   public boolean func_150145_f(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      int var6 = var5 & 3;
      float var7 = 0.5F;
      float var8 = 1.0F;
      if ((var5 & 4) != 0) {
         var7 = 0.0F;
         var8 = 0.5F;
      }

      float var9 = 0.0F;
      float var10 = 1.0F;
      float var11 = 0.0F;
      float var12 = 0.5F;
      boolean var13 = true;
      if (var6 == 0) {
         var9 = 0.5F;
         var12 = 1.0F;
         Block var14 = var1.func_147439_a(var2 + 1, var3, var4);
         int var15 = var1.func_72805_g(var2 + 1, var3, var4);
         if (func_150148_a(var14) && (var5 & 4) == (var15 & 4)) {
            int var16 = var15 & 3;
            if (var16 == 3 && !this.func_150146_f(var1, var2, var3, var4 + 1, var5)) {
               var12 = 0.5F;
               var13 = false;
            } else if (var16 == 2 && !this.func_150146_f(var1, var2, var3, var4 - 1, var5)) {
               var11 = 0.5F;
               var13 = false;
            }
         }
      } else if (var6 == 1) {
         var10 = 0.5F;
         var12 = 1.0F;
         Block var17 = var1.func_147439_a(var2 - 1, var3, var4);
         int var20 = var1.func_72805_g(var2 - 1, var3, var4);
         if (func_150148_a(var17) && (var5 & 4) == (var20 & 4)) {
            int var23 = var20 & 3;
            if (var23 == 3 && !this.func_150146_f(var1, var2, var3, var4 + 1, var5)) {
               var12 = 0.5F;
               var13 = false;
            } else if (var23 == 2 && !this.func_150146_f(var1, var2, var3, var4 - 1, var5)) {
               var11 = 0.5F;
               var13 = false;
            }
         }
      } else if (var6 == 2) {
         var11 = 0.5F;
         var12 = 1.0F;
         Block var18 = var1.func_147439_a(var2, var3, var4 + 1);
         int var21 = var1.func_72805_g(var2, var3, var4 + 1);
         if (func_150148_a(var18) && (var5 & 4) == (var21 & 4)) {
            int var24 = var21 & 3;
            if (var24 == 1 && !this.func_150146_f(var1, var2 + 1, var3, var4, var5)) {
               var10 = 0.5F;
               var13 = false;
            } else if (var24 == 0 && !this.func_150146_f(var1, var2 - 1, var3, var4, var5)) {
               var9 = 0.5F;
               var13 = false;
            }
         }
      } else if (var6 == 3) {
         Block var19 = var1.func_147439_a(var2, var3, var4 - 1);
         int var22 = var1.func_72805_g(var2, var3, var4 - 1);
         if (func_150148_a(var19) && (var5 & 4) == (var22 & 4)) {
            int var25 = var22 & 3;
            if (var25 == 1 && !this.func_150146_f(var1, var2 + 1, var3, var4, var5)) {
               var10 = 0.5F;
               var13 = false;
            } else if (var25 == 0 && !this.func_150146_f(var1, var2 - 1, var3, var4, var5)) {
               var9 = 0.5F;
               var13 = false;
            }
         }
      }

      this.func_149676_a(var9, var7, var11, var10, var8, var12);
      return var13;
   }

   public boolean func_150144_g(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      int var6 = var5 & 3;
      float var7 = 0.5F;
      float var8 = 1.0F;
      if ((var5 & 4) != 0) {
         var7 = 0.0F;
         var8 = 0.5F;
      }

      float var9 = 0.0F;
      float var10 = 0.5F;
      float var11 = 0.5F;
      float var12 = 1.0F;
      boolean var13 = false;
      if (var6 == 0) {
         Block var14 = var1.func_147439_a(var2 - 1, var3, var4);
         int var15 = var1.func_72805_g(var2 - 1, var3, var4);
         if (func_150148_a(var14) && (var5 & 4) == (var15 & 4)) {
            int var16 = var15 & 3;
            if (var16 == 3 && !this.func_150146_f(var1, var2, var3, var4 - 1, var5)) {
               var11 = 0.0F;
               var12 = 0.5F;
               var13 = true;
            } else if (var16 == 2 && !this.func_150146_f(var1, var2, var3, var4 + 1, var5)) {
               var11 = 0.5F;
               var12 = 1.0F;
               var13 = true;
            }
         }
      } else if (var6 == 1) {
         Block var17 = var1.func_147439_a(var2 + 1, var3, var4);
         int var20 = var1.func_72805_g(var2 + 1, var3, var4);
         if (func_150148_a(var17) && (var5 & 4) == (var20 & 4)) {
            var9 = 0.5F;
            var10 = 1.0F;
            int var23 = var20 & 3;
            if (var23 == 3 && !this.func_150146_f(var1, var2, var3, var4 - 1, var5)) {
               var11 = 0.0F;
               var12 = 0.5F;
               var13 = true;
            } else if (var23 == 2 && !this.func_150146_f(var1, var2, var3, var4 + 1, var5)) {
               var11 = 0.5F;
               var12 = 1.0F;
               var13 = true;
            }
         }
      } else if (var6 == 2) {
         Block var18 = var1.func_147439_a(var2, var3, var4 - 1);
         int var21 = var1.func_72805_g(var2, var3, var4 - 1);
         if (func_150148_a(var18) && (var5 & 4) == (var21 & 4)) {
            var11 = 0.0F;
            var12 = 0.5F;
            int var24 = var21 & 3;
            if (var24 == 1 && !this.func_150146_f(var1, var2 - 1, var3, var4, var5)) {
               var13 = true;
            } else if (var24 == 0 && !this.func_150146_f(var1, var2 + 1, var3, var4, var5)) {
               var9 = 0.5F;
               var10 = 1.0F;
               var13 = true;
            }
         }
      } else if (var6 == 3) {
         Block var19 = var1.func_147439_a(var2, var3, var4 + 1);
         int var22 = var1.func_72805_g(var2, var3, var4 + 1);
         if (func_150148_a(var19) && (var5 & 4) == (var22 & 4)) {
            int var25 = var22 & 3;
            if (var25 == 1 && !this.func_150146_f(var1, var2 - 1, var3, var4, var5)) {
               var13 = true;
            } else if (var25 == 0 && !this.func_150146_f(var1, var2 + 1, var3, var4, var5)) {
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

   @Override
   public void func_149743_a(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
      this.func_150147_e(var1, var2, var3, var4);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      boolean var8 = this.func_150145_f(var1, var2, var3, var4);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      if (var8 && this.func_150144_g(var1, var2, var3, var4)) {
         super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      }

      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      this.field_150149_b.func_149734_b(var1, var2, var3, var4, var5);
   }

   @Override
   public void func_149699_a(World var1, int var2, int var3, int var4, EntityPlayer var5) {
      this.field_150149_b.func_149699_a(var1, var2, var3, var4, var5);
   }

   @Override
   public void func_149664_b(World var1, int var2, int var3, int var4, int var5) {
      this.field_150149_b.func_149664_b(var1, var2, var3, var4, var5);
   }

   @Override
   public int func_149677_c(IBlockAccess var1, int var2, int var3, int var4) {
      return this.field_150149_b.func_149677_c(var1, var2, var3, var4);
   }

   @Override
   public float func_149638_a(Entity var1) {
      return this.field_150149_b.func_149638_a(var1);
   }

   @Override
   public int func_149701_w() {
      return this.field_150149_b.func_149701_w();
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return this.field_150149_b.func_149691_a(var1, this.field_150151_M);
   }

   @Override
   public int func_149738_a(World var1) {
      return this.field_150149_b.func_149738_a(var1);
   }

   @Override
   public AxisAlignedBB func_149633_g(World var1, int var2, int var3, int var4) {
      return this.field_150149_b.func_149633_g(var1, var2, var3, var4);
   }

   @Override
   public void func_149640_a(World var1, int var2, int var3, int var4, Entity var5, Vec3 var6) {
      this.field_150149_b.func_149640_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean func_149703_v() {
      return this.field_150149_b.func_149703_v();
   }

   @Override
   public boolean func_149678_a(int var1, boolean var2) {
      return this.field_150149_b.func_149678_a(var1, var2);
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return this.field_150149_b.func_149742_c(var1, var2, var3, var4);
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      this.func_149695_a(var1, var2, var3, var4, Blocks.field_150350_a);
      this.field_150149_b.func_149726_b(var1, var2, var3, var4);
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      this.field_150149_b.func_149749_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void func_149724_b(World var1, int var2, int var3, int var4, Entity var5) {
      this.field_150149_b.func_149724_b(var1, var2, var3, var4, var5);
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      this.field_150149_b.func_149674_a(var1, var2, var3, var4, var5);
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      return this.field_150149_b.func_149727_a(var1, var2, var3, var4, var5, 0, 0.0F, 0.0F, 0.0F);
   }

   @Override
   public void func_149723_a(World var1, int var2, int var3, int var4, Explosion var5) {
      this.field_150149_b.func_149723_a(var1, var2, var3, var4, var5);
   }

   @Override
   public MapColor func_149728_f(int var1) {
      return this.field_150149_b.func_149728_f(this.field_150151_M);
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      int var7 = MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 0.5) & 3;
      int var8 = var1.func_72805_g(var2, var3, var4) & 4;
      if (var7 == 0) {
         var1.func_72921_c(var2, var3, var4, 2 | var8, 2);
      }

      if (var7 == 1) {
         var1.func_72921_c(var2, var3, var4, 1 | var8, 2);
      }

      if (var7 == 2) {
         var1.func_72921_c(var2, var3, var4, 3 | var8, 2);
      }

      if (var7 == 3) {
         var1.func_72921_c(var2, var3, var4, 0 | var8, 2);
      }
   }

   @Override
   public int func_149660_a(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
      return var5 != 0 && (var5 == 1 || !((double)var7 > 0.5)) ? var9 : var9 | 4;
   }

   @Override
   public MovingObjectPosition func_149731_a(World var1, int var2, int var3, int var4, Vec3 var5, Vec3 var6) {
      MovingObjectPosition[] var7 = new MovingObjectPosition[8];
      int var8 = var1.func_72805_g(var2, var3, var4);
      int var9 = var8 & 3;
      boolean var10 = (var8 & 4) == 4;
      int[] var11 = field_150150_a[var9 + (var10 ? 4 : 0)];
      this.field_150152_N = true;

      for(int var12 = 0; var12 < 8; ++var12) {
         this.field_150153_O = var12;

         for(int var16 : var11) {
            if (var16 == var12) {
            }
         }

         var7[var12] = super.func_149731_a(var1, var2, var3, var4, var5, var6);
      }

      for(int var26 : var11) {
         var7[var26] = null;
      }

      MovingObjectPosition var22 = null;
      double var24 = 0.0;

      for(MovingObjectPosition var18 : var7) {
         if (var18 != null) {
            double var19 = var18.field_72307_f.func_72436_e(var6);
            if (var19 > var24) {
               var22 = var18;
               var24 = var19;
            }
         }
      }

      return var22;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
   }
}
