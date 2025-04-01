package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.WolfRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Quaternionfc;

public class WolfHeldItemLayer extends RenderLayer<WolfRenderState, WolfModel> {
   public WolfHeldItemLayer(RenderLayerParent<WolfRenderState, WolfModel> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, WolfRenderState var4, float var5, float var6) {
      ItemStackRenderState var7 = var4.heldItem;
      if (!var7.isEmpty()) {
         boolean var8 = var4.isBaby;
         var1.pushPose();
         var1.translate(((WolfModel)this.getParentModel()).head.x / 16.0F, ((WolfModel)this.getParentModel()).head.y / 16.0F, ((WolfModel)this.getParentModel()).head.z / 16.0F);
         if (var8) {
            float var9 = 0.75F;
            var1.scale(0.75F, 0.75F, 0.75F);
         }

         var1.mulPose((Quaternionfc)Axis.ZP.rotation(var4.headRollAngle));
         var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var5));
         var1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(var6));
         if (var4.isBaby) {
            var1.translate(0.27F, 0.19F, -0.45F);
         } else {
            var1.translate(0.22F, 0.12F, -0.35F);
         }

         var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(30.0F));
         var1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90.0F));
         var7.render(var1, var2, var3, OverlayTexture.NO_OVERLAY);
         var1.popPose();
      }
   }
}
