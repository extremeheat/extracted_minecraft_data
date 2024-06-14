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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.WitherSkull;

public class WitherSkullRenderer extends EntityRenderer<WitherSkull> {
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

   public void render(WitherSkull var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      var4.scale(-1.0F, -1.0F, 1.0F);
      float var7 = Mth.rotLerp(var3, var1.yRotO, var1.getYRot());
      float var8 = Mth.lerp(var3, var1.xRotO, var1.getXRot());
      VertexConsumer var9 = var5.getBuffer(this.model.renderType(this.getTextureLocation(var1)));
      this.model.setupAnim(0.0F, var7, var8);
      this.model.renderToBuffer(var4, var9, var6, OverlayTexture.NO_OVERLAY);
      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public ResourceLocation getTextureLocation(WitherSkull var1) {
      return var1.isDangerous() ? WITHER_INVULNERABLE_LOCATION : WITHER_LOCATION;
   }
}
