package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class TextFeatureRenderer {
   public TextFeatureRenderer() {
      super();
   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context) {
      Font font = context.font();
      GlyphRenderer glyphRenderer = new GlyphRenderer(context.bufferSource());

      for(Submit submit : nodeCollection.getTextSubmits()) {
         glyphRenderer.pose.set(submit.pose());
         glyphRenderer.lightCoords = submit.lightCoords();
         glyphRenderer.displayMode = submit.displayMode();
         if (submit.outlineColor() == 0) {
            Font.PreparedText text = font.prepareText(submit.string(), submit.x(), submit.y(), submit.color(), submit.dropShadow(), false, submit.backgroundColor());
            text.visit(glyphRenderer);
         } else {
            Font.PreparedText outline = font.prepare8xTextOutline(submit.string(), submit.x(), submit.y(), submit.outlineColor());
            Font.PreparedText text = font.prepareText(submit.string(), submit.x(), submit.y(), submit.color(), false, false, 0);
            glyphRenderer.displayMode = Font.DisplayMode.NORMAL;
            outline.visit(glyphRenderer);
            glyphRenderer.displayMode = Font.DisplayMode.POLYGON_OFFSET;
            text.visit(glyphRenderer);
         }
      }

   }

   private static class GlyphRenderer implements Font.GlyphVisitor {
      private final MultiBufferSource bufferSource;
      private final Matrix4f pose = new Matrix4f();
      private int lightCoords = 15728880;
      private Font.DisplayMode displayMode;

      private GlyphRenderer(final MultiBufferSource bufferSource) {
         super();
         this.displayMode = Font.DisplayMode.NORMAL;
         this.bufferSource = bufferSource;
      }

      public void acceptRenderable(final TextRenderable renderable) {
         VertexConsumer buffer = this.bufferSource.getBuffer(renderable.renderType(this.displayMode));
         renderable.render(this.pose, buffer, this.lightCoords, false);
      }
   }

   public static record Submit(Matrix4fc pose, float x, float y, FormattedCharSequence string, boolean dropShadow, Font.DisplayMode displayMode, int lightCoords, int color, int backgroundColor, int outlineColor) {
      public Submit {
         super();
      }
   }
}
