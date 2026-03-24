package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public final class CubeVoxelShape extends VoxelShape {
   protected CubeVoxelShape(final DiscreteVoxelShape shape) {
      super(shape);
   }

   public DoubleList getCoords(final Direction.Axis axis) {
      return new CubePointRange(this.shape.getSize(axis));
   }

   protected int findIndex(final Direction.Axis axis, final double coord) {
      int size = this.shape.getSize(axis);
      return Mth.floor(Mth.clamp(coord * (double)size, -1.0, (double)size));
   }
}
