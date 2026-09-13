package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLadder extends Block {
   protected BlockLadder() {
      super(Material.field_151594_q);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      this.func_149719_a(var1, var2, var3, var4);
      return super.func_149668_a(var1, var2, var3, var4);
   }

   @Override
   public AxisAlignedBB func_149633_g(World var1, int var2, int var3, int var4) {
      this.func_149719_a(var1, var2, var3, var4);
      return super.func_149633_g(var1, var2, var3, var4);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      this.func_149797_b(var1.func_72805_g(var2, var3, var4));
   }

   public void func_149797_b(int var1) {
      float var3 = 0.125F;
      if (var1 == 2) {
         this.func_149676_a(0.0F, 0.0F, 1.0F - var3, 1.0F, 1.0F, 1.0F);
      }

      if (var1 == 3) {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var3);
      }

      if (var1 == 4) {
         this.func_149676_a(1.0F - var3, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      }

      if (var1 == 5) {
         this.func_149676_a(0.0F, 0.0F, 0.0F, var3, 1.0F, 1.0F);
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
      return 8;
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      if (var1.func_147439_a(var2 - 1, var3, var4).func_149721_r()) {
         return true;
      } else if (var1.func_147439_a(var2 + 1, var3, var4).func_149721_r()) {
         return true;
      } else if (var1.func_147439_a(var2, var3, var4 - 1).func_149721_r()) {
         return true;
      } else {
         return var1.func_147439_a(var2, var3, var4 + 1).func_149721_r();
      }
   }

   @Override
   public int func_149660_a(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
      int var10 = var9;
      if ((var9 == 0 || var5 == 2) && var1.func_147439_a(var2, var3, var4 + 1).func_149721_r()) {
         var10 = 2;
      }

      if ((var10 == 0 || var5 == 3) && var1.func_147439_a(var2, var3, var4 - 1).func_149721_r()) {
         var10 = 3;
      }

      if ((var10 == 0 || var5 == 4) && var1.func_147439_a(var2 + 1, var3, var4).func_149721_r()) {
         var10 = 4;
      }

      if ((var10 == 0 || var5 == 5) && var1.func_147439_a(var2 - 1, var3, var4).func_149721_r()) {
         var10 = 5;
      }

      return var10;
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      boolean var7 = false;
      if (var6 == 2 && var1.func_147439_a(var2, var3, var4 + 1).func_149721_r()) {
         var7 = true;
      }

      if (var6 == 3 && var1.func_147439_a(var2, var3, var4 - 1).func_149721_r()) {
         var7 = true;
      }

      if (var6 == 4 && var1.func_147439_a(var2 + 1, var3, var4).func_149721_r()) {
         var7 = true;
      }

      if (var6 == 5 && var1.func_147439_a(var2 - 1, var3, var4).func_149721_r()) {
         var7 = true;
      }

      if (!var7) {
         this.func_149697_b(var1, var2, var3, var4, var6, 0);
         var1.func_147468_f(var2, var3, var4);
      }

      super.func_149695_a(var1, var2, var3, var4, var5);
   }

   @Override
   public int func_149745_a(Random var1) {
      return 1;
   }
}
