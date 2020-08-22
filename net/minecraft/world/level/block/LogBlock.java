package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;

public class LogBlock extends RotatedPillarBlock {
   private final MaterialColor woodMaterialColor;

   public LogBlock(MaterialColor var1, Block.Properties var2) {
      super(var2);
      this.woodMaterialColor = var1;
   }

   public MaterialColor getMapColor(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.getValue(AXIS) == Direction.Axis.Y ? this.woodMaterialColor : this.materialColor;
   }
}
