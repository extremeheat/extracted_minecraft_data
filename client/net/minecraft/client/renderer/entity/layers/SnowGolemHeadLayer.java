package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SnowGolemHeadLayer extends RenderLayer<SnowGolem, SnowGolemModel<SnowGolem>> {
   private final BlockRenderDispatcher blockRenderer;
   private final ItemRenderer itemRenderer;

   public SnowGolemHeadLayer(RenderLayerParent<SnowGolem, SnowGolemModel<SnowGolem>> var1, BlockRenderDispatcher var2, ItemRenderer var3) {
      super(var1);
      this.blockRenderer = var2;
      this.itemRenderer = var3;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, SnowGolem var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (var4.hasPumpkin()) {
         boolean var11 = Minecraft.getInstance().shouldEntityAppearGlowing(var4) && var4.isInvisible();
         if (!var4.isInvisible() || var11) {
            var1.pushPose();
            ((SnowGolemModel)this.getParentModel()).getHead().translateAndRotate(var1);
            float var12 = 0.625F;
            var1.translate(0.0F, -0.34375F, 0.0F);
            var1.mulPose(Axis.YP.rotationDegrees(180.0F));
            var1.scale(0.625F, -0.625F, -0.625F);
            ItemStack var13 = new ItemStack(Blocks.CARVED_PUMPKIN);
            if (var11) {
               BlockState var14 = Blocks.CARVED_PUMPKIN.defaultBlockState();
               BakedModel var15 = this.blockRenderer.getBlockModel(var14);
               int var16 = LivingEntityRenderer.getOverlayCoords(var4, 0.0F);
               var1.translate(-0.5F, -0.5F, -0.5F);
               this.blockRenderer.getModelRenderer().renderModel(var1.last(), var2.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), var14, var15, 0.0F, 0.0F, 0.0F, var3, var16);
            } else {
               this.itemRenderer.renderStatic(var4, var13, ItemDisplayContext.HEAD, false, var1, var2, var4.level(), var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), var4.getId());
            }

            var1.popPose();
         }
      }
   }
}
