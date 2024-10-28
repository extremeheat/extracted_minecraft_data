package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public abstract class RenderLayer<T extends Entity, M extends EntityModel<T>> {
   private final RenderLayerParent<T, M> renderer;

   public RenderLayer(RenderLayerParent<T, M> var1) {
      super();
      this.renderer = var1;
   }

   protected static <T extends LivingEntity> void coloredCutoutModelCopyLayerRender(EntityModel<T> var0, EntityModel<T> var1, ResourceLocation var2, PoseStack var3, MultiBufferSource var4, int var5, T var6, float var7, float var8, float var9, float var10, float var11, float var12, int var13) {
      if (!var6.isInvisible()) {
         var0.copyPropertiesTo(var1);
         var1.prepareMobModel(var6, var7, var8, var12);
         var1.setupAnim(var6, var7, var8, var9, var10, var11);
         renderColoredCutoutModel(var1, var2, var3, var4, var5, var6, var13);
      }

   }

   protected static <T extends LivingEntity> void renderColoredCutoutModel(EntityModel<T> var0, ResourceLocation var1, PoseStack var2, MultiBufferSource var3, int var4, T var5, int var6) {
      VertexConsumer var7 = var3.getBuffer(RenderType.entityCutoutNoCull(var1));
      var0.renderToBuffer(var2, var7, var4, LivingEntityRenderer.getOverlayCoords(var5, 0.0F), var6);
   }

   public M getParentModel() {
      return this.renderer.getModel();
   }

   protected ResourceLocation getTextureLocation(T var1) {
      return this.renderer.getTextureLocation(var1);
   }

   public abstract void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10);
}
