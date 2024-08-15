package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.WitherSkullRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.WitherSkull;

public class WitherSkullRenderer extends EntityRenderer<WitherSkull, WitherSkullRenderState> {
   private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation WITHER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/wither/wither.png");
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

   public void render(WitherSkullRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      var2.scale(-1.0F, -1.0F, 1.0F);
      VertexConsumer var5 = var3.getBuffer(this.model.renderType(this.getTextureLocation(var1)));
      this.model.setupAnim(0.0F, var1.yRot, var1.xRot);
      this.model.renderToBuffer(var2, var5, var4, OverlayTexture.NO_OVERLAY);
      var2.popPose();
      super.render(var1, var2, var3, var4);
   }

   public ResourceLocation getTextureLocation(WitherSkullRenderState var1) {
      return var1.isDangerous ? WITHER_INVULNERABLE_LOCATION : WITHER_LOCATION;
   }

   public WitherSkullRenderState createRenderState() {
      return new WitherSkullRenderState();
   }

   public void extractRenderState(WitherSkull var1, WitherSkullRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isDangerous = var1.isDangerous();
      var2.yRot = var1.getYRot(var3);
      var2.xRot = var1.getXRot(var3);
   }
}
