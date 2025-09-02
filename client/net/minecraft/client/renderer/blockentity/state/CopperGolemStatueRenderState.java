package net.minecraft.client.renderer.blockentity.state;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.CopperGolemStatueBlock;

public class CopperGolemStatueRenderState extends BlockEntityRenderState {
   public CopperGolemStatueBlock.Pose pose;
   public Direction direction;

   public CopperGolemStatueRenderState() {
      super();
      this.pose = CopperGolemStatueBlock.Pose.STANDING;
      this.direction = Direction.NORTH;
   }
}
