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
   public ChunkSectionsToRender(GpuTextureView var1, EnumMap<ChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>> var2, int var3, GpuBufferSlice[] var4) {
      super();
      this.textureView = var1;
      this.drawsPerLayer = var2;
      this.maxIndicesRequired = var3;
      this.chunkSectionInfos = var4;
   }

   public void renderGroup(ChunkSectionLayerGroup var1, GpuSampler var2) {
      RenderSystem.AutoStorageIndexBuffer var3 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer var4 = this.maxIndicesRequired == 0 ? null : var3.getBuffer(this.maxIndicesRequired);
      VertexFormat.IndexType var5 = this.maxIndicesRequired == 0 ? null : var3.type();
      ChunkSectionLayer[] var6 = var1.layers();
      Minecraft var7 = Minecraft.getInstance();
      boolean var8 = SharedConstants.DEBUG_HOTKEYS && var7.wireframe;
      RenderTarget var9 = var1.outputTarget();

      try (RenderPass var10 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Section layers for " + var1.label(), var9.getColorTextureView(), OptionalInt.empty(), var9.getDepthTextureView(), OptionalDouble.empty())) {
         RenderSystem.bindDefaultUniforms(var10);
         var10.bindTexture("Sampler2", var7.gameRenderer.lightTexture().getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));

         for(ChunkSectionLayer var14 : var6) {
            List var15 = (List)this.drawsPerLayer.get(var14);
            if (!var15.isEmpty()) {
               if (var14 == ChunkSectionLayer.TRANSLUCENT) {
                  var15 = var15.reversed();
               }

               var10.setPipeline(var8 ? RenderPipelines.WIREFRAME : var14.pipeline());
               var10.bindTexture("Sampler0", this.textureView, var2);
               var10.drawMultipleIndexed(var15, var4, var5, List.of("ChunkSection"), this.chunkSectionInfos);
            }
         }
      }

   }
}
