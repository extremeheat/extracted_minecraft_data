package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WindChargeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
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

   public void submit(EntityRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var3.submitModel(this.model, var1, var2, RenderType.breezeWind(TEXTURE_LOCATION, this.xOffset(var1.ageInTicks) % 1.0F, 0.0F), var1.lightCoords, OverlayTexture.NO_OVERLAY, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      super.submit(var1, var2, var3, var4);
   }

   protected float xOffset(float var1) {
      return var1 * 0.03F;
   }

   public EntityRenderState createRenderState() {
      return new EntityRenderState();
   }
}
