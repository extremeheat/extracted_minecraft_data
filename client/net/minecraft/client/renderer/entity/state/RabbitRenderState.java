package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.Rabbit;

public class RabbitRenderState extends LivingEntityRenderState {
   public float jumpCompletion;
   public boolean isToast;
   public Rabbit.Variant variant = Rabbit.Variant.BROWN;

   public RabbitRenderState() {
      super();
   }
}
