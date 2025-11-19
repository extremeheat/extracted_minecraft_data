package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.animal.golem.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import org.joml.Quaternionfc;

public class IronGolemFlowerLayer extends RenderLayer<IronGolemRenderState, IronGolemModel> {
   public IronGolemFlowerLayer(RenderLayerParent<IronGolemRenderState, IronGolemModel> var1) {
      super(var1);
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, IronGolemRenderState var4, float var5, float var6) {
      if (var4.offerFlowerTick != 0) {
         var1.pushPose();
         ModelPart var7 = ((IronGolemModel)this.getParentModel()).getFlowerHoldingArm();
         var7.translateAndRotate(var1);
         var1.translate(-1.1875F, 1.0625F, -0.9375F);
         var1.translate(0.5F, 0.5F, 0.5F);
         float var8 = 0.5F;
         var1.scale(0.5F, 0.5F, 0.5F);
         var1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-90.0F));
         var1.translate(-0.5F, -0.5F, -0.5F);
         var2.submitBlock(var1, Blocks.POPPY.defaultBlockState(), var3, OverlayTexture.NO_OVERLAY, var4.outlineColor);
         var1.popPose();
      }
   }
}
