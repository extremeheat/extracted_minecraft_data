package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ItemInHandLayer<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends RenderLayer<T, M> {
   private final ItemInHandRenderer itemInHandRenderer;

   public ItemInHandLayer(RenderLayerParent<T, M> var1, ItemInHandRenderer var2) {
      super(var1);
      this.itemInHandRenderer = var2;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      boolean var11 = var4.getMainArm() == HumanoidArm.RIGHT;
      ItemStack var12 = var11 ? var4.getOffhandItem() : var4.getMainHandItem();
      ItemStack var13 = var11 ? var4.getMainHandItem() : var4.getOffhandItem();
      if (!var12.isEmpty() || !var13.isEmpty()) {
         var1.pushPose();
         if (this.getParentModel().young) {
            float var14 = 0.5F;
            var1.translate(0.0F, 0.75F, 0.0F);
            var1.scale(0.5F, 0.5F, 0.5F);
         }

         this.renderArmWithItem(var4, var13, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, var1, var2, var3);
         this.renderArmWithItem(var4, var12, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, var1, var2, var3);
         var1.popPose();
      }
   }

   protected void renderArmWithItem(LivingEntity var1, ItemStack var2, ItemDisplayContext var3, HumanoidArm var4, PoseStack var5, MultiBufferSource var6, int var7) {
      if (!var2.isEmpty()) {
         var5.pushPose();
         ((ArmedModel)this.getParentModel()).translateToHand(var4, var5);
         var5.mulPose(Axis.XP.rotationDegrees(-90.0F));
         var5.mulPose(Axis.YP.rotationDegrees(180.0F));
         boolean var8 = var4 == HumanoidArm.LEFT;
         var5.translate((float)(var8 ? -1 : 1) / 16.0F, 0.125F, -0.625F);
         this.itemInHandRenderer.renderItem(var1, var2, var3, var8, var5, var6, var7);
         var5.popPose();
      }
   }
}
