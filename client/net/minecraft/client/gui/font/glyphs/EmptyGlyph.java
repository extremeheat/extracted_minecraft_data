package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class EmptyGlyph extends BakedGlyph {
   public EmptyGlyph() {
      super(RenderType.text(new ResourceLocation("")), RenderType.textSeeThrough(new ResourceLocation("")), RenderType.textPolygonOffset(new ResourceLocation("")), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void render(boolean var1, float var2, float var3, Matrix4f var4, VertexConsumer var5, float var6, float var7, float var8, float var9, int var10) {
   }
}
