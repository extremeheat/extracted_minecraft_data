package net.minecraft.client.gui.fonts;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class TexturedGlyph {
   private final ResourceLocation field_211235_a;
   private final float field_211236_b;
   private final float field_211237_c;
   private final float field_211238_d;
   private final float field_211239_e;
   private final float field_211240_f;
   private final float field_211241_g;
   private final float field_211242_h;
   private final float field_211243_i;

   public TexturedGlyph(ResourceLocation var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      super();
      this.field_211235_a = var1;
      this.field_211236_b = var2;
      this.field_211237_c = var3;
      this.field_211238_d = var4;
      this.field_211239_e = var5;
      this.field_211240_f = var6;
      this.field_211241_g = var7;
      this.field_211242_h = var8;
      this.field_211243_i = var9;
   }

   public void func_211234_a(TextureManager var1, boolean var2, float var3, float var4, BufferBuilder var5, float var6, float var7, float var8, float var9) {
      boolean var10 = true;
      float var11 = var3 + this.field_211240_f;
      float var12 = var3 + this.field_211241_g;
      float var13 = this.field_211242_h - 3.0F;
      float var14 = this.field_211243_i - 3.0F;
      float var15 = var4 + var13;
      float var16 = var4 + var14;
      float var17 = var2 ? 1.0F - 0.25F * var13 : 0.0F;
      float var18 = var2 ? 1.0F - 0.25F * var14 : 0.0F;
      var5.func_181662_b((double)(var11 + var17), (double)var15, 0.0D).func_187315_a((double)this.field_211236_b, (double)this.field_211238_d).func_181666_a(var6, var7, var8, var9).func_181675_d();
      var5.func_181662_b((double)(var11 + var18), (double)var16, 0.0D).func_187315_a((double)this.field_211236_b, (double)this.field_211239_e).func_181666_a(var6, var7, var8, var9).func_181675_d();
      var5.func_181662_b((double)(var12 + var18), (double)var16, 0.0D).func_187315_a((double)this.field_211237_c, (double)this.field_211239_e).func_181666_a(var6, var7, var8, var9).func_181675_d();
      var5.func_181662_b((double)(var12 + var17), (double)var15, 0.0D).func_187315_a((double)this.field_211237_c, (double)this.field_211238_d).func_181666_a(var6, var7, var8, var9).func_181675_d();
   }

   @Nullable
   public ResourceLocation func_211233_b() {
      return this.field_211235_a;
   }
}
