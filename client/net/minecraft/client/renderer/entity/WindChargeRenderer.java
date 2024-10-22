package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.WindChargeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;

public class WindChargeRenderer extends EntityRenderer<AbstractWindCharge, EntityRenderState> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/wind_charge.png");
   private final WindChargeModel model;

   public WindChargeRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new WindChargeModel(var1.bakeLayer(ModelLayers.WIND_CHARGE));
   }

   @Override
   public void render(EntityRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      VertexConsumer var5 = var3.getBuffer(RenderType.breezeWind(TEXTURE_LOCATION, this.xOffset(var1.ageInTicks) % 1.0F, 0.0F));
      this.model.setupAnim(var1);
      this.model.renderToBuffer(var2, var5, var4, OverlayTexture.NO_OVERLAY);
      super.render(var1, var2, var3, var4);
   }

   protected float xOffset(float var1) {
      return var1 * 0.03F;
   }

   @Override
   public EntityRenderState createRenderState() {
      return new EntityRenderState();
   }
}
