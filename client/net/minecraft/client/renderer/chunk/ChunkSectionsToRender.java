package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.EnumMap;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;

public record ChunkSectionsToRender(GpuTextureView textureView, EnumMap<ChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>> drawsPerLayer, int maxIndicesRequired, GpuBufferSlice[] chunkSectionInfos) {
   public ChunkSectionsToRender {
      super();
   }

   public void renderGroup(final ChunkSectionLayerGroup group, final GpuSampler sampler) {
      RenderSystem.AutoStorageIndexBuffer autoIndices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer defaultIndexBuffer = this.maxIndicesRequired == 0 ? null : autoIndices.getBuffer(this.maxIndicesRequired);
      VertexFormat.IndexType defaultIndexType = this.maxIndicesRequired == 0 ? null : autoIndices.type();
      ChunkSectionLayer[] layers = group.layers();
      Minecraft minecraft = Minecraft.getInstance();
      boolean wireframe = SharedConstants.DEBUG_HOTKEYS && minecraft.wireframe;
      RenderTarget renderTarget = group.outputTarget();

      try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Section layers for " + group.label(), renderTarget.getColorTextureView(), OptionalInt.empty(), renderTarget.getDepthTextureView(), OptionalDouble.empty())) {
         RenderSystem.bindDefaultUniforms(renderPass);
         renderPass.bindTexture("Sampler2", minecraft.gameRenderer.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));

         for(ChunkSectionLayer layer : layers) {
            List<RenderPass.Draw<GpuBufferSlice[]>> draws = (List)this.drawsPerLayer.get(layer);
            if (!draws.isEmpty()) {
               if (layer == ChunkSectionLayer.TRANSLUCENT) {
                  draws = draws.reversed();
               }

               renderPass.setPipeline(wireframe ? RenderPipelines.WIREFRAME : layer.pipeline());
               renderPass.bindTexture("Sampler0", this.textureView, sampler);
               renderPass.drawMultipleIndexed(draws, defaultIndexBuffer, defaultIndexType, List.of("ChunkSection"), this.chunkSectionInfos);
            }
         }
      }

   }
}
