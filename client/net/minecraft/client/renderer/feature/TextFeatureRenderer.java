package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
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
         Content var10000 = submit.content();
         Objects.requireNonNull(var10000);
         Content var7 = var10000;
         byte var8 = 0;
         //$FF: var8->value
         //0->net/minecraft/client/renderer/feature/TextFeatureRenderer$Content$Text
         //1->net/minecraft/client/renderer/feature/TextFeatureRenderer$Content$StandaloneBackground
         switch (var7.typeSwitch<invokedynamic>(var7, var8)) {
            case 0:
               Content.Text text = (Content.Text)var7;
               renderText(font, glyphRenderer, text);
               break;
            case 1:
               Content.StandaloneBackground standaloneBackground = (Content.StandaloneBackground)var7;
               glyphRenderer.acceptRenderable(font.prepareBackground(standaloneBackground.x0(), standaloneBackground.y0(), standaloneBackground.x1(), standaloneBackground.y1(), standaloneBackground.color()));
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }
      }

   }

   private static void renderText(final Font font, final GlyphRenderer glyphRenderer, final Content.Text content) {
      if (content.outlineColor() == 0) {
         Font.PreparedText text = font.prepareText(content.string(), content.x(), content.y(), content.color(), content.dropShadow(), false, content.backgroundColor());
         text.visit(glyphRenderer);
      } else {
         Font.PreparedText outline = font.prepare8xTextOutline(content.string(), content.x(), content.y(), content.outlineColor());
         Font.PreparedText text = font.prepareText(content.string(), content.x(), content.y(), content.color(), false, false, 0);
         glyphRenderer.displayMode = Font.DisplayMode.NORMAL;
         outline.visit(glyphRenderer);
         glyphRenderer.displayMode = Font.DisplayMode.POLYGON_OFFSET;
         text.visit(glyphRenderer);
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

   public static record Submit(Matrix4fc pose, Font.DisplayMode displayMode, int lightCoords, Content content) implements TranslucentSubmit {
      public Submit {
         super();
      }

      public float distanceToCameraSq() {
         return TranslucentSubmit.computeDistanceToCameraSq(this.pose);
      }

      public FeatureRendererType<Submit> featureType() {
         return TextFeatureRenderer.TYPE;
      }
   }

   public sealed interface Content {
      public static record Text(float x, float y, FormattedCharSequence string, boolean dropShadow, int color, int backgroundColor, int outlineColor) implements Content {
         public Text {
            super();
         }
      }

      public static record StandaloneBackground(float x0, float y0, float x1, float y1, int color) implements Content {
         public StandaloneBackground {
            super();
         }
      }
   }
}
