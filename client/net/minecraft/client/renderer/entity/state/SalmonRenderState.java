package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.Salmon;

public class SalmonRenderState extends LivingEntityRenderState {
   public Salmon.Variant variant;

   public SalmonRenderState() {
      super();
      this.variant = Salmon.Variant.MEDIUM;
   }
}
