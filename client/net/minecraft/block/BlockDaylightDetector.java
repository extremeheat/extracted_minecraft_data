package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDaylightDetector;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDaylightDetector extends BlockContainer {
   private IIcon[] field_149958_a = new IIcon[2];

   public BlockDaylightDetector() {
      super(Material.field_151575_d);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
      this.func_149647_a(CreativeTabs.field_78028_d);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
   }

   @Override
   public int func_149709_b(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return var1.func_72805_g(var2, var3, var4);
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
   }

   public void func_149957_e(World var1, int var2, int var3, int var4) {
      if (!var1.field_73011_w.field_76576_e) {
         int var5 = var1.func_72805_g(var2, var3, var4);
         int var6 = var1.func_72972_b(EnumSkyBlock.Sky, var2, var3, var4) - var1.field_73008_k;
         float var7 = var1.func_72929_e(1.0F);
         if (var7 < 3.1415927F) {
            var7 += (0.0F - var7) * 0.2F;
         } else {
            var7 += (6.2831855F - var7) * 0.2F;
         }

         var6 = Math.round((float)var6 * MathHelper.func_76134_b(var7));
         if (var6 < 0) {
            var6 = 0;
         }

         if (var6 > 15) {
            var6 = 15;
         }

         if (var5 != var6) {
            var1.func_72921_c(var2, var3, var4, var6, 3);
         }
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
   public boolean func_149744_f() {
      return true;
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityDaylightDetector();
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return var1 == 1 ? this.field_149958_a[0] : this.field_149958_a[1];
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149958_a[0] = var1.func_94245_a(this.func_149641_N() + "_top");
      this.field_149958_a[1] = var1.func_94245_a(this.func_149641_N() + "_side");
   }
}
