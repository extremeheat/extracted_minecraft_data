package net.minecraft.client.renderer.entity.state;

import java.util.Optional;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.animal.golem.CopperGolemState;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

public class CopperGolemRenderState extends ArmedEntityRenderState {
   public WeatheringCopper.WeatherState weathering;
   public CopperGolemState copperGolemState;
   public final AnimationState idleAnimationState;
   public final AnimationState interactionGetItem;
   public final AnimationState interactionGetNoItem;
   public final AnimationState interactionDropItem;
   public final AnimationState interactionDropNoItem;
   public Optional<BlockState> blockOnAntenna;

   public CopperGolemRenderState() {
      super();
      this.weathering = WeatheringCopper.WeatherState.UNAFFECTED;
      this.copperGolemState = CopperGolemState.IDLE;
      this.idleAnimationState = new AnimationState();
      this.interactionGetItem = new AnimationState();
      this.interactionGetNoItem = new AnimationState();
      this.interactionDropItem = new AnimationState();
      this.interactionDropNoItem = new AnimationState();
      this.blockOnAntenna = Optional.empty();
   }
}
