package net.minecraft.client.renderer.entity.state;

public class StriderRenderState extends LivingEntityRenderState implements SaddleableRenderState {
   public boolean isSaddled;
   public boolean isSuffocating;
   public boolean isRidden;

   public StriderRenderState() {
      super();
   }

   @Override
   public boolean isSaddled() {
      return this.isSaddled;
   }
}
