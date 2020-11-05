package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public final class CubeVoxelShape extends VoxelShape {
   protected CubeVoxelShape(DiscreteVoxelShape var1) {
      super(var1);
   }

   protected DoubleList getCoords(Direction.Axis var1) {
      return new CubePointRange(this.shape.getSize(var1));
   }

   protected int findIndex(Direction.Axis var1, double var2) {
      int var4 = this.shape.getSize(var1);
      return Mth.floor(Mth.clamp(var2 * (double)var4, -1.0D, (double)var4));
   }
}
