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
   public void renderChar(BakedGlyph.GlyphInstance var1, Matrix4f var2, VertexConsumer var3, int var4) {
   }
}
