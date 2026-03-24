package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.world.entity.animal.cow.MushroomCow;

public class MushroomCowRenderState extends LivingEntityRenderState {
   public MushroomCow.Variant variant;
   public final BlockModelRenderState mushroomModel;

   public MushroomCowRenderState() {
      super();
      this.variant = MushroomCow.Variant.RED;
      this.mushroomModel = new BlockModelRenderState();
   }
}
