package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BreakingQuad extends BakedQuad {
   private final TextureAtlasSprite breakingIcon;

   public BreakingQuad(BakedQuad var1, TextureAtlasSprite var2) {
      super(Arrays.copyOf(var1.getVertices(), var1.getVertices().length), var1.tintIndex, FaceBakery.calculateFacing(var1.getVertices()), var1.getSprite());
      this.breakingIcon = var2;
      this.calculateBreakingUVs();
   }

   private void calculateBreakingUVs() {
      for(int var1 = 0; var1 < 4; ++var1) {
         int var2 = 7 * var1;
         this.vertices[var2 + 4] = Float.floatToRawIntBits(this.breakingIcon.getU((double)this.sprite.getUOffset(Float.intBitsToFloat(this.vertices[var2 + 4]))));
         this.vertices[var2 + 4 + 1] = Float.floatToRawIntBits(this.breakingIcon.getV((double)this.sprite.getVOffset(Float.intBitsToFloat(this.vertices[var2 + 4 + 1]))));
      }

   }
}
