package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.Fox;

public class FoxRenderState extends LivingEntityRenderState {
   public float headRollAngle;
   public float crouchAmount;
   public boolean isCrouching;
   public boolean isSleeping;
   public boolean isSitting;
   public boolean isFaceplanted;
   public boolean isPouncing;
   public Fox.Variant variant;

   public FoxRenderState() {
      super();
      this.variant = Fox.Variant.RED;
   }
}
