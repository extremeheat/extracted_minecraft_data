package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.Salmon;

public class SalmonRenderState extends LivingEntityRenderState {
   public Salmon.Variant variant = Salmon.Variant.MEDIUM;

   public SalmonRenderState() {
      super();
   }
}
