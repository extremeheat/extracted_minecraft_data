package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.EnumMap;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;

public record ChunkSectionsToRender(EnumMap<ChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>> drawsPerLayer, int maxIndicesRequired, GpuBufferSlice[] dynamicTransforms) {
   public ChunkSectionsToRender(EnumMap<ChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>> var1, int var2, GpuBufferSlice[] var3) {
      super();
      this.drawsPerLayer = var1;
      this.maxIndicesRequired = var2;
      this.dynamicTransforms = var3;
   }

   public void renderGroup(ChunkSectionLayerGroup var1) {
      RenderSystem.AutoStorageIndexBuffer var2 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer var3 = this.maxIndicesRequired == 0 ? null : var2.getBuffer(this.maxIndicesRequired);
      VertexFormat.IndexType var4 = this.maxIndicesRequired == 0 ? null : var2.type();
      ChunkSectionLayer[] var5 = var1.layers();
      Minecraft var6 = Minecraft.getInstance();
      boolean var7 = SharedConstants.DEBUG_HOTKEYS && var6.wireframe;
      RenderTarget var8 = var1.outputTarget();
      GpuSampler var9 = RenderSystem.getSamplerCache().getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST, FilterMode.NEAREST);

      try (RenderPass var10 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Section layers for " + var1.label(), var8.getColorTextureView(), OptionalInt.empty(), var8.getDepthTextureView(), OptionalDouble.empty())) {
         RenderSystem.bindDefaultUniforms(var10);
         var10.bindTexture("Sampler2", var6.gameRenderer.lightTexture().getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));

         for(ChunkSectionLayer var14 : var5) {
            List var15 = (List)this.drawsPerLayer.get(var14);
            if (!var15.isEmpty()) {
               if (var14 == ChunkSectionLayer.TRANSLUCENT) {
                  var15 = var15.reversed();
               }

               var10.setPipeline(var7 ? RenderPipelines.WIREFRAME : var14.pipeline());
               var10.bindTexture("Sampler0", var14.texture().getTextureView(), var9);
               var10.drawMultipleIndexed(var15, var3, var4, List.of("DynamicTransforms"), this.dynamicTransforms);
            }
         }
      }

   }
}
