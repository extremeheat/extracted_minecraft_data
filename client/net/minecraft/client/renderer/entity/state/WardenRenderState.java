package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.AnimationState;

public class WardenRenderState extends LivingEntityRenderState {
   public float tendrilAnimation;
   public float heartAnimation;
   public final AnimationState roarAnimationState = new AnimationState();
   public final AnimationState sniffAnimationState = new AnimationState();
   public final AnimationState emergeAnimationState = new AnimationState();
   public final AnimationState diggingAnimationState = new AnimationState();
   public final AnimationState attackAnimationState = new AnimationState();
   public final AnimationState sonicBoomAnimationState = new AnimationState();

   public WardenRenderState() {
      super();
   }
}