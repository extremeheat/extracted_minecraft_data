package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;

public abstract class RenderLayer<S extends EntityRenderState, M extends EntityModel<? super S>> {
   private final RenderLayerParent<S, M> renderer;

   public RenderLayer(RenderLayerParent<S, M> var1) {
      super();
      this.renderer = var1;
   }

   protected static <S extends LivingEntityRenderState> void coloredCutoutModelCopyLayerRender(EntityModel<S> var0, ResourceLocation var1, PoseStack var2, MultiBufferSource var3, int var4, S var5, int var6) {
      if (!var5.isInvisible) {
         var0.setupAnim(var5);
         renderColoredCutoutModel(var0, var1, var2, var3, var4, var5, var6);
      }

   }

   protected static void renderColoredCutoutModel(EntityModel<?> var0, ResourceLocation var1, PoseStack var2, MultiBufferSource var3, int var4, LivingEntityRenderState var5, int var6) {
      VertexConsumer var7 = var3.getBuffer(RenderType.entityCutoutNoCull(var1));
      var0.renderToBuffer(var2, var7, var4, LivingEntityRenderer.getOverlayCoords(var5, 0.0F), var6);
   }

   public M getParentModel() {
      return this.renderer.getModel();
   }

   public abstract void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6);
}
