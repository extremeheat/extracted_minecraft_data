package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotOnShoulderLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
   private final ParrotModel model;

   public ParrotOnShoulderLayer(RenderLayerParent<PlayerRenderState, PlayerModel> var1, EntityModelSet var2) {
      super(var1);
      this.model = new ParrotModel(var2.bakeLayer(ModelLayers.PARROT));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, PlayerRenderState var4, float var5, float var6) {
      Parrot.Variant var7 = var4.parrotOnLeftShoulder;
      if (var7 != null) {
         this.renderOnShoulder(var1, var2, var3, var4, var7, var5, var6, true);
      }

      Parrot.Variant var8 = var4.parrotOnRightShoulder;
      if (var8 != null) {
         this.renderOnShoulder(var1, var2, var3, var4, var8, var5, var6, false);
      }
   }

   private void renderOnShoulder(
      PoseStack var1, MultiBufferSource var2, int var3, PlayerRenderState var4, Parrot.Variant var5, float var6, float var7, boolean var8
   ) {
      var1.pushPose();
      var1.translate(var8 ? 0.4F : -0.4F, var4.isCrouching ? -1.3F : -1.5F, 0.0F);
      VertexConsumer var9 = var2.getBuffer(this.model.renderType(ParrotRenderer.getVariantTexture(var5)));
      this.model.renderOnShoulder(var1, var9, var3, OverlayTexture.NO_OVERLAY, var4.walkAnimationPos, var4.walkAnimationSpeed, var6, var7, var4.ageInTicks);
      var1.popPose();
   }
}
