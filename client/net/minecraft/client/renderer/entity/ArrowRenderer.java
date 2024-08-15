package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArrowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.projectile.AbstractArrow;

public abstract class ArrowRenderer<T extends AbstractArrow, S extends ArrowRenderState> extends EntityRenderer<T, S> {
   private final ArrowModel model;

   public ArrowRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new ArrowModel(var1.bakeLayer(ModelLayers.ARROW));
   }

   public void render(S var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      var2.mulPose(Axis.YP.rotationDegrees(var1.yRot - 90.0F));
      var2.mulPose(Axis.ZP.rotationDegrees(var1.xRot));
      VertexConsumer var5 = var3.getBuffer(RenderType.entityCutout(this.getTextureLocation((S)var1)));
      this.model.setupAnim(var1);
      this.model.renderToBuffer(var2, var5, var4, OverlayTexture.NO_OVERLAY);
      var2.popPose();
      super.render((S)var1, var2, var3, var4);
   }

   public void extractRenderState(T var1, S var2, float var3) {
      super.extractRenderState((T)var1, (S)var2, var3);
      var2.xRot = var1.getXRot(var3);
      var2.yRot = var1.getYRot(var3);
      var2.shake = (float)var1.shakeTime - var3;
   }
}
