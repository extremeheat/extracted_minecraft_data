package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class HangingSignBlockEntity extends SignBlockEntity {
   private static final int MAX_TEXT_LINE_WIDTH = 50;
   private static final int TEXT_LINE_HEIGHT = 9;

   public HangingSignBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.HANGING_SIGN, var1, var2);
   }

   @Override
   public int getTextLineHeight() {
      return 9;
   }

   @Override
   public int getMaxTextLineWidth() {
      return 50;
   }
}
