package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BlockRenderDispatcher implements ResourceManagerReloadListener {
   private final BlockModelShaper blockModelShaper;
   private final ModelBlockRenderer modelRenderer;
   private final BlockEntityWithoutLevelRenderer blockEntityRenderer;
   private final LiquidBlockRenderer liquidBlockRenderer;
   private final Random random = new Random();
   private final BlockColors blockColors;

   public BlockRenderDispatcher(BlockModelShaper var1, BlockEntityWithoutLevelRenderer var2, BlockColors var3) {
      super();
      this.blockModelShaper = var1;
      this.blockEntityRenderer = var2;
      this.blockColors = var3;
      this.modelRenderer = new ModelBlockRenderer(this.blockColors);
      this.liquidBlockRenderer = new LiquidBlockRenderer();
   }

   public BlockModelShaper getBlockModelShaper() {
      return this.blockModelShaper;
   }

   public void renderBreakingTexture(BlockState var1, BlockPos var2, BlockAndTintGetter var3, PoseStack var4, VertexConsumer var5) {
      if (var1.getRenderShape() == RenderShape.MODEL) {
         BakedModel var6 = this.blockModelShaper.getBlockModel(var1);
         long var7 = var1.getSeed(var2);
         this.modelRenderer.tesselateBlock(var3, var6, var1, var2, var4, var5, true, this.random, var7, OverlayTexture.NO_OVERLAY);
      }
   }

   public boolean renderBatched(BlockState var1, BlockPos var2, BlockAndTintGetter var3, PoseStack var4, VertexConsumer var5, boolean var6, Random var7) {
      try {
         RenderShape var8 = var1.getRenderShape();
         return var8 != RenderShape.MODEL ? false : this.modelRenderer.tesselateBlock(var3, this.getBlockModel(var1), var1, var2, var4, var5, var6, var7, var1.getSeed(var2), OverlayTexture.NO_OVERLAY);
      } catch (Throwable var11) {
         CrashReport var9 = CrashReport.forThrowable(var11, "Tesselating block in world");
         CrashReportCategory var10 = var9.addCategory("Block being tesselated");
         CrashReportCategory.populateBlockDetails(var10, var3, var2, var1);
         throw new ReportedException(var9);
      }
   }

   public boolean renderLiquid(BlockPos var1, BlockAndTintGetter var2, VertexConsumer var3, FluidState var4) {
      try {
         return this.liquidBlockRenderer.tesselate(var2, var1, var3, var4);
      } catch (Throwable var8) {
         CrashReport var6 = CrashReport.forThrowable(var8, "Tesselating liquid in world");
         CrashReportCategory var7 = var6.addCategory("Block being tesselated");
         CrashReportCategory.populateBlockDetails(var7, var2, var1, (BlockState)null);
         throw new ReportedException(var6);
      }
   }

   public ModelBlockRenderer getModelRenderer() {
      return this.modelRenderer;
   }

   public BakedModel getBlockModel(BlockState var1) {
      return this.blockModelShaper.getBlockModel(var1);
   }

   public void renderSingleBlock(BlockState var1, PoseStack var2, MultiBufferSource var3, int var4, int var5) {
      RenderShape var6 = var1.getRenderShape();
      if (var6 != RenderShape.INVISIBLE) {
         switch(var6) {
         case MODEL:
            BakedModel var7 = this.getBlockModel(var1);
            int var8 = this.blockColors.getColor(var1, (BlockAndTintGetter)null, (BlockPos)null, 0);
            float var9 = (float)(var8 >> 16 & 255) / 255.0F;
            float var10 = (float)(var8 >> 8 & 255) / 255.0F;
            float var11 = (float)(var8 & 255) / 255.0F;
            this.modelRenderer.renderModel(var2.last(), var3.getBuffer(ItemBlockRenderTypes.getRenderType(var1, false)), var1, var7, var9, var10, var11, var4, var5);
            break;
         case ENTITYBLOCK_ANIMATED:
            this.blockEntityRenderer.renderByItem(new ItemStack(var1.getBlock()), ItemTransforms.TransformType.NONE, var2, var3, var4, var5);
         }

      }
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.liquidBlockRenderer.setupSprites();
   }
}
