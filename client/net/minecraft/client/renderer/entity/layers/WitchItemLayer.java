package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.WitchRenderState;
import net.minecraft.world.item.Items;

public class WitchItemLayer extends CrossedArmsItemLayer<WitchRenderState, WitchModel> {
   public WitchItemLayer(RenderLayerParent<WitchRenderState, WitchModel> var1, ItemRenderer var2) {
      super(var1, var2);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, WitchRenderState var4, float var5, float var6) {
      var1.pushPose();
      if (var4.rightHandItem.is(Items.POTION)) {
         ((WitchModel)this.getParentModel()).root().translateAndRotate(var1);
         ((WitchModel)this.getParentModel()).getHead().translateAndRotate(var1);
         ((WitchModel)this.getParentModel()).getNose().translateAndRotate(var1);
         var1.translate(0.0625F, 0.25F, 0.0F);
         var1.mulPose(Axis.ZP.rotationDegrees(180.0F));
         var1.mulPose(Axis.XP.rotationDegrees(140.0F));
         var1.mulPose(Axis.ZP.rotationDegrees(10.0F));
         var1.translate(0.0F, -0.4F, 0.4F);
      }

      super.render(var1, var2, var3, (LivingEntityRenderState)var4, var5, var6);
      var1.popPose();
   }
}
