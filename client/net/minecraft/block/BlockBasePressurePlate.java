package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block {
   private String field_150067_a;

   protected BlockBasePressurePlate(String var1, Material var2) {
      super(var2);
      this.field_150067_a = var1;
      this.func_149647_a(CreativeTabs.field_78028_d);
      this.func_149675_a(true);
      this.func_150063_b(this.func_150066_d(15));
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      this.func_150063_b(var1.func_72805_g(var2, var3, var4));
   }

   protected void func_150063_b(int var1) {
      boolean var2 = this.func_150060_c(var1) > 0;
      float var3 = 0.0625F;
      if (var2) {
         this.func_149676_a(var3, 0.0F, var3, 1.0F - var3, 0.03125F, 1.0F - var3);
      } else {
         this.func_149676_a(var3, 0.0F, var3, 1.0F - var3, 0.0625F, 1.0F - var3);
      }
   }

   @Override
   public int func_149738_a(World var1) {
      return 20;
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return null;
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
   public boolean func_149655_b(IBlockAccess var1, int var2, int var3, int var4) {
      return true;
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return World.func_147466_a(var1, var2, var3 - 1, var4) || BlockFence.func_149825_a(var1.func_147439_a(var2, var3 - 1, var4));
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      boolean var6 = false;
      if (!World.func_147466_a(var1, var2, var3 - 1, var4) && !BlockFence.func_149825_a(var1.func_147439_a(var2, var3 - 1, var4))) {
         var6 = true;
      }

      if (var6) {
         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147468_f(var2, var3, var4);
      }
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (!var1.field_72995_K) {
         int var6 = this.func_150060_c(var1.func_72805_g(var2, var3, var4));
         if (var6 > 0) {
            this.func_150062_a(var1, var2, var3, var4, var6);
         }
      }
   }

   @Override
   public void func_149670_a(World var1, int var2, int var3, int var4, Entity var5) {
      if (!var1.field_72995_K) {
         int var6 = this.func_150060_c(var1.func_72805_g(var2, var3, var4));
         if (var6 == 0) {
            this.func_150062_a(var1, var2, var3, var4, var6);
         }
      }
   }

   protected void func_150062_a(World var1, int var2, int var3, int var4, int var5) {
      int var6 = this.func_150065_e(var1, var2, var3, var4);
      boolean var7 = var5 > 0;
      boolean var8 = var6 > 0;
      if (var5 != var6) {
         var1.func_72921_c(var2, var3, var4, this.func_150066_d(var6), 2);
         this.func_150064_a_(var1, var2, var3, var4);
         var1.func_147458_c(var2, var3, var4, var2, var3, var4);
      }

      if (!var8 && var7) {
         var1.func_72908_a((double)var2 + 0.5, (double)var3 + 0.1, (double)var4 + 0.5, "random.click", 0.3F, 0.5F);
      } else if (var8 && !var7) {
         var1.func_72908_a((double)var2 + 0.5, (double)var3 + 0.1, (double)var4 + 0.5, "random.click", 0.3F, 0.6F);
      }

      if (var8) {
         var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1));
      }
   }

   protected AxisAlignedBB func_150061_a(int var1, int var2, int var3) {
      float var4 = 0.125F;
      return AxisAlignedBB.func_72330_a(
         (double)((float)var1 + var4),
         (double)var2,
         (double)((float)var3 + var4),
         (double)((float)(var1 + 1) - var4),
         (double)var2 + 0.25,
         (double)((float)(var3 + 1) - var4)
      );
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      if (this.func_150060_c(var6) > 0) {
         this.func_150064_a_(var1, var2, var3, var4);
      }

      super.func_149749_a(var1, var2, var3, var4, var5, var6);
   }

   protected void func_150064_a_(World var1, int var2, int var3, int var4) {
      var1.func_147459_d(var2, var3, var4, this);
      var1.func_147459_d(var2, var3 - 1, var4, this);
   }

   @Override
   public int func_149709_b(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return this.func_150060_c(var1.func_72805_g(var2, var3, var4));
   }

   @Override
   public int func_149748_c(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return var5 == 1 ? this.func_150060_c(var1.func_72805_g(var2, var3, var4)) : 0;
   }

   @Override
   public boolean func_149744_f() {
      return true;
   }

   @Override
   public void func_149683_g() {
      float var1 = 0.5F;
      float var2 = 0.125F;
      float var3 = 0.5F;
      this.func_149676_a(0.5F - var1, 0.5F - var2, 0.5F - var3, 0.5F + var1, 0.5F + var2, 0.5F + var3);
   }

   @Override
   public int func_149656_h() {
      return 1;
   }

   protected abstract int func_150065_e(World var1, int var2, int var3, int var4);

   protected abstract int func_150060_c(int var1);

   protected abstract int func_150066_d(int var1);

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.field_150067_a);
   }
}
