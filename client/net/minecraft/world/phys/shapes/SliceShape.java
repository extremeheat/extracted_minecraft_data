package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;

public class SliceShape extends VoxelShape {
   private final VoxelShape delegate;
   private final Direction.Axis axis;
   private static final DoubleList SLICE_COORDS = new CubePointRange(1);

   public SliceShape(VoxelShape var1, Direction.Axis var2, int var3) {
      super(makeSlice(var1.shape, var2, var3));
      this.delegate = var1;
      this.axis = var2;
   }

   private static DiscreteVoxelShape makeSlice(DiscreteVoxelShape var0, Direction.Axis var1, int var2) {
      return new SubShape(var0, var1.choose(var2, 0, 0), var1.choose(0, var2, 0), var1.choose(0, 0, var2), var1.choose(var2 + 1, var0.xSize, var0.xSize), var1.choose(var0.ySize, var2 + 1, var0.ySize), var1.choose(var0.zSize, var0.zSize, var2 + 1));
   }

   protected DoubleList getCoords(Direction.Axis var1) {
      return var1 == this.axis ? SLICE_COORDS : this.delegate.getCoords(var1);
   }
}
