package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.TropicalFish;

public class TropicalFishRenderState extends LivingEntityRenderState {
   public TropicalFish.Pattern variant;
   public int baseColor;
   public int patternColor;

   public TropicalFishRenderState() {
      super();
      this.variant = TropicalFish.Pattern.FLOPPER;
      this.baseColor = -1;
      this.patternColor = -1;
   }
}
