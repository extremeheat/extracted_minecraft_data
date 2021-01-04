package net.minecraft.world.level.block;

import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ShearableDoublePlantBlock extends DoublePlantBlock {
   public static final EnumProperty<DoubleBlockHalf> HALF;

   public ShearableDoublePlantBlock(Block.Properties var1) {
      super(var1);
   }

   public boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      boolean var3 = super.canBeReplaced(var1, var2);
      return var3 && var2.getItemInHand().getItem() == this.asItem() ? false : var3;
   }

   static {
      HALF = DoublePlantBlock.HALF;
   }
}
