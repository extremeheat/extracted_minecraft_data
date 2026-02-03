package net.minecraft.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

public class BlockRenderDispatcher implements ResourceManagerReloadListener {
   private final BlockModelShaper blockModelShaper;
   private final MaterialSet materials;
   private final ModelBlockRenderer modelRenderer;
   private @Nullable LiquidBlockRenderer liquidBlockRenderer;
   private final RandomSource singleThreadRandom = RandomSource.create();
   private final List<BlockModelPart> singleThreadPartList = new ArrayList();
   private final BlockColors blockColors;

   public BlockRenderDispatcher(final BlockModelShaper blockModelShaper, final MaterialSet materials, final BlockColors blockColors) {
      super();
      this.blockModelShaper = blockModelShaper;
      this.materials = materials;
      this.blockColors = blockColors;
      this.modelRenderer = new ModelBlockRenderer(this.blockColors);
   }

   public BlockModelShaper getBlockModelShaper() {
      return this.blockModelShaper;
   }

   public void renderBreakingTexture(final BlockState state, final BlockPos pos, final BlockAndTintGetter level, final PoseStack poseStack, final VertexConsumer builder) {
      if (state.getRenderShape() == RenderShape.MODEL) {
         BlockStateModel model = this.blockModelShaper.getBlockModel(state);
         this.singleThreadRandom.setSeed(state.getSeed(pos));
         this.singleThreadPartList.clear();
         model.collectParts(this.singleThreadRandom, this.singleThreadPartList);
         this.modelRenderer.tesselateBlock(level, this.singleThreadPartList, state, pos, poseStack, builder, true, OverlayTexture.NO_OVERLAY);
      }
   }

   public void renderBatched(final BlockState blockState, final BlockPos pos, final BlockAndTintGetter level, final PoseStack poseStack, final VertexConsumer builder, final boolean cull, final List<BlockModelPart> parts) {
      try {
         this.modelRenderer.tesselateBlock(level, parts, blockState, pos, poseStack, builder, cull, OverlayTexture.NO_OVERLAY);
      } catch (Throwable t) {
         CrashReport report = CrashReport.forThrowable(t, "Tesselating block in world");
         CrashReportCategory category = report.addCategory("Block being tesselated");
         CrashReportCategory.populateBlockDetails(category, level, pos, blockState);
         throw new ReportedException(report);
      }
   }

   public void renderLiquid(final BlockPos pos, final BlockAndTintGetter level, final VertexConsumer builder, final BlockState blockState, final FluidState fluidState) {
      try {
         ((LiquidBlockRenderer)Objects.requireNonNull(this.liquidBlockRenderer)).tesselate(level, pos, builder, blockState, fluidState);
      } catch (Throwable t) {
         CrashReport report = CrashReport.forThrowable(t, "Tesselating liquid in world");
         CrashReportCategory category = report.addCategory("Block being tesselated");
         CrashReportCategory.populateBlockDetails(category, level, pos, blockState);
         throw new ReportedException(report);
      }
   }

   public ModelBlockRenderer getModelRenderer() {
      return this.modelRenderer;
   }

   public BlockStateModel getBlockModel(final BlockState state) {
      return this.blockModelShaper.getBlockModel(state);
   }

   public void renderSingleBlock(final BlockState state, final PoseStack poseStack, final MultiBufferSource bufferSource, final int lightCoords, final int overlayCoords) {
      RenderShape shape = state.getRenderShape();
      if (shape != RenderShape.INVISIBLE) {
         BlockStateModel model = this.getBlockModel(state);
         int col = this.blockColors.getColor(state, (BlockAndTintGetter)null, (BlockPos)null, 0);
         float r = (float)(col >> 16 & 255) / 255.0F;
         float g = (float)(col >> 8 & 255) / 255.0F;
         float b = (float)(col & 255) / 255.0F;
         ModelBlockRenderer.renderModel(poseStack.last(), bufferSource.getBuffer(ItemBlockRenderTypes.getRenderType(state)), model, r, g, b, lightCoords, overlayCoords);
      }
   }

   public void onResourceManagerReload(final ResourceManager resourceManager) {
      this.liquidBlockRenderer = new LiquidBlockRenderer(this.materials);
   }
}
