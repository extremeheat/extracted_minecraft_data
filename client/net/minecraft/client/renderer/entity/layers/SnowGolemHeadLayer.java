package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class SnowGolemHeadLayer extends RenderLayer<LivingEntityRenderState, SnowGolemModel> {
   private final BlockRenderDispatcher blockRenderer;
   private final ItemRenderer itemRenderer;

   public SnowGolemHeadLayer(RenderLayerParent<LivingEntityRenderState, SnowGolemModel> var1, BlockRenderDispatcher var2, ItemRenderer var3) {
      super(var1);
      this.blockRenderer = var2;
      this.itemRenderer = var3;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, LivingEntityRenderState var4, float var5, float var6) {
      BakedModel var7 = var4.headItemModel;
      if (var7 != null) {
         boolean var8 = var4.appearsGlowing && var4.isInvisible;
         if (!var4.isInvisible || var8) {
            label24: {
               var1.pushPose();
               ((SnowGolemModel)this.getParentModel()).getHead().translateAndRotate(var1);
               float var9 = 0.625F;
               var1.translate(0.0F, -0.34375F, 0.0F);
               var1.mulPose(Axis.YP.rotationDegrees(180.0F));
               var1.scale(0.625F, -0.625F, -0.625F);
               ItemStack var10 = var4.headItem;
               if (var8) {
                  Item var12 = var10.getItem();
                  if (var12 instanceof BlockItem) {
                     BlockItem var11 = (BlockItem)var12;
                     BlockState var15 = var11.getBlock().defaultBlockState();
                     BakedModel var13 = this.blockRenderer.getBlockModel(var15);
                     int var14 = LivingEntityRenderer.getOverlayCoords(var4, 0.0F);
                     var1.translate(-0.5F, -0.5F, -0.5F);
                     this.blockRenderer.getModelRenderer().renderModel(var1.last(), var2.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), var15, var13, 0.0F, 0.0F, 0.0F, var3, var14);
                     break label24;
                  }
               }

               this.itemRenderer.render(var10, ItemDisplayContext.HEAD, false, var1, var2, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), var7);
            }

            var1.popPose();
         }
      }
   }
}
