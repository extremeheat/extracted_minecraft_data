package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.block.state.BlockState;

public class MushroomCowMushroomLayer extends RenderLayer {
   public MushroomCowMushroomLayer(RenderLayerParent var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, MushroomCow var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (!var4.isBaby() && !var4.isInvisible()) {
         BlockRenderDispatcher var11 = Minecraft.getInstance().getBlockRenderer();
         BlockState var12 = var4.getMushroomType().getBlockState();
         int var13 = LivingEntityRenderer.getOverlayCoords(var4, 0.0F);
         var1.pushPose();
         var1.translate(0.20000000298023224D, -0.3499999940395355D, 0.5D);
         var1.mulPose(Vector3f.YP.rotationDegrees(-48.0F));
         var1.scale(-1.0F, -1.0F, 1.0F);
         var1.translate(-0.5D, -0.5D, -0.5D);
         var11.renderSingleBlock(var12, var1, var2, var3, var13);
         var1.popPose();
         var1.pushPose();
         var1.translate(0.20000000298023224D, -0.3499999940395355D, 0.5D);
         var1.mulPose(Vector3f.YP.rotationDegrees(42.0F));
         var1.translate(0.10000000149011612D, 0.0D, -0.6000000238418579D);
         var1.mulPose(Vector3f.YP.rotationDegrees(-48.0F));
         var1.scale(-1.0F, -1.0F, 1.0F);
         var1.translate(-0.5D, -0.5D, -0.5D);
         var11.renderSingleBlock(var12, var1, var2, var3, var13);
         var1.popPose();
         var1.pushPose();
         ((CowModel)this.getParentModel()).getHead().translateAndRotate(var1);
         var1.translate(0.0D, -0.699999988079071D, -0.20000000298023224D);
         var1.mulPose(Vector3f.YP.rotationDegrees(-78.0F));
         var1.scale(-1.0F, -1.0F, 1.0F);
         var1.translate(-0.5D, -0.5D, -0.5D);
         var11.renderSingleBlock(var12, var1, var2, var3, var13);
         var1.popPose();
      }
   }
}
