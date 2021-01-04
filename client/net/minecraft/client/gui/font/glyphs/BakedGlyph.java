package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.vertex.BufferBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class BakedGlyph {
   private final ResourceLocation texture;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private final float left;
   private final float right;
   private final float up;
   private final float down;

   public BakedGlyph(ResourceLocation var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      super();
      this.texture = var1;
      this.u0 = var2;
      this.u1 = var3;
      this.v0 = var4;
      this.v1 = var5;
      this.left = var6;
      this.right = var7;
      this.up = var8;
      this.down = var9;
   }

   public void render(TextureManager var1, boolean var2, float var3, float var4, BufferBuilder var5, float var6, float var7, float var8, float var9) {
      boolean var10 = true;
      float var11 = var3 + this.left;
      float var12 = var3 + this.right;
      float var13 = this.up - 3.0F;
      float var14 = this.down - 3.0F;
      float var15 = var4 + var13;
      float var16 = var4 + var14;
      float var17 = var2 ? 1.0F - 0.25F * var13 : 0.0F;
      float var18 = var2 ? 1.0F - 0.25F * var14 : 0.0F;
      var5.vertex((double)(var11 + var17), (double)var15, 0.0D).uv((double)this.u0, (double)this.v0).color(var6, var7, var8, var9).endVertex();
      var5.vertex((double)(var11 + var18), (double)var16, 0.0D).uv((double)this.u0, (double)this.v1).color(var6, var7, var8, var9).endVertex();
      var5.vertex((double)(var12 + var18), (double)var16, 0.0D).uv((double)this.u1, (double)this.v1).color(var6, var7, var8, var9).endVertex();
      var5.vertex((double)(var12 + var17), (double)var15, 0.0D).uv((double)this.u1, (double)this.v0).color(var6, var7, var8, var9).endVertex();
   }

   @Nullable
   public ResourceLocation getTexture() {
      return this.texture;
   }
}
