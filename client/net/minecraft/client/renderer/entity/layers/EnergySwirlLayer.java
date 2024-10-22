package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public abstract class EnergySwirlLayer<S extends EntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
   public EnergySwirlLayer(RenderLayerParent<S, M> var1) {
      super(var1);
   }

   @Override
   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      if (this.isPowered(var4)) {
         float var7 = var4.ageInTicks;
         EntityModel var8 = this.model();
         VertexConsumer var9 = var2.getBuffer(RenderType.energySwirl(this.getTextureLocation(), this.xOffset(var7) % 1.0F, var7 * 0.01F % 1.0F));
         var8.setupAnim(var4);
         var8.renderToBuffer(var1, var9, var3, OverlayTexture.NO_OVERLAY, -8355712);
      }
   }

   protected abstract boolean isPowered(S var1);

   protected abstract float xOffset(float var1);

   protected abstract ResourceLocation getTextureLocation();

   protected abstract M model();
}
