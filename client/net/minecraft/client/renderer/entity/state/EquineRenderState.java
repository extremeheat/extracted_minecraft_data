package net.minecraft.client.renderer.entity.state;

public class EquineRenderState extends LivingEntityRenderState {
   public boolean isSaddled;
   public boolean isRidden;
   public boolean animateTail;
   public float eatAnimation;
   public float standAnimation;
   public float feedingAnimation;

   public EquineRenderState() {
      super();
   }
}
