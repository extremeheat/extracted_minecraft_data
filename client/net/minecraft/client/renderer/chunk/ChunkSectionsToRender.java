package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;

public record ChunkSectionsToRender(GpuTextureView textureView, EnumMap<ChunkSectionLayer, Int2ObjectOpenHashMap<List<RenderPass.Draw<GpuBufferSlice[]>>>> drawGroupsPerLayer, int maxIndicesRequired, GpuBufferSlice[] chunkSectionInfos) {
   public ChunkSectionsToRender {
      super();
   }

   public void renderGroup(final ChunkSectionLayerGroup group, final GpuSampler sampler) {
      RenderSystem.AutoStorageIndexBuffer autoIndices = RenderSystem.getSequentialBuffer(PrimitiveTopology.QUADS);
      GpuBuffer defaultIndexBuffer = this.maxIndicesRequired == 0 ? null : autoIndices.getBuffer(this.maxIndicesRequired);
      IndexType defaultIndexType = this.maxIndicesRequired == 0 ? null : autoIndices.type();
      ChunkSectionLayer[] layers = group.layers();
      Minecraft minecraft = Minecraft.getInstance();
      boolean wireframe = SharedConstants.DEBUG_HOTKEYS && minecraft.wireframe;
      RenderTarget renderTarget = group.outputTarget();

      try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Section layers for " + group.label(), renderTarget.getColorTextureView(), Optional.empty(), renderTarget.getDepthTextureView(), OptionalDouble.empty())) {
         RenderSystem.bindDefaultUniforms(renderPass);
         renderPass.bindTexture("Sampler0", this.textureView, sampler);
         renderPass.bindTexture("Sampler2", minecraft.gameRenderer.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));

         for(ChunkSectionLayer layer : layers) {
            renderPass.setPipeline(wireframe ? RenderPipelines.WIREFRAME : layer.pipeline());
            Int2ObjectOpenHashMap<List<RenderPass.Draw<GpuBufferSlice[]>>> drawGroup = (Int2ObjectOpenHashMap)this.drawGroupsPerLayer.get(layer);
            ObjectIterator var16 = drawGroup.values().iterator();

            while(var16.hasNext()) {
               List<RenderPass.Draw<GpuBufferSlice[]>> draws = (List)var16.next();
               if (!draws.isEmpty()) {
                  if (layer == ChunkSectionLayer.TRANSLUCENT) {
                     draws = draws.reversed();
                  }

                  renderPass.drawMultipleIndexed(draws, defaultIndexBuffer, defaultIndexType, List.of("ChunkSection"), this.chunkSectionInfos);
               }
            }
         }
      }

   }
}
