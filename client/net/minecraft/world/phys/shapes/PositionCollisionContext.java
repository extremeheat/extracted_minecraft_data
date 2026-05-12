package net.minecraft.world.phys.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class PositionCollisionContext implements CollisionContext {
   private final double y;

   public PositionCollisionContext(final double y) {
      super();
      this.y = y;
   }

   public boolean isDescending() {
      return false;
   }

   public boolean isAbove(final VoxelShape shape, final BlockPos pos, final boolean defaultValue) {
      return this.y > (double)pos.getY() + shape.max(Direction.Axis.Y) - 9.999999747378752E-6;
   }

   public boolean isHoldingItem(final Item item) {
      return false;
   }

   public boolean alwaysCollideWithFluid() {
      return false;
   }

   public boolean canStandOnFluid(final FluidState fluidStateAbove, final FluidState fluid) {
      return false;
   }

   public VoxelShape getCollisionShape(final BlockState state, final CollisionGetter collisionGetter, final BlockPos pos) {
      return state.getCollisionShape(collisionGetter, pos, this);
   }
}
