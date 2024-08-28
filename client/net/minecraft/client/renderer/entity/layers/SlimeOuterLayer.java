package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;

public class SlimeOuterLayer extends RenderLayer<SlimeRenderState, SlimeModel> {
   private final SlimeModel model;

   public SlimeOuterLayer(RenderLayerParent<SlimeRenderState, SlimeModel> var1, EntityModelSet var2) {
      super(var1);
      this.model = new SlimeModel(var2.bakeLayer(ModelLayers.SLIME_OUTER));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, SlimeRenderState var4, float var5, float var6) {
      boolean var7 = var4.appearsGlowing && var4.isInvisible;
      if (!var4.isInvisible || var7) {
         VertexConsumer var8;
         if (var7) {
            var8 = var2.getBuffer(RenderType.outline(SlimeRenderer.SLIME_LOCATION));
         } else {
            var8 = var2.getBuffer(RenderType.entityTranslucent(SlimeRenderer.SLIME_LOCATION));
         }

         this.model.setupAnim(var4);
         this.model.renderToBuffer(var1, var8, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F));
      }
   }
}
