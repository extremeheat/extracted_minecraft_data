package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class ItemBlockTall extends ItemBlock {
   public ItemBlockTall(Block var1, Item.Properties var2) {
      super(var1, var2);
   }

   protected boolean func_195941_b(BlockItemUseContext var1, IBlockState var2) {
      var1.func_195991_k().func_180501_a(var1.func_195995_a().func_177984_a(), Blocks.field_150350_a.func_176223_P(), 27);
      return super.func_195941_b(var1, var2);
   }
}
