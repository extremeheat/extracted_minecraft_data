package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Supplier;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BlockRenderDispatcher implements ResourceManagerReloadListener {
   private final BlockModelShaper blockModelShaper;
   private final ModelBlockRenderer modelRenderer;
   private final Supplier<SpecialBlockModelRenderer> specialBlockModelRenderer;
   private final LiquidBlockRenderer liquidBlockRenderer;
   private final RandomSource random = RandomSource.create();
   private final BlockColors blockColors;

   public BlockRenderDispatcher(BlockModelShaper var1, Supplier<SpecialBlockModelRenderer> var2, BlockColors var3) {
      super();
      this.blockModelShaper = var1;
      this.specialBlockModelRenderer = var2;
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

   public void renderBatched(BlockState var1, BlockPos var2, BlockAndTintGetter var3, PoseStack var4, VertexConsumer var5, boolean var6, RandomSource var7) {
      try {
         this.modelRenderer.tesselateBlock(var3, this.getBlockModel(var1), var1, var2, var4, var5, var6, var7, var1.getSeed(var2), OverlayTexture.NO_OVERLAY);
      } catch (Throwable var11) {
         CrashReport var9 = CrashReport.forThrowable(var11, "Tesselating block in world");
         CrashReportCategory var10 = var9.addCategory("Block being tesselated");
         CrashReportCategory.populateBlockDetails(var10, var3, var2, var1);
         throw new ReportedException(var9);
      }
   }

   public void renderLiquid(BlockPos var1, BlockAndTintGetter var2, VertexConsumer var3, BlockState var4, FluidState var5) {
      try {
         this.liquidBlockRenderer.tesselate(var2, var1, var3, var4, var5);
      } catch (Throwable var9) {
         CrashReport var7 = CrashReport.forThrowable(var9, "Tesselating liquid in world");
         CrashReportCategory var8 = var7.addCategory("Block being tesselated");
         CrashReportCategory.populateBlockDetails(var8, var2, var1, (BlockState)null);
         throw new ReportedException(var7);
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
         BakedModel var7 = this.getBlockModel(var1);
         int var8 = this.blockColors.getColor(var1, (BlockAndTintGetter)null, (BlockPos)null, 0);
         float var9 = (float)(var8 >> 16 & 255) / 255.0F;
         float var10 = (float)(var8 >> 8 & 255) / 255.0F;
         float var11 = (float)(var8 & 255) / 255.0F;
         this.modelRenderer.renderModel(var2.last(), var3.getBuffer(ItemBlockRenderTypes.getRenderType(var1)), var1, var7, var9, var10, var11, var4, var5);
         ((SpecialBlockModelRenderer)this.specialBlockModelRenderer.get()).renderByBlock(var1.getBlock(), ItemDisplayContext.NONE, var2, var3, var4, var5);
      }
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.liquidBlockRenderer.setupSprites();
   }
}
