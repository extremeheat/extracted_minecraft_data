package net.minecraft.block;

import java.util.Random;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockGravel extends BlockFalling {
   public BlockGravel() {
      super();
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      if (var3 > 3) {
         var3 = 3;
      }

      return var2.nextInt(10 - var3 * 3) == 0 ? Items.field_151145_ak : Item.func_150898_a(this);
   }
}
