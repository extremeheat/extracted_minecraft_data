package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockDirectional {
   public BlockFenceGate() {
      super(Material.field_151575_d);
      this.func_149647_a(CreativeTabs.field_78028_d);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return Blocks.field_150344_f.func_149733_h(var1);
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return !var1.func_147439_a(var2, var3 - 1, var4).func_149688_o().func_76220_a() ? false : super.func_149742_c(var1, var2, var3, var4);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      if (func_149896_b(var5)) {
         return null;
      } else {
         return var5 != 2 && var5 != 0
            ? AxisAlignedBB.func_72330_a(
               (double)((float)var2 + 0.375F), (double)var3, (double)var4, (double)((float)var2 + 0.625F), (double)((float)var3 + 1.5F), (double)(var4 + 1)
            )
            : AxisAlignedBB.func_72330_a(
               (double)var2, (double)var3, (double)((float)var4 + 0.375F), (double)(var2 + 1), (double)((float)var3 + 1.5F), (double)((float)var4 + 0.625F)
            );
      }
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = func_149895_l(var1.func_72805_g(var2, var3, var4));
      if (var5 != 2 && var5 != 0) {
         this.func_149676_a(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
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
   public boolean func_149655_b(IBlockAccess var1, int var2, int var3, int var4) {
      return func_149896_b(var1.func_72805_g(var2, var3, var4));
   }

   @Override
   public int func_149645_b() {
      return 21;
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      int var7 = (MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 0.5) & 3) % 4;
      var1.func_72921_c(var2, var3, var4, var7, 2);
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      int var10 = var1.func_72805_g(var2, var3, var4);
      if (func_149896_b(var10)) {
         var1.func_72921_c(var2, var3, var4, var10 & -5, 2);
      } else {
         int var11 = (MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 0.5) & 3) % 4;
         int var12 = func_149895_l(var10);
         if (var12 == (var11 + 2) % 4) {
            var10 = var11;
         }

         var1.func_72921_c(var2, var3, var4, var10 | 4, 2);
      }

      var1.func_72889_a(var5, 1003, var2, var3, var4, 0);
      return true;
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!var1.field_72995_K) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         boolean var7 = var1.func_72864_z(var2, var3, var4);
         if (var7 || var5.func_149744_f()) {
            if (var7 && !func_149896_b(var6)) {
               var1.func_72921_c(var2, var3, var4, var6 | 4, 2);
               var1.func_72889_a(null, 1003, var2, var3, var4, 0);
            } else if (!var7 && func_149896_b(var6)) {
               var1.func_72921_c(var2, var3, var4, var6 & -5, 2);
               var1.func_72889_a(null, 1003, var2, var3, var4, 0);
            }
         }
      }
   }

   public static boolean func_149896_b(int var0) {
      return (var0 & 4) != 0;
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return true;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
   }
}
