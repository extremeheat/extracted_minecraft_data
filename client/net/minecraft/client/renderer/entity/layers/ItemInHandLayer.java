package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Quaternionfc;

public class ItemInHandLayer<S extends ArmedEntityRenderState, M extends EntityModel<S> & ArmedModel> extends RenderLayer<S, M> {
   public ItemInHandLayer(RenderLayerParent<S, M> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      this.renderArmWithItem(var4, var4.rightHandItem, HumanoidArm.RIGHT, var1, var2, var3);
      this.renderArmWithItem(var4, var4.leftHandItem, HumanoidArm.LEFT, var1, var2, var3);
   }

   protected void renderArmWithItem(S var1, ItemStackRenderState var2, HumanoidArm var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      ((ArmedModel)this.getParentModel()).translateToHand(var3, var4);
      var4.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-90.0F));
      var4.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
      boolean var7 = var3 == HumanoidArm.LEFT;
      var4.translate((float)(var7 ? -1 : 1) / 16.0F, 0.125F, -0.625F);
      if (var1 instanceof PlayerRenderState var8) {
         if (var8.isCampfirePlayer) {
            renderFireHands(0.5F, var4, var5);
         }
      }

      if (!var2.isEmpty()) {
         var2.render(var4, var5, var6, OverlayTexture.NO_OVERLAY);
      }

      var4.popPose();
   }

   public static void renderFireHands(float var0, PoseStack var1, MultiBufferSource var2) {
      TextureAtlasSprite var3 = ModelBakery.FIRE_0.sprite();
      TextureAtlasSprite var4 = ModelBakery.FIRE_1.sprite();
      var1.pushPose();
      var1.scale(var0, var0, var0);
      var1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90.0F));
      var1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(180.0F));
      var1.translate(0.0, -0.2, 0.2);
      VertexConsumer var5 = var2.getBuffer(RenderType.veryBrightVeryHotFire(TextureAtlas.LOCATION_BLOCKS));
      PoseStack.Pose var6 = var1.last();
      float var7 = 0.5F;
      fireVertex(var6, var5, -var7, 0.0F, 0.0F, var3.getU0(), var3.getV1());
      fireVertex(var6, var5, var7, 0.0F, 0.0F, var3.getU1(), var3.getV1());
      fireVertex(var6, var5, var7, 1.4F, 0.0F, var3.getU1(), var3.getV0());
      fireVertex(var6, var5, -var7, 1.4F, 0.0F, var3.getU0(), var3.getV0());
      var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
      fireVertex(var6, var5, -var7, 0.0F, 0.0F, var4.getU0(), var4.getV1());
      fireVertex(var6, var5, var7, 0.0F, 0.0F, var4.getU1(), var4.getV1());
      fireVertex(var6, var5, var7, 1.4F, 0.0F, var4.getU1(), var4.getV0());
      fireVertex(var6, var5, -var7, 1.4F, 0.0F, var4.getU0(), var4.getV0());
      var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(90.0F));
      fireVertex(var6, var5, -var7, 0.0F, 0.0F, var3.getU0(), var3.getV1());
      fireVertex(var6, var5, var7, 0.0F, 0.0F, var3.getU1(), var3.getV1());
      fireVertex(var6, var5, var7, 1.4F, 0.0F, var3.getU1(), var3.getV0());
      fireVertex(var6, var5, -var7, 1.4F, 0.0F, var3.getU0(), var3.getV0());
      var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
      fireVertex(var6, var5, -var7, 0.0F, 0.0F, var4.getU0(), var4.getV1());
      fireVertex(var6, var5, var7, 0.0F, 0.0F, var4.getU1(), var4.getV1());
      fireVertex(var6, var5, var7, 1.4F, 0.0F, var4.getU1(), var4.getV0());
      fireVertex(var6, var5, -var7, 1.4F, 0.0F, var4.getU0(), var4.getV0());
      var1.popPose();
   }

   private static void fireVertex(PoseStack.Pose var0, VertexConsumer var1, float var2, float var3, float var4, float var5, float var6) {
      var1.addVertex(var0, var2, var3, var4).setColor(-1).setUv(var5, var6).setUv1(0, 10).setLight(240).setNormal(var0, 0.0F, 1.0F, 0.0F);
   }
}
