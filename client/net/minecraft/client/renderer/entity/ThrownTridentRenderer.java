package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.ThrownTrident;

public class ThrownTridentRenderer extends EntityRenderer<ThrownTrident, ThrownTridentRenderState> {
   public static final ResourceLocation TRIDENT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/trident.png");
   private final TridentModel model;

   public ThrownTridentRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new TridentModel(var1.bakeLayer(ModelLayers.TRIDENT));
   }

   public void render(ThrownTridentRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      var2.mulPose(Axis.YP.rotationDegrees(var1.yRot - 90.0F));
      var2.mulPose(Axis.ZP.rotationDegrees(var1.xRot + 90.0F));
      VertexConsumer var5 = ItemRenderer.getFoilBufferDirect(var3, this.model.renderType(this.getTextureLocation(var1)), false, var1.isFoil);
      this.model.renderToBuffer(var2, var5, var4, OverlayTexture.NO_OVERLAY);
      var2.popPose();
      super.render(var1, var2, var3, var4);
   }

   public ResourceLocation getTextureLocation(ThrownTridentRenderState var1) {
      return TRIDENT_LOCATION;
   }

   public ThrownTridentRenderState createRenderState() {
      return new ThrownTridentRenderState();
   }

   public void extractRenderState(ThrownTrident var1, ThrownTridentRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.yRot = var1.getYRot(var3);
      var2.xRot = var1.getXRot(var3);
      var2.isFoil = var1.isFoil();
   }
}
