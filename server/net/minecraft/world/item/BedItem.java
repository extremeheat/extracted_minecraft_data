package net.minecraft.world.item;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BedItem extends BlockItem {
   public BedItem(Block var1, Item.Properties var2) {
      super(var1, var2);
   }

   protected boolean placeBlock(BlockPlaceContext var1, BlockState var2) {
      return var1.getLevel().setBlock(var1.getClickedPos(), var2, 26);
   }
}
