package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class ItemBed extends ItemBlock {
   public ItemBed(Block var1, Item.Properties var2) {
      super(var1, var2);
   }

   protected boolean func_195941_b(BlockItemUseContext var1, IBlockState var2) {
      return var1.func_195991_k().func_180501_a(var1.func_195995_a(), var2, 26);
   }
}
