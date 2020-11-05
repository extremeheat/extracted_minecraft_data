package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemStack;

public class FoxHeldItemLayer extends RenderLayer<Fox, FoxModel<Fox>> {
   public FoxHeldItemLayer(RenderLayerParent<Fox, FoxModel<Fox>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Fox var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      boolean var11 = var4.isSleeping();
      boolean var12 = var4.isBaby();
      var1.pushPose();
      float var13;
      if (var12) {
         var13 = 0.75F;
         var1.scale(0.75F, 0.75F, 0.75F);
         var1.translate(0.0D, 0.5D, 0.20937499403953552D);
      }

      var1.translate((double)(((FoxModel)this.getParentModel()).head.x / 16.0F), (double)(((FoxModel)this.getParentModel()).head.y / 16.0F), (double)(((FoxModel)this.getParentModel()).head.z / 16.0F));
      var13 = var4.getHeadRollAngle(var7);
      var1.mulPose(Vector3f.ZP.rotation(var13));
      var1.mulPose(Vector3f.YP.rotationDegrees(var9));
      var1.mulPose(Vector3f.XP.rotationDegrees(var10));
      if (var4.isBaby()) {
         if (var11) {
            var1.translate(0.4000000059604645D, 0.25999999046325684D, 0.15000000596046448D);
         } else {
            var1.translate(0.05999999865889549D, 0.25999999046325684D, -0.5D);
         }
      } else if (var11) {
         var1.translate(0.46000000834465027D, 0.25999999046325684D, 0.2199999988079071D);
      } else {
         var1.translate(0.05999999865889549D, 0.27000001072883606D, -0.5D);
      }

      var1.mulPose(Vector3f.XP.rotationDegrees(90.0F));
      if (var11) {
         var1.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
      }

      ItemStack var14 = var4.getItemBySlot(EquipmentSlot.MAINHAND);
      Minecraft.getInstance().getItemInHandRenderer().renderItem(var4, var14, ItemTransforms.TransformType.GROUND, false, var1, var2, var3);
      var1.popPose();
   }
}
