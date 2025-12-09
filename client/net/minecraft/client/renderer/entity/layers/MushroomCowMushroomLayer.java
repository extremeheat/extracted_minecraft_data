package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.animal.cow.CowModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.MushroomCowRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionfc;

public class MushroomCowMushroomLayer extends RenderLayer<MushroomCowRenderState, CowModel> {
   private final BlockRenderDispatcher blockRenderer;

   public MushroomCowMushroomLayer(RenderLayerParent<MushroomCowRenderState, CowModel> var1, BlockRenderDispatcher var2) {
      super(var1);
      this.blockRenderer = var2;
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, MushroomCowRenderState var4, float var5, float var6) {
      if (!var4.isBaby) {
         boolean var7 = var4.appearsGlowing() && var4.isInvisible;
         if (!var4.isInvisible || var7) {
            BlockState var8 = var4.variant.getBlockState();
            int var9 = LivingEntityRenderer.getOverlayCoords(var4, 0.0F);
            BlockStateModel var10 = this.blockRenderer.getBlockModel(var8);
            var1.pushPose();
            var1.translate(0.2F, -0.35F, 0.5F);
            var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-48.0F));
            var1.scale(-1.0F, -1.0F, 1.0F);
            var1.translate(-0.5F, -0.5F, -0.5F);
            this.submitMushroomBlock(var1, var2, var3, var7, var4.outlineColor, var8, var9, var10);
            var1.popPose();
            var1.pushPose();
            var1.translate(0.2F, -0.35F, 0.5F);
            var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(42.0F));
            var1.translate(0.1F, 0.0F, -0.6F);
            var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-48.0F));
            var1.scale(-1.0F, -1.0F, 1.0F);
            var1.translate(-0.5F, -0.5F, -0.5F);
            this.submitMushroomBlock(var1, var2, var3, var7, var4.outlineColor, var8, var9, var10);
            var1.popPose();
            var1.pushPose();
            ((CowModel)this.getParentModel()).getHead().translateAndRotate(var1);
            var1.translate(0.0F, -0.7F, -0.2F);
            var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-78.0F));
            var1.scale(-1.0F, -1.0F, 1.0F);
            var1.translate(-0.5F, -0.5F, -0.5F);
            this.submitMushroomBlock(var1, var2, var3, var7, var4.outlineColor, var8, var9, var10);
            var1.popPose();
         }
      }
   }

   private void submitMushroomBlock(PoseStack var1, SubmitNodeCollector var2, int var3, boolean var4, int var5, BlockState var6, int var7, BlockStateModel var8) {
      if (var4) {
         var2.submitBlockModel(var1, RenderTypes.outline(TextureAtlas.LOCATION_BLOCKS), var8, 0.0F, 0.0F, 0.0F, var3, var7, var5);
      } else {
         var2.submitBlock(var1, var6, var3, var7, var5);
      }

   }
}
