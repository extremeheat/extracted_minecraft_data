package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ShulkerBullet;

public class ShulkerBulletRenderer extends EntityRenderer<ShulkerBullet> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/shulker/spark.png");
   private static final RenderType RENDER_TYPE;
   private final ShulkerBulletModel<ShulkerBullet> model;

   public ShulkerBulletRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new ShulkerBulletModel(var1.bakeLayer(ModelLayers.SHULKER_BULLET));
   }

   protected int getBlockLightLevel(ShulkerBullet var1, BlockPos var2) {
      return 15;
   }

   public void render(ShulkerBullet var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      float var7 = Mth.rotLerp(var3, var1.yRotO, var1.getYRot());
      float var8 = Mth.lerp(var3, var1.xRotO, var1.getXRot());
      float var9 = (float)var1.tickCount + var3;
      var4.translate(0.0F, 0.15F, 0.0F);
      var4.mulPose(Axis.YP.rotationDegrees(Mth.sin(var9 * 0.1F) * 180.0F));
      var4.mulPose(Axis.XP.rotationDegrees(Mth.cos(var9 * 0.1F) * 180.0F));
      var4.mulPose(Axis.ZP.rotationDegrees(Mth.sin(var9 * 0.15F) * 360.0F));
      var4.scale(-0.5F, -0.5F, 0.5F);
      this.model.setupAnim(var1, 0.0F, 0.0F, 0.0F, var7, var8);
      VertexConsumer var10 = var5.getBuffer(this.model.renderType(TEXTURE_LOCATION));
      this.model.renderToBuffer(var4, var10, var6, OverlayTexture.NO_OVERLAY);
      var4.scale(1.5F, 1.5F, 1.5F);
      VertexConsumer var11 = var5.getBuffer(RENDER_TYPE);
      this.model.renderToBuffer(var4, var11, var6, OverlayTexture.NO_OVERLAY, 654311423);
      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public ResourceLocation getTextureLocation(ShulkerBullet var1) {
      return TEXTURE_LOCATION;
   }

   static {
      RENDER_TYPE = RenderType.entityTranslucent(TEXTURE_LOCATION);
   }
}
