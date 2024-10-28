package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.block.state.BlockState;

public class MushroomCowMushroomLayer<T extends MushroomCow> extends RenderLayer<T, CowModel<T>> {
   private final BlockRenderDispatcher blockRenderer;

   public MushroomCowMushroomLayer(RenderLayerParent<T, CowModel<T>> var1, BlockRenderDispatcher var2) {
      super(var1);
      this.blockRenderer = var2;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (!var4.isBaby()) {
         Minecraft var11 = Minecraft.getInstance();
         boolean var12 = var11.shouldEntityAppearGlowing(var4) && var4.isInvisible();
         if (!var4.isInvisible() || var12) {
            BlockState var13 = var4.getVariant().getBlockState();
            int var14 = LivingEntityRenderer.getOverlayCoords(var4, 0.0F);
            BakedModel var15 = this.blockRenderer.getBlockModel(var13);
            var1.pushPose();
            var1.translate(0.2F, -0.35F, 0.5F);
            var1.mulPose(Axis.YP.rotationDegrees(-48.0F));
            var1.scale(-1.0F, -1.0F, 1.0F);
            var1.translate(-0.5F, -0.5F, -0.5F);
            this.renderMushroomBlock(var1, var2, var3, var12, var13, var14, var15);
            var1.popPose();
            var1.pushPose();
            var1.translate(0.2F, -0.35F, 0.5F);
            var1.mulPose(Axis.YP.rotationDegrees(42.0F));
            var1.translate(0.1F, 0.0F, -0.6F);
            var1.mulPose(Axis.YP.rotationDegrees(-48.0F));
            var1.scale(-1.0F, -1.0F, 1.0F);
            var1.translate(-0.5F, -0.5F, -0.5F);
            this.renderMushroomBlock(var1, var2, var3, var12, var13, var14, var15);
            var1.popPose();
            var1.pushPose();
            ((CowModel)this.getParentModel()).getHead().translateAndRotate(var1);
            var1.translate(0.0F, -0.7F, -0.2F);
            var1.mulPose(Axis.YP.rotationDegrees(-78.0F));
            var1.scale(-1.0F, -1.0F, 1.0F);
            var1.translate(-0.5F, -0.5F, -0.5F);
            this.renderMushroomBlock(var1, var2, var3, var12, var13, var14, var15);
            var1.popPose();
         }
      }
   }

   private void renderMushroomBlock(PoseStack var1, MultiBufferSource var2, int var3, boolean var4, BlockState var5, int var6, BakedModel var7) {
      if (var4) {
         this.blockRenderer.getModelRenderer().renderModel(var1.last(), var2.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), var5, var7, 0.0F, 0.0F, 0.0F, var3, var6);
      } else {
         this.blockRenderer.renderSingleBlock(var5, var1, var2, var3, var6);
      }

   }
}
