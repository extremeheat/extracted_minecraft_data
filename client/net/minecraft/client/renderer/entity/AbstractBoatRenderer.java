package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import org.joml.Quaternionf;

public abstract class AbstractBoatRenderer extends EntityRenderer<AbstractBoat, BoatRenderState> {
   public AbstractBoatRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.8F;
   }

   public void render(BoatRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      var2.translate(0.0F, 0.375F, 0.0F);
      var2.mulPose(Axis.YP.rotationDegrees(180.0F - var1.yRot));
      float var5 = var1.hurtTime;
      if (var5 > 0.0F) {
         var2.mulPose(Axis.XP.rotationDegrees(Mth.sin(var5) * var5 * var1.damageTime / 10.0F * (float)var1.hurtDir));
      }

      if (!Mth.equal(var1.bubbleAngle, 0.0F)) {
         var2.mulPose((new Quaternionf()).setAngleAxis(var1.bubbleAngle * 0.017453292F, 1.0F, 0.0F, 1.0F));
      }

      var2.scale(-1.0F, -1.0F, 1.0F);
      var2.mulPose(Axis.YP.rotationDegrees(90.0F));
      EntityModel var6 = this.model();
      var6.setupAnim(var1);
      VertexConsumer var7 = var3.getBuffer(this.renderType());
      var6.renderToBuffer(var2, var7, var4, OverlayTexture.NO_OVERLAY);
      this.renderTypeAdditions(var1, var2, var3, var4);
      var2.popPose();
      super.render(var1, var2, var3, var4);
   }

   protected void renderTypeAdditions(BoatRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
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
