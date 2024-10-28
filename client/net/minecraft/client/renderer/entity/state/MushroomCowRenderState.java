package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.MushroomCow;

public class MushroomCowRenderState extends LivingEntityRenderState {
   public MushroomCow.Variant variant;

   public MushroomCowRenderState() {
      super();
      this.variant = MushroomCow.Variant.RED;
   }
}
