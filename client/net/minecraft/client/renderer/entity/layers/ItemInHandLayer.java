package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ItemInHandLayer<S extends LivingEntityRenderState, M extends EntityModel<S> & ArmedModel> extends RenderLayer<S, M> {
   private final ItemRenderer itemRenderer;

   public ItemInHandLayer(RenderLayerParent<S, M> var1, ItemRenderer var2) {
      super(var1);
      this.itemRenderer = var2;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      this.renderArmWithItem(
         (S)var4, var4.rightHandItemModel, var4.rightHandItem, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, var1, var2, var3
      );
      this.renderArmWithItem((S)var4, var4.leftHandItemModel, var4.leftHandItem, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, var1, var2, var3);
   }

   protected void renderArmWithItem(
      S var1, @Nullable BakedModel var2, ItemStack var3, ItemDisplayContext var4, HumanoidArm var5, PoseStack var6, MultiBufferSource var7, int var8
   ) {
      if (var2 != null && !var3.isEmpty()) {
         var6.pushPose();
         this.getParentModel().translateToHand(var5, var6);
         var6.mulPose(Axis.XP.rotationDegrees(-90.0F));
         var6.mulPose(Axis.YP.rotationDegrees(180.0F));
         boolean var9 = var5 == HumanoidArm.LEFT;
         var6.translate((float)(var9 ? -1 : 1) / 16.0F, 0.125F, -0.625F);
         this.itemRenderer.render(var3, var4, var9, var6, var7, var8, OverlayTexture.NO_OVERLAY, var2);
         var6.popPose();
      }
   }
}
