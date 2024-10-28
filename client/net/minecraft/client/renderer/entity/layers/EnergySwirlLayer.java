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
import net.minecraft.world.entity.PowerableMob;

public abstract class EnergySwirlLayer<T extends Entity & PowerableMob, M extends EntityModel<T>> extends RenderLayer<T, M> {
   public EnergySwirlLayer(RenderLayerParent<T, M> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (((PowerableMob)var4).isPowered()) {
         float var11 = (float)var4.tickCount + var7;
         EntityModel var12 = this.model();
         var12.prepareMobModel(var4, var5, var6, var7);
         this.getParentModel().copyPropertiesTo(var12);
         VertexConsumer var13 = var2.getBuffer(RenderType.energySwirl(this.getTextureLocation(), this.xOffset(var11) % 1.0F, var11 * 0.01F % 1.0F));
         var12.setupAnim(var4, var5, var6, var8, var9, var10);
         var12.renderToBuffer(var1, var13, var3, OverlayTexture.NO_OVERLAY, -8355712);
      }
   }

   protected abstract float xOffset(float var1);

   protected abstract ResourceLocation getTextureLocation();

   protected abstract EntityModel<T> model();
}
