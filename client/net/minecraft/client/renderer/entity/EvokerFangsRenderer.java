package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EvokerFangsModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EvokerFangsRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.EvokerFangs;

public class EvokerFangsRenderer extends EntityRenderer<EvokerFangs, EvokerFangsRenderState> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/illager/evoker_fangs.png");
   private final EvokerFangsModel model;

   public EvokerFangsRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new EvokerFangsModel(var1.bakeLayer(ModelLayers.EVOKER_FANGS));
   }

   public void render(EvokerFangsRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      float var5 = var1.biteProgress;
      if (var5 != 0.0F) {
         var2.pushPose();
         var2.mulPose(Axis.YP.rotationDegrees(90.0F - var1.yRot));
         var2.scale(-1.0F, -1.0F, 1.0F);
         var2.translate(0.0F, -1.501F, 0.0F);
         this.model.setupAnim(var1);
         VertexConsumer var6 = var3.getBuffer(this.model.renderType(TEXTURE_LOCATION));
         this.model.renderToBuffer(var2, var6, var4, OverlayTexture.NO_OVERLAY);
         var2.popPose();
         super.render(var1, var2, var3, var4);
      }
   }

   public EvokerFangsRenderState createRenderState() {
      return new EvokerFangsRenderState();
   }

   public void extractRenderState(EvokerFangs var1, EvokerFangsRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.yRot = var1.getYRot();
      var2.biteProgress = var1.getAnimationProgress(var3);
   }
}
