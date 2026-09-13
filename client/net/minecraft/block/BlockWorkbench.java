package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockWorkbench extends Block {
   private IIcon field_150035_a;
   private IIcon field_150034_b;

   protected BlockWorkbench() {
      super(Material.field_151575_d);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var1 == 1) {
         return this.field_150035_a;
      } else if (var1 == 0) {
         return Blocks.field_150344_f.func_149733_h(var1);
      } else {
         return var1 != 2 && var1 != 4 ? this.field_149761_L : this.field_150034_b;
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.func_149641_N() + "_side");
      this.field_150035_a = var1.func_94245_a(this.func_149641_N() + "_top");
      this.field_150034_b = var1.func_94245_a(this.func_149641_N() + "_front");
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.field_72995_K) {
         return true;
      } else {
         var5.func_71058_b(var2, var3, var4);
         return true;
      }
   }
}
