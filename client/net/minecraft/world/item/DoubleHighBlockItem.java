package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DoubleHighBlockItem extends BlockItem {
   public DoubleHighBlockItem(Block var1, Item.Properties var2) {
      super(var1, var2);
   }

   protected boolean placeBlock(BlockPlaceContext var1, BlockState var2) {
      Level var3 = var1.getLevel();
      BlockPos var4 = var1.getClickedPos().above();
      BlockState var5 = var3.isWaterAt(var4) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
      var3.setBlock(var4, var5, 27);
      return super.placeBlock(var1, var2);
   }
}
