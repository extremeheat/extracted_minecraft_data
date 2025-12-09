package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.monster.witch.WitchModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.WitchRenderState;
import org.joml.Quaternionfc;

public class WitchItemLayer extends CrossedArmsItemLayer<WitchRenderState, WitchModel> {
   public WitchItemLayer(RenderLayerParent<WitchRenderState, WitchModel> var1) {
      super(var1);
   }

   protected void applyTranslation(WitchRenderState var1, PoseStack var2) {
      if (var1.isHoldingPotion) {
         ((WitchModel)this.getParentModel()).root().translateAndRotate(var2);
         ((WitchModel)this.getParentModel()).translateToHead(var2);
         ((WitchModel)this.getParentModel()).getNose().translateAndRotate(var2);
         var2.translate(0.0625F, 0.25F, 0.0F);
         var2.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(180.0F));
         var2.mulPose((Quaternionfc)Axis.XP.rotationDegrees(140.0F));
         var2.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(10.0F));
         var2.mulPose((Quaternionfc)Axis.XP.rotationDegrees(180.0F));
      } else {
         super.applyTranslation(var1, var2);
      }
   }
}
