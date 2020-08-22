package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HayBlock extends RotatedPillarBlock {
   public HayBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AXIS, Direction.Axis.Y));
   }

   public void fallOn(Level var1, BlockPos var2, Entity var3, float var4) {
      var3.causeFallDamage(var4, 0.2F);
   }
}
