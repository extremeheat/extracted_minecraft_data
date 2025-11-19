package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.animal.golem.SnowGolemModel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionfc;

public class SnowGolemHeadLayer extends RenderLayer<SnowGolemRenderState, SnowGolemModel> {
   private final BlockRenderDispatcher blockRenderer;

   public SnowGolemHeadLayer(RenderLayerParent<SnowGolemRenderState, SnowGolemModel> var1, BlockRenderDispatcher var2) {
      super(var1);
      this.blockRenderer = var2;
   }

   public void submit(PoseStack var1, SubmitNodeCollector var2, int var3, SnowGolemRenderState var4, float var5, float var6) {
      if (var4.hasPumpkin) {
         if (!var4.isInvisible || var4.appearsGlowing()) {
            var1.pushPose();
            ((SnowGolemModel)this.getParentModel()).getHead().translateAndRotate(var1);
            float var7 = 0.625F;
            var1.translate(0.0F, -0.34375F, 0.0F);
            var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
            var1.scale(0.625F, -0.625F, -0.625F);
            BlockState var8 = Blocks.CARVED_PUMPKIN.defaultBlockState();
            BlockStateModel var9 = this.blockRenderer.getBlockModel(var8);
            int var10 = LivingEntityRenderer.getOverlayCoords(var4, 0.0F);
            var1.translate(-0.5F, -0.5F, -0.5F);
            RenderType var11 = var4.appearsGlowing() && var4.isInvisible ? RenderTypes.outline(TextureAtlas.LOCATION_BLOCKS) : ItemBlockRenderTypes.getRenderType(var8);
            var2.submitBlockModel(var1, var11, var9, 0.0F, 0.0F, 0.0F, var3, var10, var4.outlineColor);
            var1.popPose();
         }
      }
   }
}
