package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.animal.axolotl.Axolotl;

public class AxolotlRenderState extends LivingEntityRenderState {
   public Axolotl.Variant variant;
   public float playingDeadFactor;
   public float movingFactor;
   public float inWaterFactor;
   public float onGroundFactor;
   public final AnimationState swimAnimation;
   public final AnimationState walkAnimationState;
   public final AnimationState walkUnderWaterAnimationState;
   public final AnimationState idleUnderWaterAnimationState;
   public final AnimationState idleUnderWaterOnGroundAnimationState;
   public final AnimationState idleOnGroundAnimationState;
   public final AnimationState playDeadAnimationState;

   public AxolotlRenderState() {
      super();
      this.variant = Axolotl.Variant.DEFAULT;
      this.inWaterFactor = 1.0F;
      this.swimAnimation = new AnimationState();
      this.walkAnimationState = new AnimationState();
      this.walkUnderWaterAnimationState = new AnimationState();
      this.idleUnderWaterAnimationState = new AnimationState();
      this.idleUnderWaterOnGroundAnimationState = new AnimationState();
      this.idleOnGroundAnimationState = new AnimationState();
      this.playDeadAnimationState = new AnimationState();
   }
}
