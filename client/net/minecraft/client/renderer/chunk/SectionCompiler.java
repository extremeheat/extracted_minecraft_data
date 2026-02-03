package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

public class SectionCompiler {
   private final BlockRenderDispatcher blockRenderer;
   private final BlockEntityRenderDispatcher blockEntityRenderer;

   public SectionCompiler(final BlockRenderDispatcher blockRenderer, final BlockEntityRenderDispatcher blockEntityRenderer) {
      super();
      this.blockRenderer = blockRenderer;
      this.blockEntityRenderer = blockEntityRenderer;
   }

   public Results compile(final SectionPos sectionPos, final RenderSectionRegion region, final VertexSorting vertexSorting, final SectionBufferBuilderPack builders) {
      Results results = new Results();
      BlockPos minPos = sectionPos.origin();
      BlockPos maxPos = minPos.offset(15, 15, 15);
      VisGraph visGraph = new VisGraph();
      PoseStack poseStack = new PoseStack();
      ModelBlockRenderer.enableCaching();
      Map<ChunkSectionLayer, BufferBuilder> startedLayers = new EnumMap(ChunkSectionLayer.class);
      RandomSource random = RandomSource.create();
      List<BlockModelPart> parts = new ObjectArrayList();

      for(BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
         BlockState blockState = region.getBlockState(pos);
         if (blockState.isSolidRender()) {
            visGraph.setOpaque(pos);
         }

         if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = region.getBlockEntity(pos);
            if (blockEntity != null) {
               this.handleBlockEntity(results, blockEntity);
            }
         }

         FluidState fluidState = blockState.getFluidState();
         if (!fluidState.isEmpty()) {
            ChunkSectionLayer layer = ItemBlockRenderTypes.getRenderLayer(fluidState);
            BufferBuilder builder = this.getOrBeginLayer(startedLayers, builders, layer);
            this.blockRenderer.renderLiquid(pos, region, builder, blockState, fluidState);
         }

         if (blockState.getRenderShape() == RenderShape.MODEL) {
            ChunkSectionLayer layer = ItemBlockRenderTypes.getChunkRenderType(blockState);
            BufferBuilder builder = this.getOrBeginLayer(startedLayers, builders, layer);
            random.setSeed(blockState.getSeed(pos));
            this.blockRenderer.getBlockModel(blockState).collectParts(random, parts);
            poseStack.pushPose();
            poseStack.translate((float)SectionPos.sectionRelative(pos.getX()), (float)SectionPos.sectionRelative(pos.getY()), (float)SectionPos.sectionRelative(pos.getZ()));
            this.blockRenderer.renderBatched(blockState, pos, region, poseStack, builder, true, parts);
            poseStack.popPose();
            parts.clear();
         }
      }

      for(Map.Entry<ChunkSectionLayer, BufferBuilder> entry : startedLayers.entrySet()) {
         ChunkSectionLayer layer = (ChunkSectionLayer)entry.getKey();
         MeshData mesh = ((BufferBuilder)entry.getValue()).build();
         if (mesh != null) {
            if (layer == ChunkSectionLayer.TRANSLUCENT) {
               results.transparencyState = mesh.sortQuads(builders.buffer(layer), vertexSorting);
            }

            results.renderedLayers.put(layer, mesh);
         }
      }

      ModelBlockRenderer.clearCache();
      results.visibilitySet = visGraph.resolve();
      return results;
   }

   private BufferBuilder getOrBeginLayer(final Map<ChunkSectionLayer, BufferBuilder> startedLayers, final SectionBufferBuilderPack buffers, final ChunkSectionLayer renderType) {
      BufferBuilder builder = (BufferBuilder)startedLayers.get(renderType);
      if (builder == null) {
         ByteBufferBuilder buffer = buffers.buffer(renderType);
         builder = new BufferBuilder(buffer, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
         startedLayers.put(renderType, builder);
      }

      return builder;
   }

   private <E extends BlockEntity> void handleBlockEntity(final Results results, final E blockEntity) {
      BlockEntityRenderer<E, ?> renderer = this.blockEntityRenderer.getRenderer(blockEntity);
      if (renderer != null && !renderer.shouldRenderOffScreen()) {
         results.blockEntities.add(blockEntity);
      }

   }

   public static final class Results {
      public final List<BlockEntity> blockEntities = new ArrayList();
      public final Map<ChunkSectionLayer, MeshData> renderedLayers = new EnumMap(ChunkSectionLayer.class);
      public VisibilitySet visibilitySet = new VisibilitySet();
      public MeshData.@Nullable SortState transparencyState;

      public Results() {
         super();
      }

      public void release() {
         this.renderedLayers.values().forEach(MeshData::close);
      }
   }
}
