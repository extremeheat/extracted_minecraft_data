package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class TextFeatureRenderer extends RenderTypeFeatureRenderer<Submit> {
   public static final FeatureRendererType<Submit> TYPE = FeatureRendererType.<Submit>create("Text");

   public TextFeatureRenderer() {
      super();
   }

   protected void buildGroup(final FeatureFrameContext context, final List<Submit> submits) {
      Font font = context.font();
      GlyphRenderer glyphRenderer = new GlyphRenderer();

      for(Submit submit : submits) {
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

   private class GlyphRenderer implements Font.GlyphVisitor {
      private final Matrix4f pose;
      private int lightCoords;
      private Font.DisplayMode displayMode;

      private GlyphRenderer() {
         Objects.requireNonNull(TextFeatureRenderer.this);
         super();
         this.pose = new Matrix4f();
         this.lightCoords = 15728880;
         this.displayMode = Font.DisplayMode.NORMAL;
      }

      public void acceptRenderable(final TextRenderable renderable) {
         VertexConsumer builder = TextFeatureRenderer.this.getVertexBuilder(renderable.renderType(this.displayMode));
         renderable.render(this.pose, builder, this.lightCoords, false);
      }
   }

   public static record Submit(Matrix4fc pose, float x, float y, FormattedCharSequence string, boolean dropShadow, Font.DisplayMode displayMode, int lightCoords, int color, int backgroundColor, int outlineColor) implements SubmitNode {
      public Submit {
         super();
      }

      public FeatureRendererType<Submit> featureType() {
         return TextFeatureRenderer.TYPE;
      }
   }
}
