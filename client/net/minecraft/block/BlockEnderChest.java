package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockEnderChest extends BlockContainer {
   protected BlockEnderChest() {
      super(Material.field_151576_e);
      this.func_149647_a(CreativeTabs.field_78031_c);
      this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
      return 22;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150343_Z);
   }

   @Override
   public int func_149745_a(Random var1) {
      return 8;
   }

   @Override
   protected boolean func_149700_E() {
      return true;
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      byte var7 = 0;
      int var8 = MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 0.5) & 3;
      if (var8 == 0) {
         var7 = 2;
      }

      if (var8 == 1) {
         var7 = 5;
      }

      if (var8 == 2) {
         var7 = 3;
      }

      if (var8 == 3) {
         var7 = 4;
      }

      var1.func_72921_c(var2, var3, var4, var7, 2);
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      InventoryEnderChest var10 = var5.func_71005_bN();
      TileEntityEnderChest var11 = (TileEntityEnderChest)var1.func_147438_o(var2, var3, var4);
      if (var10 != null && var11 != null) {
         if (var1.func_147439_a(var2, var3 + 1, var4).func_149721_r()) {
            return true;
         } else if (var1.field_72995_K) {
            return true;
         } else {
            var10.func_146031_a(var11);
            var5.func_71007_a(var10);
            return true;
         }
      } else {
         return true;
      }
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityEnderChest();
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      for(int var6 = 0; var6 < 3; ++var6) {
         double var7 = (double)((float)var2 + var5.nextFloat());
         double var9 = (double)((float)var3 + var5.nextFloat());
         double var11 = (double)((float)var4 + var5.nextFloat());
         double var13 = 0.0;
         double var15 = 0.0;
         double var17 = 0.0;
         int var19 = var5.nextInt(2) * 2 - 1;
         int var20 = var5.nextInt(2) * 2 - 1;
         var13 = ((double)var5.nextFloat() - 0.5) * 0.125;
         var15 = ((double)var5.nextFloat() - 0.5) * 0.125;
         var17 = ((double)var5.nextFloat() - 0.5) * 0.125;
         var11 = (double)var4 + 0.5 + 0.25 * (double)var20;
         var17 = (double)(var5.nextFloat() * 1.0F * (float)var20);
         var7 = (double)var2 + 0.5 + 0.25 * (double)var19;
         var13 = (double)(var5.nextFloat() * 1.0F * (float)var19);
         var1.func_72869_a("portal", var7, var9, var11, var13, var15, var17);
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a("obsidian");
   }
}
