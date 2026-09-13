package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCake extends Block {
   private IIcon field_150038_a;
   private IIcon field_150037_b;
   private IIcon field_150039_M;

   protected BlockCake() {
      super(Material.field_151568_F);
      this.func_149675_a(true);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      float var6 = 0.0625F;
      float var7 = (float)(1 + var5 * 2) / 16.0F;
      float var8 = 0.5F;
      this.func_149676_a(var7, 0.0F, var6, 1.0F - var6, var8, 1.0F - var6);
   }

   @Override
   public void func_149683_g() {
      float var1 = 0.0625F;
      float var2 = 0.5F;
      this.func_149676_a(var1, 0.0F, var1, 1.0F - var1, var2, 1.0F - var1);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      float var6 = 0.0625F;
      float var7 = (float)(1 + var5 * 2) / 16.0F;
      float var8 = 0.5F;
      return AxisAlignedBB.func_72330_a(
         (double)((float)var2 + var7),
         (double)var3,
         (double)((float)var4 + var6),
         (double)((float)(var2 + 1) - var6),
         (double)((float)var3 + var8 - var6),
         (double)((float)(var4 + 1) - var6)
      );
   }

   @Override
   public AxisAlignedBB func_149633_g(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      float var6 = 0.0625F;
      float var7 = (float)(1 + var5 * 2) / 16.0F;
      float var8 = 0.5F;
      return AxisAlignedBB.func_72330_a(
         (double)((float)var2 + var7),
         (double)var3,
         (double)((float)var4 + var6),
         (double)((float)(var2 + 1) - var6),
         (double)((float)var3 + var8),
         (double)((float)(var4 + 1) - var6)
      );
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var1 == 1) {
         return this.field_150038_a;
      } else if (var1 == 0) {
         return this.field_150037_b;
      } else {
         return var2 > 0 && var1 == 4 ? this.field_150039_M : this.field_149761_L;
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.func_149641_N() + "_side");
      this.field_150039_M = var1.func_94245_a(this.func_149641_N() + "_inner");
      this.field_150038_a = var1.func_94245_a(this.func_149641_N() + "_top");
      this.field_150037_b = var1.func_94245_a(this.func_149641_N() + "_bottom");
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
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      this.func_150036_b(var1, var2, var3, var4, var5);
      return true;
   }

   @Override
   public void func_149699_a(World var1, int var2, int var3, int var4, EntityPlayer var5) {
      this.func_150036_b(var1, var2, var3, var4, var5);
   }

   private void func_150036_b(World var1, int var2, int var3, int var4, EntityPlayer var5) {
      if (var5.func_71043_e(false)) {
         var5.func_71024_bL().func_75122_a(2, 0.1F);
         int var6 = var1.func_72805_g(var2, var3, var4) + 1;
         if (var6 >= 6) {
            var1.func_147468_f(var2, var3, var4);
         } else {
            var1.func_72921_c(var2, var3, var4, var6, 2);
         }
      }
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return !super.func_149742_c(var1, var2, var3, var4) ? false : this.func_149718_j(var1, var2, var3, var4);
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!this.func_149718_j(var1, var2, var3, var4)) {
         var1.func_147468_f(var2, var3, var4);
      }
   }

   @Override
   public boolean func_149718_j(World var1, int var2, int var3, int var4) {
      return var1.func_147439_a(var2, var3 - 1, var4).func_149688_o().func_76220_a();
   }

   @Override
   public int func_149745_a(Random var1) {
      return 0;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return null;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Items.field_151105_aU;
   }
}
