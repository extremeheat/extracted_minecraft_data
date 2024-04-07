package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DaylightDetectorBlockEntity extends BlockEntity {
   public DaylightDetectorBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.DAYLIGHT_DETECTOR, var1, var2);
   }
}
