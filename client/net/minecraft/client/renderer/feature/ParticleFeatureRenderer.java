package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Queue;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.QuadParticleRenderState;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureManager;

public class ParticleFeatureRenderer implements AutoCloseable {
   private final Queue<ParticleBufferCache> availableBuffers = new ArrayDeque();
   private final List<ParticleBufferCache> usedBuffers = new ArrayList();

   public ParticleFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeCollection var1) {
      if (!var1.getParticleGroupRenderers().isEmpty()) {
         GpuDevice var2 = RenderSystem.getDevice();
         Minecraft var3 = Minecraft.getInstance();
         TextureManager var4 = var3.getTextureManager();
         RenderTarget var5 = var3.getMainRenderTarget();
         RenderTarget var6 = var3.levelRenderer.getParticlesTarget();

         for(SubmitNodeCollector.ParticleGroupRenderer var8 : var1.getParticleGroupRenderers()) {
            ParticleBufferCache var9 = (ParticleBufferCache)this.availableBuffers.poll();
            if (var9 == null) {
               var9 = new ParticleBufferCache();
            }

            this.usedBuffers.add(var9);
            QuadParticleRenderState.PreparedBuffers var10 = var8.prepare(var9);
            if (var10 != null) {
               try (RenderPass var11 = var2.createCommandEncoder().createRenderPass(() -> "Particles - Main", var5.getColorTextureView(), OptionalInt.empty(), var5.getDepthTextureView(), OptionalDouble.empty())) {
                  this.prepareRenderPass(var11);
                  var8.render(var10, var9, var11, var4, false);
                  if (var6 == null) {
                     var8.render(var10, var9, var11, var4, true);
                  }
               }

               if (var6 != null) {
                  try (RenderPass var18 = var2.createCommandEncoder().createRenderPass(() -> "Particles - Transparent", var6.getColorTextureView(), OptionalInt.empty(), var6.getDepthTextureView(), OptionalDouble.empty())) {
                     this.prepareRenderPass(var18);
                     var8.render(var10, var9, var18, var4, true);
                  }
               }
            }
         }

      }
   }

   public void endFrame() {
      for(ParticleBufferCache var2 : this.usedBuffers) {
         var2.rotate();
      }

      this.availableBuffers.addAll(this.usedBuffers);
      this.usedBuffers.clear();
   }

   private void prepareRenderPass(RenderPass var1) {
      var1.setUniform("Projection", RenderSystem.getProjectionMatrixBuffer());
      var1.setUniform("Fog", RenderSystem.getShaderFog());
      var1.bindSampler("Sampler2", Minecraft.getInstance().gameRenderer.lightTexture().getTextureView());
   }

   public void close() {
      this.availableBuffers.forEach(ParticleBufferCache::close);
   }

   public static class ParticleBufferCache implements AutoCloseable {
      @Nullable
      private MappableRingBuffer ringBuffer;

      public ParticleBufferCache() {
         super();
      }

      public void write(ByteBuffer var1) {
         if (this.ringBuffer == null || this.ringBuffer.size() < var1.remaining()) {
            if (this.ringBuffer != null) {
               this.ringBuffer.close();
            }

            this.ringBuffer = new MappableRingBuffer(() -> "Particle Vertices", 34, var1.remaining());
         }

         try (GpuBuffer.MappedView var2 = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.ringBuffer.currentBuffer().slice(), false, true)) {
            var2.data().put(var1);
         }

      }

      public GpuBuffer get() {
         if (this.ringBuffer == null) {
            throw new IllegalStateException("Can't get buffer before it's made");
         } else {
            return this.ringBuffer.currentBuffer();
         }
      }

      void rotate() {
         if (this.ringBuffer != null) {
            this.ringBuffer.rotate();
         }

      }

      public void close() {
         if (this.ringBuffer != null) {
            this.ringBuffer.close();
         }

      }
   }
}
