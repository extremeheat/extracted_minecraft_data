package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.ShulkerBulletRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ShulkerBullet;

public class ShulkerBulletRenderer extends EntityRenderer<ShulkerBullet, ShulkerBulletRenderState> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/shulker/spark.png");
   private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE_LOCATION);
   private final ShulkerBulletModel model;

   public ShulkerBulletRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new ShulkerBulletModel(var1.bakeLayer(ModelLayers.SHULKER_BULLET));
   }

   protected int getBlockLightLevel(ShulkerBullet var1, BlockPos var2) {
      return 15;
   }

   public void render(ShulkerBulletRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      float var5 = var1.ageInTicks;
      var2.translate(0.0F, 0.15F, 0.0F);
      var2.mulPose(Axis.YP.rotationDegrees(Mth.sin(var5 * 0.1F) * 180.0F));
      var2.mulPose(Axis.XP.rotationDegrees(Mth.cos(var5 * 0.1F) * 180.0F));
      var2.mulPose(Axis.ZP.rotationDegrees(Mth.sin(var5 * 0.15F) * 360.0F));
      var2.scale(-0.5F, -0.5F, 0.5F);
      this.model.setupAnim(var1);
      VertexConsumer var6 = var3.getBuffer(this.model.renderType(TEXTURE_LOCATION));
      this.model.renderToBuffer(var2, var6, var4, OverlayTexture.NO_OVERLAY);
      var2.scale(1.5F, 1.5F, 1.5F);
      VertexConsumer var7 = var3.getBuffer(RENDER_TYPE);
      this.model.renderToBuffer(var2, var7, var4, OverlayTexture.NO_OVERLAY, 654311423);
      var2.popPose();
      super.render(var1, var2, var3, var4);
   }

   public ResourceLocation getTextureLocation(ShulkerBulletRenderState var1) {
      return TEXTURE_LOCATION;
   }

   public ShulkerBulletRenderState createRenderState() {
      return new ShulkerBulletRenderState();
   }

   public void extractRenderState(ShulkerBullet var1, ShulkerBulletRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.yRot = var1.getYRot(var3);
      var2.xRot = var1.getXRot(var3);
   }
}
