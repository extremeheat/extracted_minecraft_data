package net.minecraft.client.grid;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.grid.SubGridBlocks;
import net.minecraft.world.grid.SubGridLightEngine;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

public class SubGridMeshBuilder {
   private static final int INITIAL_BUFFER_SIZE = 4096;
   private final BlockRenderDispatcher blockRenderer;
   private final SubGridMeshBuilder.BlockView blockView;

   public SubGridMeshBuilder(BlockRenderDispatcher var1, SubGridMeshBuilder.BlockView var2) {
      super();
      this.blockRenderer = var1;
      this.blockView = var2;
   }

   public SubGridMeshBuilder.Results build() {
      Reference2ObjectArrayMap var1 = new Reference2ObjectArrayMap();
      PoseStack var2 = new PoseStack();
      RandomSource var3 = RandomSource.create();

      for(BlockPos var5 : this.blockView) {
         BlockState var6 = this.blockView.getBlockState(var5);
         FluidState var7 = var6.getFluidState();
         if (!var7.isEmpty()) {
            BufferBuilder var8 = startBuilding(var1, ItemBlockRenderTypes.getRenderLayer(var7));
            this.blockRenderer.renderLiquid(var5, this.blockView, var8, var6, var7, var5.getX(), var5.getY(), var5.getZ());
         }

         if (var6.getRenderShape() != RenderShape.INVISIBLE) {
            BufferBuilder var9 = startBuilding(var1, ItemBlockRenderTypes.getChunkRenderType(var6));
            var2.pushPose();
            var2.translate((float)var5.getX(), (float)var5.getY(), (float)var5.getZ());
            this.blockRenderer.renderBatched(var6, var5, this.blockView, var2, var9, true, var3);
            var2.popPose();
         }
      }

      return new SubGridMeshBuilder.Results(var1);
   }

   private static BufferBuilder startBuilding(Reference2ObjectMap<RenderType, BufferBuilder> var0, RenderType var1) {
      return (BufferBuilder)var0.computeIfAbsent(var1, var0x -> {
         BufferBuilder var1xx = new BufferBuilder(4096);
         var1xx.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
         return var1xx;
      });
   }

   public static record BlockView(Level a, SubGridBlocks b, Holder<Biome> c) implements BlockAndTintGetter, Iterable<BlockPos> {
      private final Level level;
      private final SubGridBlocks blocks;
      private final Holder<Biome> biome;

      public BlockView(Level var1, SubGridBlocks var2, Holder<Biome> var3) {
         super();
         this.level = var1;
         this.blocks = var2;
         this.biome = var3;
      }

      public static SubGridMeshBuilder.BlockView copyOf(ClientSubGrid var0) {
         return new SubGridMeshBuilder.BlockView(var0.level(), var0.getBlocks().copy(), var0.getBiome());
      }

      @Override
      public float getShade(Direction var1, boolean var2) {
         return this.level.getShade(var1, var2);
      }

      @Override
      public LevelLightEngine getLightEngine() {
         return SubGridLightEngine.INSTANCE;
      }

      @Override
      public int getBlockTint(BlockPos var1, ColorResolver var2) {
         return var2.getColor(this.biome.value(), (double)var1.getX(), (double)var1.getZ());
      }

      @Nullable
      @Override
      public BlockEntity getBlockEntity(BlockPos var1) {
         return null;
      }

      @Override
      public BlockState getBlockState(BlockPos var1) {
         return this.blocks.getBlockState(var1);
      }

      @Override
      public FluidState getFluidState(BlockPos var1) {
         return this.getBlockState(var1).getFluidState();
      }

      @Override
      public boolean isPotato() {
         return false;
      }

      @Override
      public int getHeight() {
         return this.blocks.sizeY();
      }

      @Override
      public int getMinBuildHeight() {
         return 0;
      }

      @Override
      public Iterator<BlockPos> iterator() {
         return BlockPos.betweenClosed(0, 0, 0, this.blocks.sizeX() - 1, this.blocks.sizeY() - 1, this.blocks.sizeZ() - 1).iterator();
      }
   }

   public static class Results implements AutoCloseable {
      private final Reference2ObjectMap<RenderType, BufferBuilder> builders;

      public Results(Reference2ObjectMap<RenderType, BufferBuilder> var1) {
         super();
         this.builders = var1;
      }

      public void uploadTo(Reference2ObjectMap<RenderType, VertexBuffer> var1) {
         for(RenderType var3 : RenderType.chunkBufferLayers()) {
            BufferBuilder.RenderedBuffer var4 = this.takeLayer(var3);
            if (var4 == null) {
               VertexBuffer var5 = (VertexBuffer)var1.remove(var3);
               if (var5 != null) {
                  var5.close();
               }
            } else {
               VertexBuffer var6 = (VertexBuffer)var1.get(var3);
               if (var6 == null) {
                  var6 = new VertexBuffer(VertexBuffer.Usage.STATIC);
                  var1.put(var3, var6);
               }

               var6.bind();
               var6.upload(var4);
            }
         }
      }

      @Nullable
      public BufferBuilder.RenderedBuffer takeLayer(RenderType var1) {
         BufferBuilder var2 = (BufferBuilder)this.builders.get(var1);
         return var2 != null ? var2.endOrDiscardIfEmpty() : null;
      }

      @Override
      public void close() {
         this.builders.values().forEach(BufferBuilder::release);
      }
   }
}
