package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;

public class LeashKnotRenderer extends EntityRenderer<LeashFenceKnotEntity> {
   private static final ResourceLocation KNOT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/lead_knot.png");
   private final LeashKnotModel<LeashFenceKnotEntity> model;

   public LeashKnotRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new LeashKnotModel(var1.bakeLayer(ModelLayers.LEASH_KNOT));
   }

   public void render(LeashFenceKnotEntity var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      var4.scale(-1.0F, -1.0F, 1.0F);
      this.model.setupAnim(var1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      VertexConsumer var7 = var5.getBuffer(this.model.renderType(KNOT_LOCATION));
      this.model.renderToBuffer(var4, var7, var6, OverlayTexture.NO_OVERLAY);
      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public ResourceLocation getTextureLocation(LeashFenceKnotEntity var1) {
      return KNOT_LOCATION;
   }
}
