package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockWeb extends Block {
   public BlockWeb() {
      super(Material.field_151569_G);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   public void func_180634_a(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      var4.func_70110_aj();
   }

   public boolean func_149662_c() {
      return false;
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   public boolean func_149686_d() {
      return false;
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151007_F;
   }

   protected boolean func_149700_E() {
      return true;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }
}
