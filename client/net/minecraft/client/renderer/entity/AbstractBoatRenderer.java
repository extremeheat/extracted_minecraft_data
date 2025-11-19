package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public abstract class AbstractBoatRenderer extends EntityRenderer<AbstractBoat, BoatRenderState> {
   public AbstractBoatRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.8F;
   }

   public void submit(BoatRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var2.pushPose();
      var2.translate(0.0F, 0.375F, 0.0F);
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F - var1.yRot));
      float var5 = var1.hurtTime;
      if (var5 > 0.0F) {
         var2.mulPose((Quaternionfc)Axis.XP.rotationDegrees(Mth.sin((double)var5) * var5 * var1.damageTime / 10.0F * (float)var1.hurtDir));
      }

      if (!var1.isUnderWater && !Mth.equal(var1.bubbleAngle, 0.0F)) {
         var2.mulPose((Quaternionfc)(new Quaternionf()).setAngleAxis(var1.bubbleAngle * 0.017453292F, 1.0F, 0.0F, 1.0F));
      }

      var2.scale(-1.0F, -1.0F, 1.0F);
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(90.0F));
      var3.submitModel(this.model(), var1, var2, this.renderType(), var1.lightCoords, OverlayTexture.NO_OVERLAY, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      this.submitTypeAdditions(var1, var2, var3, var1.lightCoords);
      var2.popPose();
      super.submit(var1, var2, var3, var4);
   }

   protected void submitTypeAdditions(BoatRenderState var1, PoseStack var2, SubmitNodeCollector var3, int var4) {
   }

   protected abstract EntityModel<BoatRenderState> model();

   protected abstract RenderType renderType();

   public BoatRenderState createRenderState() {
      return new BoatRenderState();
   }

   public void extractRenderState(AbstractBoat var1, BoatRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.yRot = var1.getYRot(var3);
      var2.hurtTime = (float)var1.getHurtTime() - var3;
      var2.hurtDir = var1.getHurtDir();
      var2.damageTime = Math.max(var1.getDamage() - var3, 0.0F);
      var2.bubbleAngle = var1.getBubbleAngle(var3);
      var2.isUnderWater = var1.isUnderWater();
      var2.rowingTimeLeft = var1.getRowingTime(0, var3);
      var2.rowingTimeRight = var1.getRowingTime(1, var3);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
