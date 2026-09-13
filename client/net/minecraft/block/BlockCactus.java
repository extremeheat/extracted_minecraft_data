package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockCactus extends Block {
   private IIcon field_150041_a;
   private IIcon field_150040_b;

   protected BlockCactus() {
      super(Material.field_151570_A);
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (var1.func_147437_c(var2, var3 + 1, var4)) {
         int var6 = 1;

         while(var1.func_147439_a(var2, var3 - var6, var4) == this) {
            ++var6;
         }

         if (var6 < 3) {
            int var7 = var1.func_72805_g(var2, var3, var4);
            if (var7 == 15) {
               var1.func_147449_b(var2, var3 + 1, var4, this);
               var1.func_72921_c(var2, var3, var4, 0, 4);
               this.func_149695_a(var1, var2, var3 + 1, var4, this);
            } else {
               var1.func_72921_c(var2, var3, var4, var7 + 1, 4);
            }
         }
      }
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      float var5 = 0.0625F;
      return AxisAlignedBB.func_72330_a(
         (double)((float)var2 + var5),
         (double)var3,
         (double)((float)var4 + var5),
         (double)((float)(var2 + 1) - var5),
         (double)((float)(var3 + 1) - var5),
         (double)((float)(var4 + 1) - var5)
      );
   }

   @Override
   public AxisAlignedBB func_149633_g(World var1, int var2, int var3, int var4) {
      float var5 = 0.0625F;
      return AxisAlignedBB.func_72330_a(
         (double)((float)var2 + var5),
         (double)var3,
         (double)((float)var4 + var5),
         (double)((float)(var2 + 1) - var5),
         (double)(var3 + 1),
         (double)((float)(var4 + 1) - var5)
      );
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var1 == 1) {
         return this.field_150041_a;
      } else {
         return var1 == 0 ? this.field_150040_b : this.field_149761_L;
      }
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public int func_149645_b() {
      return 13;
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return !super.func_149742_c(var1, var2, var3, var4) ? false : this.func_149718_j(var1, var2, var3, var4);
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!this.func_149718_j(var1, var2, var3, var4)) {
         var1.func_147480_a(var2, var3, var4, true);
      }
   }

   @Override
   public boolean func_149718_j(World var1, int var2, int var3, int var4) {
      if (var1.func_147439_a(var2 - 1, var3, var4).func_149688_o().func_76220_a()) {
         return false;
      } else if (var1.func_147439_a(var2 + 1, var3, var4).func_149688_o().func_76220_a()) {
         return false;
      } else if (var1.func_147439_a(var2, var3, var4 - 1).func_149688_o().func_76220_a()) {
         return false;
      } else if (var1.func_147439_a(var2, var3, var4 + 1).func_149688_o().func_76220_a()) {
         return false;
      } else {
         Block var5 = var1.func_147439_a(var2, var3 - 1, var4);
         return var5 == Blocks.field_150434_aF || var5 == Blocks.field_150354_m;
      }
   }

   @Override
   public void func_149670_a(World var1, int var2, int var3, int var4, Entity var5) {
      var5.func_70097_a(DamageSource.field_76367_g, 1.0F);
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.func_149641_N() + "_side");
      this.field_150041_a = var1.func_94245_a(this.func_149641_N() + "_top");
      this.field_150040_b = var1.func_94245_a(this.func_149641_N() + "_bottom");
   }
}
