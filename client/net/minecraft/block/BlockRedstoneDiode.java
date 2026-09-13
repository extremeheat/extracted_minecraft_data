package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRedstoneDiode extends BlockDirectional {
   protected final boolean field_149914_a;

   protected BlockRedstoneDiode(boolean var1) {
      super(Material.field_151594_q);
      this.field_149914_a = var1;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return !World.func_147466_a(var1, var2, var3 - 1, var4) ? false : super.func_149742_c(var1, var2, var3, var4);
   }

   @Override
   public boolean func_149718_j(World var1, int var2, int var3, int var4) {
      return !World.func_147466_a(var1, var2, var3 - 1, var4) ? false : super.func_149718_j(var1, var2, var3, var4);
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      if (!this.func_149910_g(var1, var2, var3, var4, var6)) {
         boolean var7 = this.func_149900_a(var1, var2, var3, var4, var6);
         if (this.field_149914_a && !var7) {
            var1.func_147465_d(var2, var3, var4, this.func_149898_i(), var6, 2);
         } else if (!this.field_149914_a) {
            var1.func_147465_d(var2, var3, var4, this.func_149906_e(), var6, 2);
            if (!var7) {
               var1.func_147454_a(var2, var3, var4, this.func_149906_e(), this.func_149899_k(var6), -1);
            }
         }
      }
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var1 == 0) {
         return this.field_149914_a ? Blocks.field_150429_aA.func_149733_h(var1) : Blocks.field_150437_az.func_149733_h(var1);
      } else {
         return var1 == 1 ? this.field_149761_L : Blocks.field_150334_T.func_149733_h(1);
      }
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return var5 != 0 && var5 != 1;
   }

   @Override
   public int func_149645_b() {
      return 36;
   }

   protected boolean func_149905_c(int var1) {
      return this.field_149914_a;
   }

   @Override
   public int func_149748_c(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return this.func_149709_b(var1, var2, var3, var4, var5);
   }

   @Override
   public int func_149709_b(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      if (!this.func_149905_c(var6)) {
         return 0;
      } else {
         int var7 = func_149895_l(var6);
         if (var7 == 0 && var5 == 3) {
            return this.func_149904_f(var1, var2, var3, var4, var6);
         } else if (var7 == 1 && var5 == 4) {
            return this.func_149904_f(var1, var2, var3, var4, var6);
         } else if (var7 == 2 && var5 == 2) {
            return this.func_149904_f(var1, var2, var3, var4, var6);
         } else {
            return var7 == 3 && var5 == 5 ? this.func_149904_f(var1, var2, var3, var4, var6) : 0;
         }
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!this.func_149718_j(var1, var2, var3, var4)) {
         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147468_f(var2, var3, var4);
         var1.func_147459_d(var2 + 1, var3, var4, this);
         var1.func_147459_d(var2 - 1, var3, var4, this);
         var1.func_147459_d(var2, var3, var4 + 1, this);
         var1.func_147459_d(var2, var3, var4 - 1, this);
         var1.func_147459_d(var2, var3 - 1, var4, this);
         var1.func_147459_d(var2, var3 + 1, var4, this);
      } else {
         this.func_149897_b(var1, var2, var3, var4, var5);
      }
   }

   protected void func_149897_b(World var1, int var2, int var3, int var4, Block var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      if (!this.func_149910_g(var1, var2, var3, var4, var6)) {
         boolean var7 = this.func_149900_a(var1, var2, var3, var4, var6);
         if ((this.field_149914_a && !var7 || !this.field_149914_a && var7) && !var1.func_147477_a(var2, var3, var4, this)) {
            byte var8 = -1;
            if (this.func_149912_i(var1, var2, var3, var4, var6)) {
               var8 = -3;
            } else if (this.field_149914_a) {
               var8 = -2;
            }

            var1.func_147454_a(var2, var3, var4, this, this.func_149901_b(var6), var8);
         }
      }
   }

   public boolean func_149910_g(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return false;
   }

   protected boolean func_149900_a(World var1, int var2, int var3, int var4, int var5) {
      return this.func_149903_h(var1, var2, var3, var4, var5) > 0;
   }

   protected int func_149903_h(World var1, int var2, int var3, int var4, int var5) {
      int var6 = func_149895_l(var5);
      int var7 = var2 + Direction.field_71583_a[var6];
      int var8 = var4 + Direction.field_71581_b[var6];
      int var9 = var1.func_72878_l(var7, var3, var8, Direction.field_71582_c[var6]);
      return var9 >= 15 ? var9 : Math.max(var9, var1.func_147439_a(var7, var3, var8) == Blocks.field_150488_af ? var1.func_72805_g(var7, var3, var8) : 0);
   }

   protected int func_149902_h(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      int var6 = func_149895_l(var5);
      switch(var6) {
         case 0:
         case 2:
            return Math.max(this.func_149913_i(var1, var2 - 1, var3, var4, 4), this.func_149913_i(var1, var2 + 1, var3, var4, 5));
         case 1:
         case 3:
            return Math.max(this.func_149913_i(var1, var2, var3, var4 + 1, 3), this.func_149913_i(var1, var2, var3, var4 - 1, 2));
         default:
            return 0;
      }
   }

   protected int func_149913_i(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      Block var6 = var1.func_147439_a(var2, var3, var4);
      if (this.func_149908_a(var6)) {
         return var6 == Blocks.field_150488_af ? var1.func_72805_g(var2, var3, var4) : var1.func_72879_k(var2, var3, var4, var5);
      } else {
         return 0;
      }
   }

   @Override
   public boolean func_149744_f() {
      return true;
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      int var7 = ((MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 0.5) & 3) + 2) % 4;
      var1.func_72921_c(var2, var3, var4, var7, 3);
      boolean var8 = this.func_149900_a(var1, var2, var3, var4, var7);
      if (var8) {
         var1.func_147464_a(var2, var3, var4, this, 1);
      }
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      this.func_149911_e(var1, var2, var3, var4);
   }

   protected void func_149911_e(World var1, int var2, int var3, int var4) {
      int var5 = func_149895_l(var1.func_72805_g(var2, var3, var4));
      if (var5 == 1) {
         var1.func_147460_e(var2 + 1, var3, var4, this);
         var1.func_147441_b(var2 + 1, var3, var4, this, 4);
      }

      if (var5 == 3) {
         var1.func_147460_e(var2 - 1, var3, var4, this);
         var1.func_147441_b(var2 - 1, var3, var4, this, 5);
      }

      if (var5 == 2) {
         var1.func_147460_e(var2, var3, var4 + 1, this);
         var1.func_147441_b(var2, var3, var4 + 1, this, 2);
      }

      if (var5 == 0) {
         var1.func_147460_e(var2, var3, var4 - 1, this);
         var1.func_147441_b(var2, var3, var4 - 1, this, 3);
      }
   }

   @Override
   public void func_149664_b(World var1, int var2, int var3, int var4, int var5) {
      if (this.field_149914_a) {
         var1.func_147459_d(var2 + 1, var3, var4, this);
         var1.func_147459_d(var2 - 1, var3, var4, this);
         var1.func_147459_d(var2, var3, var4 + 1, this);
         var1.func_147459_d(var2, var3, var4 - 1, this);
         var1.func_147459_d(var2, var3 - 1, var4, this);
         var1.func_147459_d(var2, var3 + 1, var4, this);
      }

      super.func_149664_b(var1, var2, var3, var4, var5);
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   protected boolean func_149908_a(Block var1) {
      return var1.func_149744_f();
   }

   protected int func_149904_f(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return 15;
   }

   public static boolean func_149909_d(Block var0) {
      return Blocks.field_150413_aR.func_149907_e(var0) || Blocks.field_150441_bU.func_149907_e(var0);
   }

   public boolean func_149907_e(Block var1) {
      return var1 == this.func_149906_e() || var1 == this.func_149898_i();
   }

   public boolean func_149912_i(World var1, int var2, int var3, int var4, int var5) {
      int var6 = func_149895_l(var5);
      if (func_149909_d(var1.func_147439_a(var2 - Direction.field_71583_a[var6], var3, var4 - Direction.field_71581_b[var6]))) {
         int var7 = var1.func_72805_g(var2 - Direction.field_71583_a[var6], var3, var4 - Direction.field_71581_b[var6]);
         int var8 = func_149895_l(var7);
         return var8 != var6;
      } else {
         return false;
      }
   }

   protected int func_149899_k(int var1) {
      return this.func_149901_b(var1);
   }

   protected abstract int func_149901_b(int var1);

   protected abstract BlockRedstoneDiode func_149906_e();

   protected abstract BlockRedstoneDiode func_149898_i();

   @Override
   public boolean func_149667_c(Block var1) {
      return this.func_149907_e(var1);
   }
}
