package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockSoulSand extends Block {
   public BlockSoulSand() {
      super(Material.field_151595_p, MapColor.field_151650_B);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      float var4 = 0.125F;
      return new AxisAlignedBB((double)var2.func_177958_n(), (double)var2.func_177956_o(), (double)var2.func_177952_p(), (double)(var2.func_177958_n() + 1), (double)((float)(var2.func_177956_o() + 1) - var4), (double)(var2.func_177952_p() + 1));
   }

   public void func_180634_a(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      var4.field_70159_w *= 0.4D;
      var4.field_70179_y *= 0.4D;
   }
}
