package net.minecraft.world.item;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DoubleHighBlockItem extends BlockItem {
   public DoubleHighBlockItem(Block var1, Item.Properties var2) {
      super(var1, var2);
   }

   protected boolean placeBlock(BlockPlaceContext var1, BlockState var2) {
      var1.getLevel().setBlock(var1.getClickedPos().above(), Blocks.AIR.defaultBlockState(), 27);
      return super.placeBlock(var1, var2);
   }
}
