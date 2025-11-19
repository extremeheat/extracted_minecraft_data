package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.monster.enderman.EndermanModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EndermanRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionfc;

public class CarriedBlockLayer extends RenderLayer<EndermanRenderState, EndermanModel<EndermanRenderState>> {
   public CarriedBlockLayer(RenderLayerParent<EndermanRenderState, EndermanModel<EndermanRenderState>> var1) {
      super(var1);
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, EndermanRenderState var4, float var5, float var6) {
      BlockState var7 = var4.carriedBlock;
      if (var7 != null) {
         var1.pushPose();
         var1.translate(0.0F, 0.6875F, -0.75F);
         var1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(20.0F));
         var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(45.0F));
         var1.translate(0.25F, 0.1875F, 0.25F);
         float var8 = 0.5F;
         var1.scale(-0.5F, -0.5F, 0.5F);
         var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(90.0F));
         var2.submitBlock(var1, var7, var3, OverlayTexture.NO_OVERLAY, var4.outlineColor);
         var1.popPose();
      }
   }
}
