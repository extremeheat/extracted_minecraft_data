package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCarpet extends Block {
   protected BlockCarpet() {
      super(Material.field_151593_r);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78031_c);
      this.func_150089_b(0);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return Blocks.field_150325_L.func_149691_a(var1, var2);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      byte var5 = 0;
      float var6 = 0.0625F;
      return AxisAlignedBB.func_72330_a(
         (double)var2 + this.field_149759_B,
         (double)var3 + this.field_149760_C,
         (double)var4 + this.field_149754_D,
         (double)var2 + this.field_149755_E,
         (double)((float)var3 + (float)var5 * var6),
         (double)var4 + this.field_149757_G
      );
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
   public void func_149683_g() {
      this.func_150089_b(0);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      this.func_150089_b(var1.func_72805_g(var2, var3, var4));
   }

   protected void func_150089_b(int var1) {
      byte var2 = 0;
      float var3 = (float)(1 * (1 + var2)) / 16.0F;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, var3, 1.0F);
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return super.func_149742_c(var1, var2, var3, var4) && this.func_149718_j(var1, var2, var3, var4);
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      this.func_150090_e(var1, var2, var3, var4);
   }

   private boolean func_150090_e(World var1, int var2, int var3, int var4) {
      if (!this.func_149718_j(var1, var2, var3, var4)) {
         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147468_f(var2, var3, var4);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public boolean func_149718_j(World var1, int var2, int var3, int var4) {
      return !var1.func_147437_c(var2, var3 - 1, var4);
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return var5 == 1 ? true : super.func_149646_a(var1, var2, var3, var4, var5);
   }

   @Override
   public int func_149692_a(int var1) {
      return var1;
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      for(int var4 = 0; var4 < 16; ++var4) {
         var3.add(new ItemStack(var1, 1, var4));
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
   }
}
