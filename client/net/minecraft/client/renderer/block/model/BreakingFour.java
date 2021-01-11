package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BreakingFour extends BakedQuad {
   private final TextureAtlasSprite field_178218_d;

   public BreakingFour(BakedQuad var1, TextureAtlasSprite var2) {
      super(Arrays.copyOf(var1.func_178209_a(), var1.func_178209_a().length), var1.field_178213_b, FaceBakery.func_178410_a(var1.func_178209_a()));
      this.field_178218_d = var2;
      this.func_178217_e();
   }

   private void func_178217_e() {
      for(int var1 = 0; var1 < 4; ++var1) {
         this.func_178216_a(var1);
      }

   }

   private void func_178216_a(int var1) {
      int var2 = 7 * var1;
      float var3 = Float.intBitsToFloat(this.field_178215_a[var2]);
      float var4 = Float.intBitsToFloat(this.field_178215_a[var2 + 1]);
      float var5 = Float.intBitsToFloat(this.field_178215_a[var2 + 2]);
      float var6 = 0.0F;
      float var7 = 0.0F;
      switch(this.field_178214_c) {
      case DOWN:
         var6 = var3 * 16.0F;
         var7 = (1.0F - var5) * 16.0F;
         break;
      case UP:
         var6 = var3 * 16.0F;
         var7 = var5 * 16.0F;
         break;
      case NORTH:
         var6 = (1.0F - var3) * 16.0F;
         var7 = (1.0F - var4) * 16.0F;
         break;
      case SOUTH:
         var6 = var3 * 16.0F;
         var7 = (1.0F - var4) * 16.0F;
         break;
      case WEST:
         var6 = var5 * 16.0F;
         var7 = (1.0F - var4) * 16.0F;
         break;
      case EAST:
         var6 = (1.0F - var5) * 16.0F;
         var7 = (1.0F - var4) * 16.0F;
      }

      this.field_178215_a[var2 + 4] = Float.floatToRawIntBits(this.field_178218_d.func_94214_a((double)var6));
      this.field_178215_a[var2 + 4 + 1] = Float.floatToRawIntBits(this.field_178218_d.func_94207_b((double)var7));
   }
}
