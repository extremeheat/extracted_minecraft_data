package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class NameTagFeatureRenderer extends RenderTypeFeatureRenderer<Submit> {
   public static final FeatureRendererType<Submit> TYPE = FeatureRendererType.<Submit>create("Name Tag");

   public NameTagFeatureRenderer() {
      super();
   }

   protected void buildGroup(final FeatureFrameContext context, final List<Submit> submits) {
      GlyphRenderer glyphRenderer = new GlyphRenderer();

      for(Submit nameTag : submits) {
         Font.PreparedText preparedText = prepareText(context.font(), nameTag);
         glyphRenderer.prepare(nameTag, nameTag.displayMode());
         preparedText.visit(glyphRenderer);
      }

   }

   private static Font.PreparedText prepareText(final Font font, final Submit nameTag) {
      return font.prepareText(nameTag.text().getVisualOrderText(), nameTag.x(), nameTag.y(), nameTag.color(), false, false, nameTag.backgroundColor());
   }

   private class GlyphRenderer implements Font.GlyphVisitor {
      private final Matrix4f pose;
      private int lightCoords;
      private Font.DisplayMode displayMode;

      private GlyphRenderer() {
         Objects.requireNonNull(NameTagFeatureRenderer.this);
         super();
         this.pose = new Matrix4f();
         this.lightCoords = 15728880;
         this.displayMode = Font.DisplayMode.NORMAL;
      }

      public void prepare(final Submit submit, final Font.DisplayMode displayMode) {
         this.pose.set(submit.pose());
         this.lightCoords = submit.lightCoords();
         this.displayMode = displayMode;
      }

      public void acceptRenderable(final TextRenderable renderable) {
         VertexConsumer builder = NameTagFeatureRenderer.this.getVertexBuilder(renderable.renderType(this.displayMode));
         renderable.render(this.pose, builder, this.lightCoords, false);
      }
   }

   public static record Submit(Matrix4fc pose, float x, float y, Component text, int lightCoords, int color, int backgroundColor, Font.DisplayMode displayMode) implements TranslucentSubmit {
      public Submit {
         super();
      }

      public float distanceToCameraSq() {
         return TranslucentSubmit.computeDistanceToCameraSq(this.pose);
      }

      public FeatureRendererType<Submit> featureType() {
         return NameTagFeatureRenderer.TYPE;
      }
   }
}
