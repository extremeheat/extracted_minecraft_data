package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SectionCompiler {
   private final BlockRenderDispatcher blockRenderer;
   private final BlockEntityRenderDispatcher blockEntityRenderer;

   public SectionCompiler(BlockRenderDispatcher var1, BlockEntityRenderDispatcher var2) {
      super();
      this.blockRenderer = var1;
      this.blockEntityRenderer = var2;
   }

   public Results compile(SectionPos var1, RenderChunkRegion var2, VertexSorting var3, SectionBufferBuilderPack var4) {
      Results var5 = new Results();
      BlockPos var6 = var1.origin();
      BlockPos var7 = var6.offset(15, 15, 15);
      VisGraph var8 = new VisGraph();
      PoseStack var9 = new PoseStack();
      ModelBlockRenderer.enableCaching();
      Reference2ObjectArrayMap var10 = new Reference2ObjectArrayMap(RenderType.chunkBufferLayers().size());
      RandomSource var11 = RandomSource.create();
      Iterator var12 = BlockPos.betweenClosed(var6, var7).iterator();

      while(var12.hasNext()) {
         BlockPos var13 = (BlockPos)var12.next();
         BlockState var14 = var2.getBlockState(var13);
         if (var14.isSolidRender(var2, var13)) {
            var8.setOpaque(var13);
         }

         if (var14.hasBlockEntity()) {
            BlockEntity var15 = var2.getBlockEntity(var13);
            if (var15 != null) {
               this.handleBlockEntity(var5, var15);
            }
         }

         FluidState var20 = var14.getFluidState();
         RenderType var16;
         BufferBuilder var17;
         if (!var20.isEmpty()) {
            var16 = ItemBlockRenderTypes.getRenderLayer(var20);
            var17 = this.getOrBeginLayer(var10, var4, var16);
            this.blockRenderer.renderLiquid(var13, var2, var17, var14, var20);
         }

         if (var14.getRenderShape() == RenderShape.MODEL) {
            var16 = ItemBlockRenderTypes.getChunkRenderType(var14);
            var17 = this.getOrBeginLayer(var10, var4, var16);
            var9.pushPose();
            var9.translate((float)SectionPos.sectionRelative(var13.getX()), (float)SectionPos.sectionRelative(var13.getY()), (float)SectionPos.sectionRelative(var13.getZ()));
            this.blockRenderer.renderBatched(var14, var13, var2, var9, var17, true, var11);
            var9.popPose();
         }
      }

      var12 = var10.entrySet().iterator();

      while(var12.hasNext()) {
         Map.Entry var18 = (Map.Entry)var12.next();
         RenderType var19 = (RenderType)var18.getKey();
         MeshData var21 = ((BufferBuilder)var18.getValue()).build();
         if (var21 != null) {
            if (var19 == RenderType.translucent()) {
               var5.transparencyState = var21.sortQuads(var4.buffer(RenderType.translucent()), var3);
            }

            var5.renderedLayers.put(var19, var21);
         }
      }

      ModelBlockRenderer.clearCache();
      var5.visibilitySet = var8.resolve();
      return var5;
   }

   private BufferBuilder getOrBeginLayer(Map<RenderType, BufferBuilder> var1, SectionBufferBuilderPack var2, RenderType var3) {
      BufferBuilder var4 = (BufferBuilder)var1.get(var3);
      if (var4 == null) {
         ByteBufferBuilder var5 = var2.buffer(var3);
         var4 = new BufferBuilder(var5, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
         var1.put(var3, var4);
      }

      return var4;
   }

   private <E extends BlockEntity> void handleBlockEntity(Results var1, E var2) {
      BlockEntityRenderer var3 = this.blockEntityRenderer.getRenderer(var2);
      if (var3 != null) {
         var1.blockEntities.add(var2);
         if (var3.shouldRenderOffScreen(var2)) {
            var1.globalBlockEntities.add(var2);
         }
      }

   }

   public static final class Results {
      public final List<BlockEntity> globalBlockEntities = new ArrayList();
      public final List<BlockEntity> blockEntities = new ArrayList();
      public final Map<RenderType, MeshData> renderedLayers = new Reference2ObjectArrayMap();
      public VisibilitySet visibilitySet = new VisibilitySet();
      @Nullable
      public MeshData.SortState transparencyState;

      public Results() {
         super();
      }

      public void release() {
         this.renderedLayers.values().forEach(MeshData::close);
      }
   }
}
