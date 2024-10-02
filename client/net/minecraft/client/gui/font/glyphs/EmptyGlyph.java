package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class EmptyGlyph extends BakedGlyph {
   public static final EmptyGlyph INSTANCE = new EmptyGlyph();

   public EmptyGlyph() {
      super(GlyphRenderTypes.createForColorTexture(ResourceLocation.withDefaultNamespace("")), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   @Override
   public void render(boolean var1, float var2, float var3, Matrix4f var4, VertexConsumer var5, int var6, int var7) {
   }
}
