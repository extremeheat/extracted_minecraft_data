package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.animal.rabbit.Rabbit;

public class RabbitRenderState extends LivingEntityRenderState {
   public float jumpCompletion;
   public boolean isToast;
   public Rabbit.Variant variant;
   public final AnimationState hopAnimationState;
   public final AnimationState idleHeadTiltAnimationState;

   public RabbitRenderState() {
      super();
      this.variant = Rabbit.Variant.DEFAULT;
      this.hopAnimationState = new AnimationState();
      this.idleHeadTiltAnimationState = new AnimationState();
   }
}
