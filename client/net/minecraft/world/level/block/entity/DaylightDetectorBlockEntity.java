package net.minecraft.world.level.block.entity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DaylightDetectorBlockEntity extends BlockEntity implements TickableBlockEntity {
   public DaylightDetectorBlockEntity() {
      super(BlockEntityType.DAYLIGHT_DETECTOR);
   }

   public void tick() {
      if (this.level != null && !this.level.isClientSide && this.level.getGameTime() % 20L == 0L) {
         BlockState var1 = this.getBlockState();
         Block var2 = var1.getBlock();
         if (var2 instanceof DaylightDetectorBlock) {
            DaylightDetectorBlock.updateSignalStrength(var1, this.level, this.worldPosition);
         }
      }

   }
}
