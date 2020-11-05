package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.block.Blocks;

public class IronGolemFlowerLayer extends RenderLayer<IronGolem, IronGolemModel<IronGolem>> {
   public IronGolemFlowerLayer(RenderLayerParent<IronGolem, IronGolemModel<IronGolem>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, IronGolem var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (var4.getOfferFlowerTick() != 0) {
         var1.pushPose();
         ModelPart var11 = ((IronGolemModel)this.getParentModel()).getFlowerHoldingArm();
         var11.translateAndRotate(var1);
         var1.translate(-1.1875D, 1.0625D, -0.9375D);
         var1.translate(0.5D, 0.5D, 0.5D);
         float var12 = 0.5F;
         var1.scale(0.5F, 0.5F, 0.5F);
         var1.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
         var1.translate(-0.5D, -0.5D, -0.5D);
         Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.POPPY.defaultBlockState(), var1, var2, var3, OverlayTexture.NO_OVERLAY);
         var1.popPose();
      }
   }
}
