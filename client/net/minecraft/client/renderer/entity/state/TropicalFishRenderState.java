package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishRenderState extends LivingEntityRenderState {
   public TropicalFish.Pattern variant = TropicalFish.Pattern.FLOPPER;
   public int baseColor = -1;
   public int patternColor = -1;

   public TropicalFishRenderState() {
      super();
   }
}
