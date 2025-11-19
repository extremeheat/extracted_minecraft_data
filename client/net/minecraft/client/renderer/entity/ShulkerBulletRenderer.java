package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.projectile.ShulkerBulletModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ShulkerBulletRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import org.joml.Quaternionfc;

public class ShulkerBulletRenderer extends EntityRenderer<ShulkerBullet, ShulkerBulletRenderState> {
   private static final Identifier TEXTURE_LOCATION = Identifier.withDefaultNamespace("textures/entity/shulker/spark.png");
   private static final RenderType RENDER_TYPE;
   private final ShulkerBulletModel model;

   public ShulkerBulletRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new ShulkerBulletModel(var1.bakeLayer(ModelLayers.SHULKER_BULLET));
   }

   protected int getBlockLightLevel(ShulkerBullet var1, BlockPos var2) {
      return 15;
   }

   public void submit(ShulkerBulletRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var2.pushPose();
      float var5 = var1.ageInTicks;
      var2.translate(0.0F, 0.15F, 0.0F);
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(Mth.sin((double)(var5 * 0.1F)) * 180.0F));
      var2.mulPose((Quaternionfc)Axis.XP.rotationDegrees(Mth.cos((double)(var5 * 0.1F)) * 180.0F));
      var2.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(Mth.sin((double)(var5 * 0.15F)) * 360.0F));
      var2.scale(-0.5F, -0.5F, 0.5F);
      var3.submitModel(this.model, var1, var2, this.model.renderType(TEXTURE_LOCATION), var1.lightCoords, OverlayTexture.NO_OVERLAY, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      var2.scale(1.5F, 1.5F, 1.5F);
      var3.order(1).submitModel(this.model, var1, var2, RENDER_TYPE, var1.lightCoords, OverlayTexture.NO_OVERLAY, 654311423, (TextureAtlasSprite)null, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      var2.popPose();
      super.submit(var1, var2, var3, var4);
   }

   public ShulkerBulletRenderState createRenderState() {
      return new ShulkerBulletRenderState();
   }

   public void extractRenderState(ShulkerBullet var1, ShulkerBulletRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.yRot = var1.getYRot(var3);
      var2.xRot = var1.getXRot(var3);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   static {
      RENDER_TYPE = RenderTypes.entityTranslucent(TEXTURE_LOCATION);
   }
}
