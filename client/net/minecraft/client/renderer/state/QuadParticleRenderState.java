package net.minecraft.client.renderer.state;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ParticleFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class QuadParticleRenderState implements SubmitNodeCollector.ParticleGroupRenderer, ParticleGroupRenderState {
   private static final int INITIAL_PARTICLE_CAPACITY = 1024;
   private static final int FLOATS_PER_PARTICLE = 12;
   private static final int INTS_PER_PARTICLE = 2;
   private final Map<SingleQuadParticle.Layer, Storage> particles = new HashMap();
   private int particleCount;

   public QuadParticleRenderState() {
      super();
   }

   public void add(SingleQuadParticle.Layer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, int var14, int var15) {
      ((Storage)this.particles.computeIfAbsent(var1, (var0) -> new Storage())).add(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15);
      ++this.particleCount;
   }

   public void clear() {
      this.particles.values().forEach(Storage::clear);
      this.particleCount = 0;
   }

   @Nullable
   public PreparedBuffers prepare(ParticleFeatureRenderer.ParticleBufferCache var1) {
      int var2 = this.particleCount * 4;

      try (ByteBufferBuilder var3 = ByteBufferBuilder.exactlySized(var2 * DefaultVertexFormat.PARTICLE.getVertexSize())) {
         BufferBuilder var4 = new BufferBuilder(var3, VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
         HashMap var5 = new HashMap();
         int var6 = 0;

         for(Map.Entry var8 : this.particles.entrySet()) {
            ((Storage)var8.getValue()).forEachParticle((var2x, var3x, var4x, var5x, var6x, var7, var8x, var9, var10, var11x, var12x, var13, var14x, var15) -> this.renderRotatedQuad(var4, var2x, var3x, var4x, var5x, var6x, var7, var8x, var9, var10, var11x, var12x, var13, var14x, var15));
            if (((Storage)var8.getValue()).count() > 0) {
               var5.put((SingleQuadParticle.Layer)var8.getKey(), new PreparedLayer(var6, ((Storage)var8.getValue()).count() * 6));
            }

            var6 += ((Storage)var8.getValue()).count() * 4;
         }

         MeshData var12 = var4.build();
         if (var12 != null) {
            var1.write(var12.vertexBuffer());
            RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS).getBuffer(var12.drawState().indexCount());
            GpuBufferSlice var14 = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), RenderSystem.getTextureMatrix(), RenderSystem.getShaderLineWidth());
            return new PreparedBuffers(var12.drawState().indexCount(), var14, var5);
         } else {
            return null;
         }
      }
   }

   public void render(PreparedBuffers var1, ParticleFeatureRenderer.ParticleBufferCache var2, RenderPass var3, TextureManager var4, boolean var5) {
      RenderSystem.AutoStorageIndexBuffer var6 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      var3.setVertexBuffer(0, var2.get());
      var3.setIndexBuffer(var6.getBuffer(var1.indexCount), var6.type());
      var3.setUniform("DynamicTransforms", var1.dynamicTransforms);

      for(Map.Entry var8 : var1.layers.entrySet()) {
         if (var5 == ((SingleQuadParticle.Layer)var8.getKey()).translucent()) {
            var3.setPipeline(((SingleQuadParticle.Layer)var8.getKey()).pipeline());
            var3.bindSampler("Sampler0", var4.getTexture(((SingleQuadParticle.Layer)var8.getKey()).textureAtlasLocation()).getTextureView());
            var3.drawIndexed(((PreparedLayer)var8.getValue()).vertexOffset, 0, ((PreparedLayer)var8.getValue()).indexCount, 1);
         }
      }

   }

   protected void renderRotatedQuad(VertexConsumer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, int var14, int var15) {
      Quaternionf var16 = new Quaternionf(var5, var6, var7, var8);
      this.renderVertex(var1, var16, var2, var3, var4, 1.0F, -1.0F, var9, var11, var13, var14, var15);
      this.renderVertex(var1, var16, var2, var3, var4, 1.0F, 1.0F, var9, var11, var12, var14, var15);
      this.renderVertex(var1, var16, var2, var3, var4, -1.0F, 1.0F, var9, var10, var12, var14, var15);
      this.renderVertex(var1, var16, var2, var3, var4, -1.0F, -1.0F, var9, var10, var13, var14, var15);
   }

   private void renderVertex(VertexConsumer var1, Quaternionf var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, int var11, int var12) {
      Vector3f var13 = (new Vector3f(var6, var7, 0.0F)).rotate(var2).mul(var8).add(var3, var4, var5);
      var1.addVertex(var13.x(), var13.y(), var13.z()).setUv(var9, var10).setColor(var11).setLight(var12);
   }

   public void submit(SubmitNodeCollector var1, CameraRenderState var2) {
      if (this.particleCount > 0) {
         var1.submitParticleGroup(this);
      }

   }

   public static record PreparedBuffers(int indexCount, GpuBufferSlice dynamicTransforms, Map<SingleQuadParticle.Layer, PreparedLayer> layers) {
      final int indexCount;
      final GpuBufferSlice dynamicTransforms;
      final Map<SingleQuadParticle.Layer, PreparedLayer> layers;

      public PreparedBuffers(int var1, GpuBufferSlice var2, Map<SingleQuadParticle.Layer, PreparedLayer> var3) {
         super();
         this.indexCount = var1;
         this.dynamicTransforms = var2;
         this.layers = var3;
      }
   }

   public static record PreparedLayer(int vertexOffset, int indexCount) {
      final int vertexOffset;
      final int indexCount;

      public PreparedLayer(int var1, int var2) {
         super();
         this.vertexOffset = var1;
         this.indexCount = var2;
      }
   }

   static class Storage {
      private int capacity = 1024;
      private float[] floatValues = new float[12288];
      private int[] intValues = new int[2048];
      private int currentParticleIndex;

      Storage() {
         super();
      }

      public void add(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, int var13, int var14) {
         if (this.currentParticleIndex >= this.capacity) {
            this.grow();
         }

         int var15 = this.currentParticleIndex * 12;
         this.floatValues[var15++] = var1;
         this.floatValues[var15++] = var2;
         this.floatValues[var15++] = var3;
         this.floatValues[var15++] = var4;
         this.floatValues[var15++] = var5;
         this.floatValues[var15++] = var6;
         this.floatValues[var15++] = var7;
         this.floatValues[var15++] = var8;
         this.floatValues[var15++] = var9;
         this.floatValues[var15++] = var10;
         this.floatValues[var15++] = var11;
         this.floatValues[var15] = var12;
         var15 = this.currentParticleIndex * 2;
         this.intValues[var15++] = var13;
         this.intValues[var15] = var14;
         ++this.currentParticleIndex;
      }

      public void forEachParticle(ParticleConsumer var1) {
         for(int var2 = 0; var2 < this.currentParticleIndex; ++var2) {
            int var3 = var2 * 12;
            int var4 = var2 * 2;
            var1.consume(this.floatValues[var3++], this.floatValues[var3++], this.floatValues[var3++], this.floatValues[var3++], this.floatValues[var3++], this.floatValues[var3++], this.floatValues[var3++], this.floatValues[var3++], this.floatValues[var3++], this.floatValues[var3++], this.floatValues[var3++], this.floatValues[var3], this.intValues[var4++], this.intValues[var4]);
         }

      }

      public void clear() {
         this.currentParticleIndex = 0;
      }

      private void grow() {
         this.capacity *= 2;
         this.floatValues = Arrays.copyOf(this.floatValues, this.capacity * 12);
         this.intValues = Arrays.copyOf(this.intValues, this.capacity * 2);
      }

      public int count() {
         return this.currentParticleIndex;
      }
   }

   @FunctionalInterface
   public interface ParticleConsumer {
      void consume(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, int var13, int var14);
   }
}
