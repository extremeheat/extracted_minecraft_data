package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;

public class ItemAnvilBlock extends ItemMultiTexture {
   public ItemAnvilBlock(Block var1) {
      super(var1, var1, BlockAnvil.field_149834_a);
   }

   @Override
   public int func_77647_b(int var1) {
      return var1 << 2;
   }
}
