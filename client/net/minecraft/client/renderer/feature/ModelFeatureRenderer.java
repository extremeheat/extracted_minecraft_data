package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.feature.submit.BatchableSubmit;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jspecify.annotations.Nullable;

public class ModelFeatureRenderer extends RenderTypeFeatureRenderer<Submit<?>> {
   public static final FeatureRendererType<Submit<?>> TYPE = FeatureRendererType.<Submit<?>>create("Entity Model");
   private final PoseStack poseStack = new PoseStack();

   public ModelFeatureRenderer() {
      super();
   }

   protected void buildGroup(final FeatureFrameContext context, final List<Submit<?>> submits) {
      for(Submit<?> submit : submits) {
         this.prepareModel(submit);
      }

   }

   private <S> void prepareModel(final Submit<S> submit) {
      this.poseStack.last().set(submit.pose());
      VertexConsumer buffer = this.getVertexBuilder(submit.renderType());
      if (submit.sheetedDecalPose() != null) {
         buffer = new SheetedDecalTextureGenerator(buffer, submit.sheetedDecalPose(), 1.0F);
      } else if (submit.sprite() != null) {
         buffer = submit.sprite().wrap(buffer);
      }

      Model<? super S> model = submit.model();
      model.setupAnim(submit.state());
      model.renderToBuffer(this.poseStack, buffer, submit.lightCoords(), submit.overlayCoords(), submit.tintedColor());
   }

   public static record CrumblingOverlay(int progress, PoseStack.Pose cameraPose) {
      public CrumblingOverlay {
         super();
      }
   }

   public static record Submit<S>(RenderType renderType, PoseStack.Pose pose, Model<? super S> model, S state, int lightCoords, int overlayCoords, int tintedColor, @Nullable TextureAtlasSprite sprite, PoseStack.@Nullable Pose sheetedDecalPose) implements BatchableSubmit, TranslucentSubmit {
      public Submit {
         super();
      }

      public Object batchKey() {
         return this.renderType;
      }

      public float distanceToCameraSq() {
         return TranslucentSubmit.computeDistanceToCameraSq(this.pose.pose());
      }

      public FeatureRendererType<Submit<S>> featureType() {
         return ModelFeatureRenderer.TYPE;
      }
   }
}
