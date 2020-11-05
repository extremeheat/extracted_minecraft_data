package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class SnowGolemHeadLayer extends RenderLayer<SnowGolem, SnowGolemModel<SnowGolem>> {
   public SnowGolemHeadLayer(RenderLayerParent<SnowGolem, SnowGolemModel<SnowGolem>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, SnowGolem var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (!var4.isInvisible() && var4.hasPumpkin()) {
         var1.pushPose();
         ((SnowGolemModel)this.getParentModel()).getHead().translateAndRotate(var1);
         float var11 = 0.625F;
         var1.translate(0.0D, -0.34375D, 0.0D);
         var1.mulPose(Vector3f.YP.rotationDegrees(180.0F));
         var1.scale(0.625F, -0.625F, -0.625F);
         ItemStack var12 = new ItemStack(Blocks.CARVED_PUMPKIN);
         Minecraft.getInstance().getItemRenderer().renderStatic(var4, var12, ItemTransforms.TransformType.HEAD, false, var1, var2, var4.level, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F));
         var1.popPose();
      }
   }
}
