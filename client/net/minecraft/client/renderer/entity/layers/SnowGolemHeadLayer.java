package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SnowGolemHeadLayer extends RenderLayer<SnowGolem, SnowGolemModel<SnowGolem>> {
   public SnowGolemHeadLayer(RenderLayerParent<SnowGolem, SnowGolemModel<SnowGolem>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, SnowGolem var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (var4.hasPumpkin()) {
         Minecraft var11 = Minecraft.getInstance();
         boolean var12 = var11.shouldEntityAppearGlowing(var4) && var4.isInvisible();
         if (!var4.isInvisible() || var12) {
            var1.pushPose();
            ((SnowGolemModel)this.getParentModel()).getHead().translateAndRotate(var1);
            float var13 = 0.625F;
            var1.translate(0.0D, -0.34375D, 0.0D);
            var1.mulPose(Vector3f.field_292.rotationDegrees(180.0F));
            var1.scale(0.625F, -0.625F, -0.625F);
            ItemStack var14 = new ItemStack(Blocks.CARVED_PUMPKIN);
            if (var12) {
               BlockState var15 = Blocks.CARVED_PUMPKIN.defaultBlockState();
               BlockRenderDispatcher var16 = var11.getBlockRenderer();
               BakedModel var17 = var16.getBlockModel(var15);
               int var18 = LivingEntityRenderer.getOverlayCoords(var4, 0.0F);
               var1.translate(-0.5D, -0.5D, -0.5D);
               var16.getModelRenderer().renderModel(var1.last(), var2.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), var15, var17, 0.0F, 0.0F, 0.0F, var3, var18);
            } else {
               var11.getItemRenderer().renderStatic(var4, var14, ItemTransforms.TransformType.HEAD, false, var1, var2, var4.level, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), var4.getId());
            }

            var1.popPose();
         }
      }
   }
}
