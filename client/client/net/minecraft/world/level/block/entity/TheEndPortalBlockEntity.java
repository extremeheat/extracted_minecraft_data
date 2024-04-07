package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class TheEndPortalBlockEntity extends BlockEntity {
   protected TheEndPortalBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3) {
      super(var1, var2, var3);
   }

   public TheEndPortalBlockEntity(BlockPos var1, BlockState var2) {
      this(BlockEntityType.END_PORTAL, var1, var2);
   }

   public boolean shouldRenderFace(Direction var1) {
      return var1.getAxis() == Direction.Axis.Y;
   }
}
