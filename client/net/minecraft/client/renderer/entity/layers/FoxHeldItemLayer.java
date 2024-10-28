package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class FoxHeldItemLayer extends RenderLayer<Fox, FoxModel<Fox>> {
   private final ItemInHandRenderer itemInHandRenderer;

   public FoxHeldItemLayer(RenderLayerParent<Fox, FoxModel<Fox>> var1, ItemInHandRenderer var2) {
      super(var1);
      this.itemInHandRenderer = var2;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Fox var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      boolean var11 = var4.isSleeping();
      boolean var12 = var4.isBaby();
      var1.pushPose();
      float var13;
      if (var12) {
         var13 = 0.75F;
         var1.scale(0.75F, 0.75F, 0.75F);
         var1.translate(0.0F, 0.5F, 0.209375F);
      }

      var1.translate(((FoxModel)this.getParentModel()).head.x / 16.0F, ((FoxModel)this.getParentModel()).head.y / 16.0F, ((FoxModel)this.getParentModel()).head.z / 16.0F);
      var13 = var4.getHeadRollAngle(var7);
      var1.mulPose(Axis.ZP.rotation(var13));
      var1.mulPose(Axis.YP.rotationDegrees(var9));
      var1.mulPose(Axis.XP.rotationDegrees(var10));
      if (var4.isBaby()) {
         if (var11) {
            var1.translate(0.4F, 0.26F, 0.15F);
         } else {
            var1.translate(0.06F, 0.26F, -0.5F);
         }
      } else if (var11) {
         var1.translate(0.46F, 0.26F, 0.22F);
      } else {
         var1.translate(0.06F, 0.27F, -0.5F);
      }

      var1.mulPose(Axis.XP.rotationDegrees(90.0F));
      if (var11) {
         var1.mulPose(Axis.ZP.rotationDegrees(90.0F));
      }

      ItemStack var14 = var4.getItemBySlot(EquipmentSlot.MAINHAND);
      this.itemInHandRenderer.renderItem(var4, var14, ItemDisplayContext.GROUND, false, var1, var2, var3);
      var1.popPose();
   }
}
