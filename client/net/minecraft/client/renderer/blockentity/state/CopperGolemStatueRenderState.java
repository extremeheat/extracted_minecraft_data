package net.minecraft.client.renderer.blockentity.state;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.WeatheringCopper;

public class CopperGolemStatueRenderState extends BlockEntityRenderState {
   public CopperGolemStatueBlock.Pose pose;
   public Direction direction;
   public WeatheringCopper.WeatherState oxidationState;

   public CopperGolemStatueRenderState() {
      super();
      this.pose = CopperGolemStatueBlock.Pose.STANDING;
      this.direction = Direction.NORTH;
      this.oxidationState = WeatheringCopper.WeatherState.UNAFFECTED;
   }
}
