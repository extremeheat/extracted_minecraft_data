package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;

public class Deadmau5EarsLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
   public Deadmau5EarsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, AbstractClientPlayer var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if ("deadmau5".equals(var4.getName().getString()) && !var4.isInvisible()) {
         VertexConsumer var11 = var2.getBuffer(RenderType.entitySolid(var4.getSkin().texture()));
         int var12 = LivingEntityRenderer.getOverlayCoords(var4, 0.0F);

         for(int var13 = 0; var13 < 2; ++var13) {
            float var14 = Mth.lerp(var7, var4.yRotO, var4.getYRot()) - Mth.lerp(var7, var4.yBodyRotO, var4.yBodyRot);
            float var15 = Mth.lerp(var7, var4.xRotO, var4.getXRot());
            var1.pushPose();
            var1.mulPose(Axis.YP.rotationDegrees(var14));
            var1.mulPose(Axis.XP.rotationDegrees(var15));
            var1.translate(0.375F * (float)(var13 * 2 - 1), 0.0F, 0.0F);
            var1.translate(0.0F, -0.375F, 0.0F);
            var1.mulPose(Axis.XP.rotationDegrees(-var15));
            var1.mulPose(Axis.YP.rotationDegrees(-var14));
            float var16 = 1.3333334F;
            var1.scale(1.3333334F, 1.3333334F, 1.3333334F);
            ((PlayerModel)this.getParentModel()).renderEars(var1, var11, var3, var12);
            var1.popPose();
         }

      }
   }
}
