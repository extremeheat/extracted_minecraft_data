package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.item.ItemStack;

public class PandaHoldsItemLayer extends RenderLayer<Panda, PandaModel<Panda>> {
   public PandaHoldsItemLayer(RenderLayerParent<Panda, PandaModel<Panda>> var1) {
      super(var1);
   }

   public void render(Panda var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      ItemStack var9 = var1.getItemBySlot(EquipmentSlot.MAINHAND);
      if (var1.isSitting() && !var9.isEmpty() && !var1.isScared()) {
         float var10 = -0.6F;
         float var11 = 1.4F;
         if (var1.isEating()) {
            var10 -= 0.2F * Mth.sin(var5 * 0.6F) + 0.2F;
            var11 -= 0.09F * Mth.sin(var5 * 0.6F);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.1F, var11, var10);
         Minecraft.getInstance().getItemRenderer().renderWithMobState(var9, var1, ItemTransforms.TransformType.GROUND, false);
         GlStateManager.popMatrix();
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
