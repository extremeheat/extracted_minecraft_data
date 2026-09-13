package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public class BlockHay extends BlockRotatedPillar {
   public BlockHay() {
      super(Material.field_151577_b);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   protected IIcon func_150163_b(int var1) {
      return this.field_149761_L;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_150164_N = var1.func_94245_a(this.func_149641_N() + "_top");
      this.field_149761_L = var1.func_94245_a(this.func_149641_N() + "_side");
   }
}
