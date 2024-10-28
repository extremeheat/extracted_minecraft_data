package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Saddleable;

public class SaddleLayer<T extends Entity & Saddleable, M extends EntityModel<T>> extends RenderLayer<T, M> {
   private final ResourceLocation textureLocation;
   private final M model;

   public SaddleLayer(RenderLayerParent<T, M> var1, M var2, ResourceLocation var3) {
      super(var1);
      this.model = var2;
      this.textureLocation = var3;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (((Saddleable)var4).isSaddled()) {
         this.getParentModel().copyPropertiesTo(this.model);
         this.model.prepareMobModel(var4, var5, var6, var7);
         this.model.setupAnim(var4, var5, var6, var8, var9, var10);
         VertexConsumer var11 = var2.getBuffer(RenderType.entityCutoutNoCull(this.textureLocation));
         this.model.renderToBuffer(var1, var11, var3, OverlayTexture.NO_OVERLAY);
      }
   }
}
