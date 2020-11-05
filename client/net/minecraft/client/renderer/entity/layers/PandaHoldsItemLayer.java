package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.renderer.MultiBufferSource;
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

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Panda var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      ItemStack var11 = var4.getItemBySlot(EquipmentSlot.MAINHAND);
      if (var4.isSitting() && !var4.isScared()) {
         float var12 = -0.6F;
         float var13 = 1.4F;
         if (var4.isEating()) {
            var12 -= 0.2F * Mth.sin(var8 * 0.6F) + 0.2F;
            var13 -= 0.09F * Mth.sin(var8 * 0.6F);
         }

         var1.pushPose();
         var1.translate(0.10000000149011612D, (double)var13, (double)var12);
         Minecraft.getInstance().getItemInHandRenderer().renderItem(var4, var11, ItemTransforms.TransformType.GROUND, false, var1, var2, var3);
         var1.popPose();
      }
   }
}
