package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Quaternionfc;

public class ItemInHandLayer<S extends ArmedEntityRenderState, M extends EntityModel<S> & ArmedModel> extends RenderLayer<S, M> {
   public ItemInHandLayer(RenderLayerParent<S, M> var1) {
      super(var1);
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, S var4, float var5, float var6) {
      this.submitArmWithItem(var4, var4.rightHandItem, HumanoidArm.RIGHT, var1, var2, var3);
      this.submitArmWithItem(var4, var4.leftHandItem, HumanoidArm.LEFT, var1, var2, var3);
   }

   protected void submitArmWithItem(S var1, ItemStackRenderState var2, HumanoidArm var3, PoseStack var4, SubmitNodeCollector var5, int var6) {
      if (!var2.isEmpty()) {
         var4.pushPose();
         ((ArmedModel)this.getParentModel()).translateToHand(var1, var3, var4);
         var4.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-90.0F));
         var4.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
         boolean var7 = var3 == HumanoidArm.LEFT;
         var4.translate((float)(var7 ? -1 : 1) / 16.0F, 0.125F, -0.625F);
         var2.submit(var4, var5, var6, OverlayTexture.NO_OVERLAY, var1.outlineColor);
         var4.popPose();
      }
   }
}
