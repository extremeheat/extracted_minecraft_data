package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ArmedModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ItemInHandLayer<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends RenderLayer<T, M> {
   public ItemInHandLayer(RenderLayerParent<T, M> var1) {
      super(var1);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      boolean var9 = var1.getMainArm() == HumanoidArm.RIGHT;
      ItemStack var10 = var9 ? var1.getOffhandItem() : var1.getMainHandItem();
      ItemStack var11 = var9 ? var1.getMainHandItem() : var1.getOffhandItem();
      if (!var10.isEmpty() || !var11.isEmpty()) {
         GlStateManager.pushMatrix();
         if (this.getParentModel().young) {
            float var12 = 0.5F;
            GlStateManager.translatef(0.0F, 0.75F, 0.0F);
            GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         }

         this.renderArmWithItem(var1, var11, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT);
         this.renderArmWithItem(var1, var10, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT);
         GlStateManager.popMatrix();
      }
   }

   private void renderArmWithItem(LivingEntity var1, ItemStack var2, ItemTransforms.TransformType var3, HumanoidArm var4) {
      if (!var2.isEmpty()) {
         GlStateManager.pushMatrix();
         this.translateToHand(var4);
         if (var1.isVisuallySneaking()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
         boolean var5 = var4 == HumanoidArm.LEFT;
         GlStateManager.translatef((float)(var5 ? -1 : 1) / 16.0F, 0.125F, -0.625F);
         Minecraft.getInstance().getItemInHandRenderer().renderItem(var1, var2, var3, var5);
         GlStateManager.popMatrix();
      }
   }

   protected void translateToHand(HumanoidArm var1) {
      ((ArmedModel)this.getParentModel()).translateToHand(0.0625F, var1);
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
