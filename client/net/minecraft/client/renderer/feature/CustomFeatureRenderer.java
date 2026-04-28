package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.submit.BatchableSubmit;
import net.minecraft.client.renderer.rendertype.RenderType;

public class CustomFeatureRenderer extends RenderTypeFeatureRenderer<Submit> {
   public static final FeatureRendererType<Submit> TYPE = FeatureRendererType.<Submit>create("Custom");

   public CustomFeatureRenderer() {
      super();
   }

   protected void buildGroup(final FeatureFrameContext context, final List<Submit> submits) {
      for(Submit submit : submits) {
         VertexConsumer builder = this.getVertexBuilder(submit.renderType());
         submit.customGeometryRenderer().render(submit.pose(), builder);
      }

   }

   public static record Submit(PoseStack.Pose pose, RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) implements BatchableSubmit {
      public Submit {
         super();
      }

      public Object batchKey() {
         return this.renderType;
      }

      public FeatureRendererType<Submit> featureType() {
         return CustomFeatureRenderer.TYPE;
      }
   }
}
