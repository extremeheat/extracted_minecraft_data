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
   public GuiProfilerChartRenderer(MultiBufferSource.BufferSource var1) {
      super(var1);
   }

   public Class<GuiProfilerChartRenderState> getRenderStateClass() {
      return GuiProfilerChartRenderState.class;
   }

   protected void renderToTexture(GuiProfilerChartRenderState var1, PoseStack var2) {
      double var3 = 0.0;
      var2.translate(0.0F, -5.0F, 0.0F);
      Matrix4f var5 = var2.last().pose();

      for(ResultField var7 : var1.chartData()) {
         int var8 = Mth.floor(var7.percentage / 4.0) + 1;
         VertexConsumer var9 = this.bufferSource.getBuffer(RenderTypes.debugTriangleFan());
         int var10 = ARGB.opaque(var7.getColor());
         int var11 = ARGB.multiply(var10, -8355712);
         var9.addVertex((Matrix4fc)var5, 0.0F, 0.0F, 0.0F).setColor(var10);

         for(int var12 = var8; var12 >= 0; --var12) {
            float var13 = (float)((var3 + var7.percentage * (double)var12 / (double)var8) * 6.2831854820251465 / 100.0);
            float var14 = Mth.sin((double)var13) * 105.0F;
            float var15 = Mth.cos((double)var13) * 105.0F * 0.5F;
            var9.addVertex((Matrix4fc)var5, var14, var15, 0.0F).setColor(var10);
         }

         var9 = this.bufferSource.getBuffer(RenderTypes.debugQuads());

         for(int var20 = var8; var20 > 0; --var20) {
            float var21 = (float)((var3 + var7.percentage * (double)var20 / (double)var8) * 6.2831854820251465 / 100.0);
            float var22 = Mth.sin((double)var21) * 105.0F;
            float var23 = Mth.cos((double)var21) * 105.0F * 0.5F;
            float var16 = (float)((var3 + var7.percentage * (double)(var20 - 1) / (double)var8) * 6.2831854820251465 / 100.0);
            float var17 = Mth.sin((double)var16) * 105.0F;
            float var18 = Mth.cos((double)var16) * 105.0F * 0.5F;
            if (!((var23 + var18) / 2.0F < 0.0F)) {
               var9.addVertex((Matrix4fc)var5, var22, var23, 0.0F).setColor(var11);
               var9.addVertex((Matrix4fc)var5, var22, var23 + 10.0F, 0.0F).setColor(var11);
               var9.addVertex((Matrix4fc)var5, var17, var18 + 10.0F, 0.0F).setColor(var11);
               var9.addVertex((Matrix4fc)var5, var17, var18, 0.0F).setColor(var11);
            }
         }

         var3 += var7.percentage;
      }

   }

   protected float getTranslateY(int var1, int var2) {
      return (float)var1 / 2.0F;
   }

   protected String getTextureLabel() {
      return "profiler chart";
   }
}
