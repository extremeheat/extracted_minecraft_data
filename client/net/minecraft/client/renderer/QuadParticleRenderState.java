package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.particle.SingleQuadParticle;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class QuadParticleRenderState implements ParticleGroupRenderState {
   private static final int INITIAL_PARTICLE_CAPACITY = 1024;
   private static final int FLOATS_PER_PARTICLE = 12;
   private static final int INTS_PER_PARTICLE = 2;
   private final EnumMap<SingleQuadParticle.Layer, Storage> particles = new EnumMap(SingleQuadParticle.Layer.class);

   public QuadParticleRenderState() {
      super();

      for(SingleQuadParticle.Layer var4 : SingleQuadParticle.Layer.values()) {
         this.particles.put(var4, new Storage());
      }

   }

   public void add(SingleQuadParticle.Layer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, int var14, int var15) {
      ((Storage)this.particles.get(var1)).add(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15);
   }

   public void clear() {
      this.particles.values().forEach(Storage::clear);
   }

   private void render(MultiBufferSource.BufferSource var1) {
      for(Map.Entry var3 : this.particles.entrySet()) {
         VertexConsumer var4 = var1.getBuffer(((SingleQuadParticle.Layer)var3.getKey()).getRenderType());
         ((Storage)var3.getValue()).forEachParticle((var2, var3x, var4x, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15) -> this.renderRotatedQuad(var4, var2, var3x, var4x, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15));
      }

      var1.endBatch();
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

   public void submit(SubmitNodeCollector var1) {
      var1.submitParticleGroup(this::render);
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
   }

   @FunctionalInterface
   public interface ParticleConsumer {
      void consume(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, int var13, int var14);
   }
}
