package net.minecraft.client.renderer.model;

import java.util.Arrays;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BakedQuadRetextured extends BakedQuad {
   private final TextureAtlasSprite field_178218_d;

   public BakedQuadRetextured(BakedQuad var1, TextureAtlasSprite var2) {
      super(Arrays.copyOf(var1.func_178209_a(), var1.func_178209_a().length), var1.field_178213_b, FaceBakery.func_178410_a(var1.func_178209_a()), var1.func_187508_a());
      this.field_178218_d = var2;
      this.func_178217_e();
   }

   private void func_178217_e() {
      for(int var1 = 0; var1 < 4; ++var1) {
         int var2 = 7 * var1;
         this.field_178215_a[var2 + 4] = Float.floatToRawIntBits(this.field_178218_d.func_94214_a((double)this.field_187509_d.func_188537_a(Float.intBitsToFloat(this.field_178215_a[var2 + 4]))));
         this.field_178215_a[var2 + 4 + 1] = Float.floatToRawIntBits(this.field_178218_d.func_94207_b((double)this.field_187509_d.func_188536_b(Float.intBitsToFloat(this.field_178215_a[var2 + 4 + 1]))));
      }

   }
}
