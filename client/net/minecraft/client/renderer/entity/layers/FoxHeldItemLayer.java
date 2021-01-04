package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemStack;

public class FoxHeldItemLayer extends RenderLayer<Fox, FoxModel<Fox>> {
   public FoxHeldItemLayer(RenderLayerParent<Fox, FoxModel<Fox>> var1) {
      super(var1);
   }

   public void render(Fox var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      ItemStack var9 = var1.getItemBySlot(EquipmentSlot.MAINHAND);
      if (!var9.isEmpty()) {
         boolean var10 = var1.isSleeping();
         boolean var11 = var1.isBaby();
         GlStateManager.pushMatrix();
         float var12;
         if (var11) {
            var12 = 0.75F;
            GlStateManager.scalef(0.75F, 0.75F, 0.75F);
            GlStateManager.translatef(0.0F, 8.0F * var8, 3.35F * var8);
         }

         GlStateManager.translatef(((FoxModel)this.getParentModel()).head.x / 16.0F, ((FoxModel)this.getParentModel()).head.y / 16.0F, ((FoxModel)this.getParentModel()).head.z / 16.0F);
         var12 = var1.getHeadRollAngle(var4) * 57.295776F;
         GlStateManager.rotatef(var12, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(var6, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(var7, 1.0F, 0.0F, 0.0F);
         if (var1.isBaby()) {
            if (var10) {
               GlStateManager.translatef(0.4F, 0.26F, 0.15F);
            } else {
               GlStateManager.translatef(0.06F, 0.26F, -0.5F);
            }
         } else if (var10) {
            GlStateManager.translatef(0.46F, 0.26F, 0.22F);
         } else {
            GlStateManager.translatef(0.06F, 0.27F, -0.5F);
         }

         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         if (var10) {
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
         }

         Minecraft.getInstance().getItemRenderer().renderWithMobState(var9, var1, ItemTransforms.TransformType.GROUND, false);
         GlStateManager.popMatrix();
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
