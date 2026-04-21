package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

public class NameTagFeatureRenderer {
   public NameTagFeatureRenderer() {
      super();
   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context) {
      GlyphRenderer glyphRenderer = new GlyphRenderer(context.bufferSource());
      Storage storage = nodeCollection.getNameTagSubmits();
      storage.nameTagSubmitsSeethrough.sort(Comparator.comparing(Submit::distanceToCameraSq).reversed());

      for(Submit nameTag : storage.nameTagSubmitsSeethrough) {
         Font.PreparedText preparedText = prepareText(context.font(), nameTag);
         glyphRenderer.prepare(nameTag, Font.DisplayMode.SEE_THROUGH);
         preparedText.visit(glyphRenderer);
      }

      for(Submit nameTag : storage.nameTagSubmitsNormal) {
         Font.PreparedText preparedText = prepareText(context.font(), nameTag);
         glyphRenderer.prepare(nameTag, Font.DisplayMode.NORMAL);
         preparedText.visit(glyphRenderer);
      }

   }

   private static Font.PreparedText prepareText(final Font font, final Submit nameTag) {
      return font.prepareText(nameTag.text().getVisualOrderText(), nameTag.x(), nameTag.y(), nameTag.color(), false, false, nameTag.backgroundColor());
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

      public void prepare(final Submit submit, final Font.DisplayMode displayMode) {
         this.pose.set(submit.pose());
         this.lightCoords = submit.lightCoords();
         this.displayMode = displayMode;
      }

      public void acceptRenderable(final TextRenderable renderable) {
         VertexConsumer buffer = this.bufferSource.getBuffer(renderable.renderType(this.displayMode));
         renderable.render(this.pose, buffer, this.lightCoords, false);
      }
   }

   public static class Storage {
      private final List<Submit> nameTagSubmitsSeethrough = new ArrayList();
      private final List<Submit> nameTagSubmitsNormal = new ArrayList();

      public Storage() {
         super();
      }

      public void add(final PoseStack poseStack, final @Nullable Vec3 nameTagAttachment, final int offset, final Component name, final boolean seeThrough, final int lightCoords, final CameraRenderState camera) {
         if (nameTagAttachment != null) {
            Minecraft minecraft = Minecraft.getInstance();
            poseStack.pushPose();
            poseStack.translate(nameTagAttachment.x, nameTagAttachment.y + 0.5, nameTagAttachment.z);
            poseStack.mulPose((Quaternionfc)camera.orientation);
            poseStack.scale(0.025F, -0.025F, 0.025F);
            Matrix4f pose = new Matrix4f(poseStack.last().pose());
            float x = (float)(-minecraft.font.width((FormattedText)name)) / 2.0F;
            int backgroundColor = (int)(minecraft.gameRenderer.gameRenderState().optionsRenderState.getBackgroundOpacity(0.25F) * 255.0F) << 24;
            if (seeThrough) {
               this.nameTagSubmitsNormal.add(new Submit(pose, x, (float)offset, name, LightCoordsUtil.lightCoordsWithEmission(lightCoords, 2), -1, 0));
               this.nameTagSubmitsSeethrough.add(new Submit(pose, x, (float)offset, name, lightCoords, -2130706433, backgroundColor));
            } else {
               this.nameTagSubmitsNormal.add(new Submit(pose, x, (float)offset, name, lightCoords, -2130706433, backgroundColor));
            }

            poseStack.popPose();
         }
      }

      public void clear() {
         this.nameTagSubmitsNormal.clear();
         this.nameTagSubmitsSeethrough.clear();
      }
   }

   public static record Submit(Matrix4fc pose, float x, float y, Component text, int lightCoords, int color, int backgroundColor) implements TranslucentSubmit {
      public Submit {
         super();
      }

      public float distanceToCameraSq() {
         return TranslucentSubmit.computeDistanceToCameraSq(this.pose);
      }
   }
}
