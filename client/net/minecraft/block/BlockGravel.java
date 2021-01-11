package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockGravel extends BlockFalling {
   public BlockGravel() {
      super();
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      if (var3 > 3) {
         var3 = 3;
      }

      return var2.nextInt(10 - var3 * 3) == 0 ? Items.field_151145_ak : Item.func_150898_a(this);
   }

   public MapColor func_180659_g(IBlockState var1) {
      return MapColor.field_151665_m;
   }
}
