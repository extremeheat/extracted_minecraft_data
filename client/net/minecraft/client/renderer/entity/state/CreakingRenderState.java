package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.AnimationState;

public class CreakingRenderState extends LivingEntityRenderState {
   public AnimationState invulnerabilityAnimationState = new AnimationState();
   public AnimationState attackAnimationState = new AnimationState();
   public boolean isActive;
   public boolean canMove;

   public CreakingRenderState() {
      super();
   }
}
