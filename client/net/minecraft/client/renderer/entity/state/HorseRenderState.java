package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.equine.Markings;
import net.minecraft.world.entity.animal.equine.Variant;

public class HorseRenderState extends EquineRenderState {
   public Variant variant;
   public Markings markings;

   public HorseRenderState() {
      super();
      this.variant = Variant.WHITE;
      this.markings = Markings.NONE;
   }
}
