package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;

public class CarriedBlockLayer extends RenderLayer<EnderMan, EndermanModel<EnderMan>> {
   public CarriedBlockLayer(RenderLayerParent<EnderMan, EndermanModel<EnderMan>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, EnderMan var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      BlockState var11 = var4.getCarriedBlock();
      if (var11 != null) {
         var1.pushPose();
         var1.translate(0.0D, 0.6875D, -0.75D);
         var1.mulPose(Vector3f.XP.rotationDegrees(20.0F));
         var1.mulPose(Vector3f.YP.rotationDegrees(45.0F));
         var1.translate(0.25D, 0.1875D, 0.25D);
         float var12 = 0.5F;
         var1.scale(-0.5F, -0.5F, 0.5F);
         var1.mulPose(Vector3f.YP.rotationDegrees(90.0F));
         Minecraft.getInstance().getBlockRenderer().renderSingleBlock(var11, var1, var2, var3, OverlayTexture.NO_OVERLAY);
         var1.popPose();
      }
   }
}
