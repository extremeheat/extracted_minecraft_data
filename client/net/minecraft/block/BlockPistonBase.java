package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonBase extends Block {
   private final boolean field_150082_a;
   private IIcon field_150081_b;
   private IIcon field_150083_M;
   private IIcon field_150084_N;

   public BlockPistonBase(boolean var1) {
      super(Material.field_76233_E);
      this.field_150082_a = var1;
      this.func_149672_a(field_149780_i);
      this.func_149711_c(0.5F);
      this.func_149647_a(CreativeTabs.field_78028_d);
   }

   public IIcon func_150073_e() {
      return this.field_150084_N;
   }

   public void func_150070_b(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.func_149676_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      int var3 = func_150076_b(var2);
      if (var3 > 5) {
         return this.field_150084_N;
      } else if (var1 == var3) {
         return !func_150075_c(var2)
               && !(this.field_149759_B > 0.0)
               && !(this.field_149760_C > 0.0)
               && !(this.field_149754_D > 0.0)
               && !(this.field_149755_E < 1.0)
               && !(this.field_149756_F < 1.0)
               && !(this.field_149757_G < 1.0)
            ? this.field_150084_N
            : this.field_150081_b;
      } else {
         return var1 == Facing.field_71588_a[var3] ? this.field_150083_M : this.field_149761_L;
      }
   }

   public static IIcon func_150074_e(String var0) {
      if (var0 == "piston_side") {
         return Blocks.field_150331_J.field_149761_L;
      } else if (var0 == "piston_top_normal") {
         return Blocks.field_150331_J.field_150084_N;
      } else if (var0 == "piston_top_sticky") {
         return Blocks.field_150320_F.field_150084_N;
      } else {
         return var0 == "piston_inner" ? Blocks.field_150331_J.field_150081_b : null;
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a("piston_side");
      this.field_150084_N = var1.func_94245_a(this.field_150082_a ? "piston_top_sticky" : "piston_top_normal");
      this.field_150081_b = var1.func_94245_a("piston_inner");
      this.field_150083_M = var1.func_94245_a("piston_bottom");
   }

   @Override
   public int func_149645_b() {
      return 16;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      return false;
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      int var7 = func_150071_a(var1, var2, var3, var4, var5);
      var1.func_72921_c(var2, var3, var4, var7, 2);
      if (!var1.field_72995_K) {
         this.func_150078_e(var1, var2, var3, var4);
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!var1.field_72995_K) {
         this.func_150078_e(var1, var2, var3, var4);
      }
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      if (!var1.field_72995_K && var1.func_147438_o(var2, var3, var4) == null) {
         this.func_150078_e(var1, var2, var3, var4);
      }
   }

   private void func_150078_e(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      int var6 = func_150076_b(var5);
      if (var6 != 7) {
         boolean var7 = this.func_150072_a(var1, var2, var3, var4, var6);
         if (var7 && !func_150075_c(var5)) {
            if (func_150077_h(var1, var2, var3, var4, var6)) {
               var1.func_147452_c(var2, var3, var4, this, 0, var6);
            }
         } else if (!var7 && func_150075_c(var5)) {
            var1.func_72921_c(var2, var3, var4, var6, 2);
            var1.func_147452_c(var2, var3, var4, this, 1, var6);
         }
      }
   }

   private boolean func_150072_a(World var1, int var2, int var3, int var4, int var5) {
      if (var5 != 0 && var1.func_94574_k(var2, var3 - 1, var4, 0)) {
         return true;
      } else if (var5 != 1 && var1.func_94574_k(var2, var3 + 1, var4, 1)) {
         return true;
      } else if (var5 != 2 && var1.func_94574_k(var2, var3, var4 - 1, 2)) {
         return true;
      } else if (var5 != 3 && var1.func_94574_k(var2, var3, var4 + 1, 3)) {
         return true;
      } else if (var5 != 5 && var1.func_94574_k(var2 + 1, var3, var4, 5)) {
         return true;
      } else if (var5 != 4 && var1.func_94574_k(var2 - 1, var3, var4, 4)) {
         return true;
      } else if (var1.func_94574_k(var2, var3, var4, 0)) {
         return true;
      } else if (var1.func_94574_k(var2, var3 + 2, var4, 1)) {
         return true;
      } else if (var1.func_94574_k(var2, var3 + 1, var4 - 1, 2)) {
         return true;
      } else if (var1.func_94574_k(var2, var3 + 1, var4 + 1, 3)) {
         return true;
      } else if (var1.func_94574_k(var2 - 1, var3 + 1, var4, 4)) {
         return true;
      } else {
         return var1.func_94574_k(var2 + 1, var3 + 1, var4, 5);
      }
   }

   @Override
   public boolean func_149696_a(World var1, int var2, int var3, int var4, int var5, int var6) {
      if (!var1.field_72995_K) {
         boolean var7 = this.func_150072_a(var1, var2, var3, var4, var6);
         if (var7 && var5 == 1) {
            var1.func_72921_c(var2, var3, var4, var6 | 8, 2);
            return false;
         }

         if (!var7 && var5 == 0) {
            return false;
         }
      }

      if (var5 == 0) {
         if (!this.func_150079_i(var1, var2, var3, var4, var6)) {
            return false;
         }

         var1.func_72921_c(var2, var3, var4, var6 | 8, 2);
         var1.func_72908_a((double)var2 + 0.5, (double)var3 + 0.5, (double)var4 + 0.5, "tile.piston.out", 0.5F, var1.field_73012_v.nextFloat() * 0.25F + 0.6F);
      } else if (var5 == 1) {
         TileEntity var16 = var1.func_147438_o(var2 + Facing.field_71586_b[var6], var3 + Facing.field_71587_c[var6], var4 + Facing.field_71585_d[var6]);
         if (var16 instanceof TileEntityPiston) {
            ((TileEntityPiston)var16).func_145866_f();
         }

         var1.func_147465_d(var2, var3, var4, Blocks.field_150326_M, var6, 3);
         var1.func_147455_a(var2, var3, var4, BlockPistonMoving.func_149962_a(this, var6, var6, false, true));
         if (this.field_150082_a) {
            int var8 = var2 + Facing.field_71586_b[var6] * 2;
            int var9 = var3 + Facing.field_71587_c[var6] * 2;
            int var10 = var4 + Facing.field_71585_d[var6] * 2;
            Block var11 = var1.func_147439_a(var8, var9, var10);
            int var12 = var1.func_72805_g(var8, var9, var10);
            boolean var13 = false;
            if (var11 == Blocks.field_150326_M) {
               TileEntity var14 = var1.func_147438_o(var8, var9, var10);
               if (var14 instanceof TileEntityPiston) {
                  TileEntityPiston var15 = (TileEntityPiston)var14;
                  if (var15.func_145864_c() == var6 && var15.func_145868_b()) {
                     var15.func_145866_f();
                     var11 = var15.func_145861_a();
                     var12 = var15.func_145832_p();
                     var13 = true;
                  }
               }
            }

            if (var13
               || var11.func_149688_o() == Material.field_151579_a
               || !func_150080_a(var11, var1, var8, var9, var10, false)
               || var11.func_149656_h() != 0 && var11 != Blocks.field_150331_J && var11 != Blocks.field_150320_F) {
               if (!var13) {
                  var1.func_147468_f(var2 + Facing.field_71586_b[var6], var3 + Facing.field_71587_c[var6], var4 + Facing.field_71585_d[var6]);
               }
            } else {
               var2 += Facing.field_71586_b[var6];
               var3 += Facing.field_71587_c[var6];
               var4 += Facing.field_71585_d[var6];
               var1.func_147465_d(var2, var3, var4, Blocks.field_150326_M, var12, 3);
               var1.func_147455_a(var2, var3, var4, BlockPistonMoving.func_149962_a(var11, var12, var6, false, false));
               var1.func_147468_f(var8, var9, var10);
            }
         } else {
            var1.func_147468_f(var2 + Facing.field_71586_b[var6], var3 + Facing.field_71587_c[var6], var4 + Facing.field_71585_d[var6]);
         }

         var1.func_72908_a((double)var2 + 0.5, (double)var3 + 0.5, (double)var4 + 0.5, "tile.piston.in", 0.5F, var1.field_73012_v.nextFloat() * 0.15F + 0.6F);
      }

      return true;
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      if (func_150075_c(var5)) {
         float var6 = 0.25F;
         switch(func_150076_b(var5)) {
            case 0:
               this.func_149676_a(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
               break;
            case 1:
               this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
               break;
            case 2:
               this.func_149676_a(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
               break;
            case 3:
               this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
               break;
            case 4:
               this.func_149676_a(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
               break;
            case 5:
               this.func_149676_a(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
         }
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public void func_149683_g() {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void func_149743_a(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      this.func_149719_a(var1, var2, var3, var4);
      return super.func_149668_a(var1, var2, var3, var4);
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   public static int func_150076_b(int var0) {
      return var0 & 7;
   }

   public static boolean func_150075_c(int var0) {
      return (var0 & 8) != 0;
   }

   public static int func_150071_a(World var0, int var1, int var2, int var3, EntityLivingBase var4) {
      if (MathHelper.func_76135_e((float)var4.field_70165_t - (float)var1) < 2.0F && MathHelper.func_76135_e((float)var4.field_70161_v - (float)var3) < 2.0F) {
         double var5 = var4.field_70163_u + 1.82 - (double)var4.field_70129_M;
         if (var5 - (double)var2 > 2.0) {
            return 1;
         }

         if ((double)var2 - var5 > 0.0) {
            return 0;
         }
      }

      int var7 = MathHelper.func_76128_c((double)(var4.field_70177_z * 4.0F / 360.0F) + 0.5) & 3;
      if (var7 == 0) {
         return 2;
      } else if (var7 == 1) {
         return 5;
      } else if (var7 == 2) {
         return 3;
      } else {
         return var7 == 3 ? 4 : 0;
      }
   }

   private static boolean func_150080_a(Block var0, World var1, int var2, int var3, int var4, boolean var5) {
      if (var0 == Blocks.field_150343_Z) {
         return false;
      } else {
         if (var0 != Blocks.field_150331_J && var0 != Blocks.field_150320_F) {
            if (var0.func_149712_f(var1, var2, var3, var4) == -1.0F) {
               return false;
            }

            if (var0.func_149656_h() == 2) {
               return false;
            }

            if (var0.func_149656_h() == 1) {
               if (!var5) {
                  return false;
               }

               return true;
            }
         } else if (func_150075_c(var1.func_72805_g(var2, var3, var4))) {
            return false;
         }

         return !(var0 instanceof ITileEntityProvider);
      }
   }

   private static boolean func_150077_h(World var0, int var1, int var2, int var3, int var4) {
      int var5 = var1 + Facing.field_71586_b[var4];
      int var6 = var2 + Facing.field_71587_c[var4];
      int var7 = var3 + Facing.field_71585_d[var4];

      for(int var8 = 0; var8 < 13; ++var8) {
         if (var6 <= 0 || var6 >= 255) {
            return false;
         }

         Block var9 = var0.func_147439_a(var5, var6, var7);
         if (var9.func_149688_o() != Material.field_151579_a) {
            if (!func_150080_a(var9, var0, var5, var6, var7, true)) {
               return false;
            }

            if (var9.func_149656_h() != 1) {
               if (var8 == 12) {
                  return false;
               }

               var5 += Facing.field_71586_b[var4];
               var6 += Facing.field_71587_c[var4];
               var7 += Facing.field_71585_d[var4];
               continue;
            }
         }
         break;
      }

      return true;
   }

   private boolean func_150079_i(World var1, int var2, int var3, int var4, int var5) {
      int var6 = var2 + Facing.field_71586_b[var5];
      int var7 = var3 + Facing.field_71587_c[var5];
      int var8 = var4 + Facing.field_71585_d[var5];
      int var9 = 0;

      while(var9 < 13) {
         if (var7 <= 0 || var7 >= 255) {
            return false;
         }

         Block var10 = var1.func_147439_a(var6, var7, var8);
         if (var10.func_149688_o() == Material.field_151579_a) {
            break;
         }

         if (!func_150080_a(var10, var1, var6, var7, var8, true)) {
            return false;
         }

         if (var10.func_149656_h() != 1) {
            if (var9 == 12) {
               return false;
            }

            var6 += Facing.field_71586_b[var5];
            var7 += Facing.field_71587_c[var5];
            var8 += Facing.field_71585_d[var5];
            ++var9;
         } else {
            var10.func_149697_b(var1, var6, var7, var8, var1.func_72805_g(var6, var7, var8), 0);
            var1.func_147468_f(var6, var7, var8);
            break;
         }
      }

      var9 = var6;
      int var23 = var7;
      int var11 = var8;
      int var12 = 0;

      Block[] var13;
      int var16;
      for(var13 = new Block[13]; var6 != var2 || var7 != var3 || var8 != var4; var8 = var16) {
         int var14 = var6 - Facing.field_71586_b[var5];
         int var15 = var7 - Facing.field_71587_c[var5];
         var16 = var8 - Facing.field_71585_d[var5];
         Block var17 = var1.func_147439_a(var14, var15, var16);
         int var18 = var1.func_72805_g(var14, var15, var16);
         if (var17 == this && var14 == var2 && var15 == var3 && var16 == var4) {
            var1.func_147465_d(var6, var7, var8, Blocks.field_150326_M, var5 | (this.field_150082_a ? 8 : 0), 4);
            var1.func_147455_a(
               var6, var7, var8, BlockPistonMoving.func_149962_a(Blocks.field_150332_K, var5 | (this.field_150082_a ? 8 : 0), var5, true, false)
            );
         } else {
            var1.func_147465_d(var6, var7, var8, Blocks.field_150326_M, var18, 4);
            var1.func_147455_a(var6, var7, var8, BlockPistonMoving.func_149962_a(var17, var18, var5, true, false));
         }

         var13[var12++] = var17;
         var6 = var14;
         var7 = var15;
      }

      var6 = var9;
      var7 = var23;
      var8 = var11;

      for(int var24 = 0; var6 != var2 || var7 != var3 || var8 != var4; var8 = var16) {
         int var25 = var6 - Facing.field_71586_b[var5];
         int var26 = var7 - Facing.field_71587_c[var5];
         var16 = var8 - Facing.field_71585_d[var5];
         var1.func_147459_d(var25, var26, var16, var13[var24++]);
         var6 = var25;
         var7 = var26;
      }

      return true;
   }
}
