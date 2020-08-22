package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CrossedArmsItemLayer extends RenderLayer {
   public CrossedArmsItemLayer(RenderLayerParent var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, LivingEntity var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      var1.pushPose();
      var1.translate(0.0D, 0.4000000059604645D, -0.4000000059604645D);
      var1.mulPose(Vector3f.XP.rotationDegrees(180.0F));
      ItemStack var11 = var4.getItemBySlot(EquipmentSlot.MAINHAND);
      Minecraft.getInstance().getItemInHandRenderer().renderItem(var4, var11, ItemTransforms.TransformType.GROUND, false, var1, var2, var3);
      var1.popPose();
   }
}
