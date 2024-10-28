package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.WindChargeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;

public class WindChargeRenderer extends EntityRenderer<AbstractWindCharge> {
   private static final float MIN_CAMERA_DISTANCE_SQUARED = Mth.square(3.5F);
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/projectiles/wind_charge.png");
   private final WindChargeModel model;

   public WindChargeRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new WindChargeModel(var1.bakeLayer(ModelLayers.WIND_CHARGE));
   }

   public void render(AbstractWindCharge var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      if (var1.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr((Entity)var1) < (double)MIN_CAMERA_DISTANCE_SQUARED)) {
         float var7 = (float)var1.tickCount + var3;
         VertexConsumer var8 = var5.getBuffer(RenderType.breezeWind(TEXTURE_LOCATION, this.xOffset(var7) % 1.0F, 0.0F));
         this.model.setupAnim(var1, 0.0F, 0.0F, var7, 0.0F, 0.0F);
         this.model.renderToBuffer(var4, var8, var6, OverlayTexture.NO_OVERLAY);
         super.render(var1, var2, var3, var4, var5, var6);
      }
   }

   protected float xOffset(float var1) {
      return var1 * 0.03F;
   }

   public ResourceLocation getTextureLocation(AbstractWindCharge var1) {
      return TEXTURE_LOCATION;
   }
}
