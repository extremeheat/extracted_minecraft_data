package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.WitherSkullRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;

public class WitherSkullRenderer extends EntityRenderer<WitherSkull, WitherSkullRenderState> {
   private static final Identifier WITHER_INVULNERABLE_LOCATION = Identifier.withDefaultNamespace("textures/entity/wither/wither_invulnerable.png");
   private static final Identifier WITHER_LOCATION = Identifier.withDefaultNamespace("textures/entity/wither/wither.png");
   private final SkullModel model;

   public WitherSkullRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new SkullModel(var1.bakeLayer(ModelLayers.WITHER_SKULL));
   }

   public static LayerDefinition createSkullLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 35).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      return LayerDefinition.create(var0, 64, 64);
   }

   protected int getBlockLightLevel(WitherSkull var1, BlockPos var2) {
      return 15;
   }

   public void submit(WitherSkullRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var2.pushPose();
      var2.scale(-1.0F, -1.0F, 1.0F);
      var3.submitModel(this.model, var1.modelState, var2, this.model.renderType(this.getTextureLocation(var1)), var1.lightCoords, OverlayTexture.NO_OVERLAY, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      var2.popPose();
      super.submit(var1, var2, var3, var4);
   }

   private Identifier getTextureLocation(WitherSkullRenderState var1) {
      return var1.isDangerous ? WITHER_INVULNERABLE_LOCATION : WITHER_LOCATION;
   }

   public WitherSkullRenderState createRenderState() {
      return new WitherSkullRenderState();
   }

   public void extractRenderState(WitherSkull var1, WitherSkullRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isDangerous = var1.isDangerous();
      var2.modelState.animationPos = 0.0F;
      var2.modelState.yRot = var1.getYRot(var3);
      var2.modelState.xRot = var1.getXRot(var3);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
