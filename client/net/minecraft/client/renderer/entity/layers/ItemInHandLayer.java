package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;

public class ItemInHandLayer<S extends ArmedEntityRenderState, M extends EntityModel<S> & ArmedModel> extends RenderLayer<S, M> {
   public ItemInHandLayer(RenderLayerParent<S, M> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      this.renderArmWithItem(var4, var4.rightHandItem, HumanoidArm.RIGHT, var1, var2, var3);
      this.renderArmWithItem(var4, var4.leftHandItem, HumanoidArm.LEFT, var1, var2, var3);
   }

   protected void renderArmWithItem(S var1, ItemStackRenderState var2, HumanoidArm var3, PoseStack var4, MultiBufferSource var5, int var6) {
      if (!var2.isEmpty()) {
         var4.pushPose();
         ((ArmedModel)this.getParentModel()).translateToHand(var3, var4);
         var4.mulPose(Axis.XP.rotationDegrees(-90.0F));
         var4.mulPose(Axis.YP.rotationDegrees(180.0F));
         boolean var7 = var3 == HumanoidArm.LEFT;
         var4.translate((float)(var7 ? -1 : 1) / 16.0F, 0.125F, -0.625F);
         var2.render(var4, var5, var6, OverlayTexture.NO_OVERLAY);
         var4.popPose();
      }
   }
}
