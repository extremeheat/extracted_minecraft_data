package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.LeashKnotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;

public class LeashKnotRenderer extends EntityRenderer<LeashFenceKnotEntity, EntityRenderState> {
   private static final ResourceLocation KNOT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/lead_knot.png");
   private final LeashKnotModel model;

   public LeashKnotRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new LeashKnotModel(var1.bakeLayer(ModelLayers.LEASH_KNOT));
   }

   @Override
   public void render(EntityRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      var2.scale(-1.0F, -1.0F, 1.0F);
      this.model.setupAnim(var1);
      VertexConsumer var5 = var3.getBuffer(this.model.renderType(KNOT_LOCATION));
      this.model.renderToBuffer(var2, var5, var4, OverlayTexture.NO_OVERLAY);
      var2.popPose();
      super.render(var1, var2, var3, var4);
   }

   @Override
   public ResourceLocation getTextureLocation(EntityRenderState var1) {
      return KNOT_LOCATION;
   }

   @Override
   public EntityRenderState createRenderState() {
      return new EntityRenderState();
   }
}
