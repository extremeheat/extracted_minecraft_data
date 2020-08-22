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

public abstract class RenderLayer {
   private final RenderLayerParent renderer;

   public RenderLayer(RenderLayerParent var1) {
      this.renderer = var1;
   }

   protected static void coloredCutoutModelCopyLayerRender(EntityModel var0, EntityModel var1, ResourceLocation var2, PoseStack var3, MultiBufferSource var4, int var5, LivingEntity var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15) {
      if (!var6.isInvisible()) {
         var0.copyPropertiesTo(var1);
         var1.prepareMobModel(var6, var7, var8, var12);
         var1.setupAnim(var6, var7, var8, var9, var10, var11);
         renderColoredCutoutModel(var1, var2, var3, var4, var5, var6, var13, var14, var15);
      }

   }

   protected static void renderColoredCutoutModel(EntityModel var0, ResourceLocation var1, PoseStack var2, MultiBufferSource var3, int var4, LivingEntity var5, float var6, float var7, float var8) {
      VertexConsumer var9 = var3.getBuffer(RenderType.entityCutoutNoCull(var1));
      var0.renderToBuffer(var2, var9, var4, LivingEntityRenderer.getOverlayCoords(var5, 0.0F), var6, var7, var8, 1.0F);
   }

   public EntityModel getParentModel() {
      return this.renderer.getModel();
   }

   protected ResourceLocation getTextureLocation(Entity var1) {
      return this.renderer.getTextureLocation(var1);
   }

   public abstract void render(PoseStack var1, MultiBufferSource var2, int var3, Entity var4, float var5, float var6, float var7, float var8, float var9, float var10);
}
