package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;

public class PigSaddleLayer extends RenderLayer {
   private static final ResourceLocation SADDLE_LOCATION = new ResourceLocation("textures/entity/pig/pig_saddle.png");
   private final PigModel model = new PigModel(0.5F);

   public PigSaddleLayer(RenderLayerParent var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Pig var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (var4.hasSaddle()) {
         ((PigModel)this.getParentModel()).copyPropertiesTo(this.model);
         this.model.prepareMobModel(var4, var5, var6, var7);
         this.model.setupAnim(var4, var5, var6, var8, var9, var10);
         VertexConsumer var11 = var2.getBuffer(RenderType.entityCutoutNoCull(SADDLE_LOCATION));
         this.model.renderToBuffer(var1, var11, var3, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}
