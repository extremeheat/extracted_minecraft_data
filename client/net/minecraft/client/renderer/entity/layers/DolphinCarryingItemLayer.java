package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.item.ItemStack;

public class DolphinCarryingItemLayer extends RenderLayer<Dolphin, DolphinModel<Dolphin>> {
   public DolphinCarryingItemLayer(RenderLayerParent<Dolphin, DolphinModel<Dolphin>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Dolphin var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      boolean var11 = var4.getMainArm() == HumanoidArm.RIGHT;
      var1.pushPose();
      float var12 = 1.0F;
      float var13 = -1.0F;
      float var14 = Mth.abs(var4.getXRot()) / 60.0F;
      if (var4.getXRot() < 0.0F) {
         var1.translate(0.0D, (double)(1.0F - var14 * 0.5F), (double)(-1.0F + var14 * 0.5F));
      } else {
         var1.translate(0.0D, (double)(1.0F + var14 * 0.8F), (double)(-1.0F + var14 * 0.2F));
      }

      ItemStack var15 = var11 ? var4.getMainHandItem() : var4.getOffhandItem();
      Minecraft.getInstance().getItemInHandRenderer().renderItem(var4, var15, ItemTransforms.TransformType.GROUND, false, var1, var2, var3);
      var1.popPose();
   }
}
