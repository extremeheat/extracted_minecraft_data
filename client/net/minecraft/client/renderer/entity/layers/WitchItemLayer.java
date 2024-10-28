package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class WitchItemLayer<T extends LivingEntity> extends CrossedArmsItemLayer<T, WitchModel<T>> {
   public WitchItemLayer(RenderLayerParent<T, WitchModel<T>> var1, ItemInHandRenderer var2) {
      super(var1, var2);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      ItemStack var11 = var4.getMainHandItem();
      var1.pushPose();
      if (var11.is(Items.POTION)) {
         ((WitchModel)this.getParentModel()).getHead().translateAndRotate(var1);
         ((WitchModel)this.getParentModel()).getNose().translateAndRotate(var1);
         var1.translate(0.0625F, 0.25F, 0.0F);
         var1.mulPose(Axis.ZP.rotationDegrees(180.0F));
         var1.mulPose(Axis.XP.rotationDegrees(140.0F));
         var1.mulPose(Axis.ZP.rotationDegrees(10.0F));
         var1.translate(0.0F, -0.4F, 0.4F);
      }

      super.render(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      var1.popPose();
   }
}
