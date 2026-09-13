package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.world.World;

public class BlockBeacon extends BlockContainer {
   public BlockBeacon() {
      super(Material.field_151592_s);
      this.func_149711_c(3.0F);
      this.func_149647_a(CreativeTabs.field_78026_f);
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityBeacon();
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntityBeacon var10 = (TileEntityBeacon)var1.func_147438_o(var2, var3, var4);
         if (var10 != null) {
            var5.func_146104_a(var10);
         }

         return true;
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
      return 34;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      super.func_149651_a(var1);
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      super.func_149689_a(var1, var2, var3, var4, var5, var6);
      if (var6.func_82837_s()) {
         ((TileEntityBeacon)var1.func_147438_o(var2, var3, var4)).func_145999_a(var6.func_82833_r());
      }
   }
}
