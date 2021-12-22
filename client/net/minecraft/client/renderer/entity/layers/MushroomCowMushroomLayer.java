package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
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
   public MushroomCowMushroomLayer(RenderLayerParent<T, CowModel<T>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (!var4.isBaby()) {
         Minecraft var11 = Minecraft.getInstance();
         boolean var12 = var11.shouldEntityAppearGlowing(var4) && var4.isInvisible();
         if (!var4.isInvisible() || var12) {
            BlockRenderDispatcher var13 = var11.getBlockRenderer();
            BlockState var14 = var4.getMushroomType().getBlockState();
            int var15 = LivingEntityRenderer.getOverlayCoords(var4, 0.0F);
            BakedModel var16 = var13.getBlockModel(var14);
            var1.pushPose();
            var1.translate(0.20000000298023224D, -0.3499999940395355D, 0.5D);
            var1.mulPose(Vector3f.field_292.rotationDegrees(-48.0F));
            var1.scale(-1.0F, -1.0F, 1.0F);
            var1.translate(-0.5D, -0.5D, -0.5D);
            this.renderMushroomBlock(var1, var2, var3, var12, var13, var14, var15, var16);
            var1.popPose();
            var1.pushPose();
            var1.translate(0.20000000298023224D, -0.3499999940395355D, 0.5D);
            var1.mulPose(Vector3f.field_292.rotationDegrees(42.0F));
            var1.translate(0.10000000149011612D, 0.0D, -0.6000000238418579D);
            var1.mulPose(Vector3f.field_292.rotationDegrees(-48.0F));
            var1.scale(-1.0F, -1.0F, 1.0F);
            var1.translate(-0.5D, -0.5D, -0.5D);
            this.renderMushroomBlock(var1, var2, var3, var12, var13, var14, var15, var16);
            var1.popPose();
            var1.pushPose();
            ((CowModel)this.getParentModel()).getHead().translateAndRotate(var1);
            var1.translate(0.0D, -0.699999988079071D, -0.20000000298023224D);
            var1.mulPose(Vector3f.field_292.rotationDegrees(-78.0F));
            var1.scale(-1.0F, -1.0F, 1.0F);
            var1.translate(-0.5D, -0.5D, -0.5D);
            this.renderMushroomBlock(var1, var2, var3, var12, var13, var14, var15, var16);
            var1.popPose();
         }
      }
   }

   private void renderMushroomBlock(PoseStack var1, MultiBufferSource var2, int var3, boolean var4, BlockRenderDispatcher var5, BlockState var6, int var7, BakedModel var8) {
      if (var4) {
         var5.getModelRenderer().renderModel(var1.last(), var2.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), var6, var8, 0.0F, 0.0F, 0.0F, var3, var7);
      } else {
         var5.renderSingleBlock(var6, var1, var2, var3, var7);
      }

   }
}
