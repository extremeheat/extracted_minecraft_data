package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.projectile.ArrowModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.joml.Quaternionfc;

public abstract class ArrowRenderer<T extends AbstractArrow, S extends ArrowRenderState> extends EntityRenderer<T, S> {
   private final ArrowModel model;

   public ArrowRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new ArrowModel(var1.bakeLayer(ModelLayers.ARROW));
   }

   public void submit(S var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var2.pushPose();
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var1.yRot - 90.0F));
      var2.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(var1.xRot));
      var3.submitModel(this.model, var1, var2, RenderTypes.entityCutout(this.getTextureLocation(var1)), var1.lightCoords, OverlayTexture.NO_OVERLAY, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      var2.popPose();
      super.submit(var1, var2, var3, var4);
   }

   protected abstract Identifier getTextureLocation(S var1);

   public void extractRenderState(T var1, S var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.xRot = var1.getXRot(var3);
      var2.yRot = var1.getYRot(var3);
      var2.shake = (float)var1.shakeTime - var3;
   }
}
