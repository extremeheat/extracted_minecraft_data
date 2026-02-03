package net.minecraft.client.model.animal.rabbit;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.RabbitRenderState;

public abstract class RabbitModel extends EntityModel<RabbitRenderState> {
   protected static final String FRONT_LEGS = "frontlegs";
   protected static final String BACK_LEGS = "backlegs";
   protected static final String LEFT_HAUNCH = "left_haunch";
   protected static final String RIGHT_HAUNCH = "right_haunch";
   private final KeyframeAnimation hopAnimation;
   private final KeyframeAnimation idleHeadTiltAnimation;

   public RabbitModel(final ModelPart root, final AnimationDefinition hop, final AnimationDefinition idleHeadTilt) {
      super(root);
      this.hopAnimation = hop.bake(root);
      this.idleHeadTiltAnimation = idleHeadTilt.bake(root);
   }

   public void setupAnim(final RabbitRenderState state) {
      super.setupAnim(state);
      this.hopAnimation.apply(state.hopAnimationState, state.ageInTicks);
      this.idleHeadTiltAnimation.apply(state.idleHeadTiltAnimationState, state.ageInTicks);
   }
}
