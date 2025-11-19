package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.fish.TropicalFish;

public class TropicalFishRenderState extends LivingEntityRenderState {
   public TropicalFish.Pattern pattern;
   public int baseColor;
   public int patternColor;

   public TropicalFishRenderState() {
      super();
      this.pattern = TropicalFish.Pattern.FLOPPER;
      this.baseColor = -1;
      this.patternColor = -1;
   }
}
