package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.parrot.Parrot;

public class ParrotOnShoulderLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
   private final ParrotModel model;

   public ParrotOnShoulderLayer(RenderLayerParent<AvatarRenderState, PlayerModel> var1, EntityModelSet var2) {
      super(var1);
      this.model = new ParrotModel(var2.bakeLayer(ModelLayers.PARROT));
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, AvatarRenderState var4, float var5, float var6) {
      Parrot.Variant var7 = var4.parrotOnLeftShoulder;
      if (var7 != null) {
         this.submitOnShoulder(var1, var2, var3, var4, var7, var5, var6, true);
      }

      Parrot.Variant var8 = var4.parrotOnRightShoulder;
      if (var8 != null) {
         this.submitOnShoulder(var1, var2, var3, var4, var8, var5, var6, false);
      }

   }

   private void submitOnShoulder(PoseStack var1, SubmitNodeCollector var2, int var3, AvatarRenderState var4, Parrot.Variant var5, float var6, float var7, boolean var8) {
      var1.pushPose();
      var1.translate(var8 ? 0.4F : -0.4F, var4.isCrouching ? -1.3F : -1.5F, 0.0F);
      ParrotRenderState var9 = new ParrotRenderState();
      var9.pose = ParrotModel.Pose.ON_SHOULDER;
      var9.ageInTicks = var4.ageInTicks;
      var9.walkAnimationPos = var4.walkAnimationPos;
      var9.walkAnimationSpeed = var4.walkAnimationSpeed;
      var9.yRot = var6;
      var9.xRot = var7;
      var2.submitModel(this.model, var9, var1, this.model.renderType(ParrotRenderer.getVariantTexture(var5)), var3, OverlayTexture.NO_OVERLAY, var4.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      var1.popPose();
   }
}
