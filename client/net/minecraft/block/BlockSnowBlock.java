package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BlockSnowBlock extends Block {
   protected BlockSnowBlock() {
      super(Material.field_151596_z);
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151126_ay;
   }

   public int func_149745_a(Random var1) {
      return 4;
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var1.func_175642_b(EnumSkyBlock.BLOCK, var2) > 11) {
         this.func_176226_b(var1, var2, var1.func_180495_p(var2), 0);
         var1.func_175698_g(var2);
      }

   }
}
