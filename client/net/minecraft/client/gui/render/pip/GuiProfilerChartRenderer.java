package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.render.state.pip.GuiProfilerChartRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ResultField;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class GuiProfilerChartRenderer extends PictureInPictureRenderer<GuiProfilerChartRenderState> {
   public GuiProfilerChartRenderer(final MultiBufferSource.BufferSource bufferSource) {
      super(bufferSource);
   }

   public Class<GuiProfilerChartRenderState> getRenderStateClass() {
      return GuiProfilerChartRenderState.class;
   }

   protected void renderToTexture(final GuiProfilerChartRenderState chartState, final PoseStack poseStack) {
      double totalPercentage = 0.0;
      poseStack.translate(0.0F, -5.0F, 0.0F);
      Matrix4f pose = poseStack.last().pose();

      for(ResultField result : chartState.chartData()) {
         int steps = Mth.floor(result.percentage / 4.0) + 1;
         VertexConsumer buffer = this.bufferSource.getBuffer(RenderTypes.debugTriangleFan());
         int color = ARGB.opaque(result.getColor());
         int shadeColor = ARGB.multiply(color, -8355712);
         buffer.addVertex((Matrix4fc)pose, 0.0F, 0.0F, 0.0F).setColor(color);

         for(int j = steps; j >= 0; --j) {
            float dir = (float)((totalPercentage + result.percentage * (double)j / (double)steps) * 6.2831854820251465 / 100.0);
            float xx = Mth.sin((double)dir) * 105.0F;
            float yy = Mth.cos((double)dir) * 105.0F * 0.5F;
            buffer.addVertex((Matrix4fc)pose, xx, yy, 0.0F).setColor(color);
         }

         buffer = this.bufferSource.getBuffer(RenderTypes.debugQuads());

         for(int j = steps; j > 0; --j) {
            float dir0 = (float)((totalPercentage + result.percentage * (double)j / (double)steps) * 6.2831854820251465 / 100.0);
            float x0 = Mth.sin((double)dir0) * 105.0F;
            float y0 = Mth.cos((double)dir0) * 105.0F * 0.5F;
            float dir1 = (float)((totalPercentage + result.percentage * (double)(j - 1) / (double)steps) * 6.2831854820251465 / 100.0);
            float x1 = Mth.sin((double)dir1) * 105.0F;
            float y1 = Mth.cos((double)dir1) * 105.0F * 0.5F;
            if (!((y0 + y1) / 2.0F < 0.0F)) {
               buffer.addVertex((Matrix4fc)pose, x0, y0, 0.0F).setColor(shadeColor);
               buffer.addVertex((Matrix4fc)pose, x0, y0 + 10.0F, 0.0F).setColor(shadeColor);
               buffer.addVertex((Matrix4fc)pose, x1, y1 + 10.0F, 0.0F).setColor(shadeColor);
               buffer.addVertex((Matrix4fc)pose, x1, y1, 0.0F).setColor(shadeColor);
            }
         }

         totalPercentage += result.percentage;
      }

   }

   protected float getTranslateY(final int height, final int guiScale) {
      return (float)height / 2.0F;
   }

   protected String getTextureLabel() {
      return "profiler chart";
   }
}
