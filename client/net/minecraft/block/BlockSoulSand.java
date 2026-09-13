package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockSoulSand extends Block {
   public BlockSoulSand() {
      super(Material.field_151595_p);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      float var5 = 0.125F;
      return AxisAlignedBB.func_72330_a((double)var2, (double)var3, (double)var4, (double)(var2 + 1), (double)((float)(var3 + 1) - var5), (double)(var4 + 1));
   }

   @Override
   public void func_149670_a(World var1, int var2, int var3, int var4, Entity var5) {
      var5.field_70159_w *= 0.4;
      var5.field_70179_y *= 0.4;
   }
}
