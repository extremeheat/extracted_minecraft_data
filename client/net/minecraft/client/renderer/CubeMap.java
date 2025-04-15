package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CubeMap {
   private static final int SIDES = 6;
   @Nullable
   private GpuBuffer cubeMapBuffer = null;
   @Nullable
   private CachedPerspectiveProjectionMatrixBuffer cubeMapProjectionMatrixBuffer = null;
   private final List<ResourceLocation> sides;

   public CubeMap(ResourceLocation var1) {
      super();
      this.sides = IntStream.range(0, 6).mapToObj((var1x) -> {
         String var10001 = var1.getPath();
         return var1.withPath(var10001 + "_" + var1x + ".png");
      }).toList();
   }

   public void render(Minecraft var1, float var2, float var3) {
      if (this.cubeMapBuffer == null) {
         this.initializeVertices();
      }

      if (this.cubeMapProjectionMatrixBuffer == null) {
         this.cubeMapProjectionMatrixBuffer = new CachedPerspectiveProjectionMatrixBuffer("cubemap", 0.05F, 10.0F);
      }

      RenderSystem.setProjectionMatrix(this.cubeMapProjectionMatrixBuffer.getBuffer(var1.getWindow().getWidth(), var1.getWindow().getHeight(), 85.0F), ProjectionType.PERSPECTIVE);
      Matrix4fStack var4 = RenderSystem.getModelViewStack();
      var4.pushMatrix();
      var4.rotationX(3.1415927F);
      boolean var5 = true;
      RenderPipeline var6 = RenderPipelines.PANORAMA;
      RenderTarget var7 = Minecraft.getInstance().getMainRenderTarget();
      GpuTexture var8 = var7.getColorTexture();
      GpuTexture var9 = var7.getDepthTexture();
      RenderSystem.AutoStorageIndexBuffer var10 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer var11 = var10.getBuffer(36);
      DynamicUniforms.Transform[] var12 = new DynamicUniforms.Transform[4];

      for(int var13 = 0; var13 < 4; ++var13) {
         var4.pushMatrix();
         float var14 = ((float)(var13 % 2) / 2.0F - 0.5F) / 256.0F;
         float var15 = ((float)(var13 / 2) / 2.0F - 0.5F) / 256.0F;
         float var16 = 0.0F;
         var4.translate(var14, var15, 0.0F);
         var4.rotateX(var2 * 0.017453292F);
         var4.rotateY(var3 * 0.017453292F);
         var12[var13] = new DynamicUniforms.Transform(new Matrix4f(var4), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f(), 0.0F);
         var4.popMatrix();
      }

      GpuBufferSlice[] var19 = RenderSystem.getDynamicUniforms().writeTransforms(var12);

      try (RenderPass var20 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var8, OptionalInt.empty(), var9, OptionalDouble.empty())) {
         var20.setPipeline(var6);
         RenderSystem.bindDefaultUniforms(var20);
         var20.setVertexBuffer(0, this.cubeMapBuffer);
         var20.setIndexBuffer(var11, var10.type());

         for(int var21 = 0; var21 < 4; ++var21) {
            var20.setUniform("DynamicTransforms", var19[var21]);

            for(int var22 = 0; var22 < 6; ++var22) {
               var20.bindSampler("Sampler0", var1.getTextureManager().getTexture((ResourceLocation)this.sides.get(var22)).getTexture());
               var20.drawIndexed(6 * var22, 6);
            }
         }
      }

      var4.popMatrix();
   }

   private void initializeVertices() {
      try (ByteBufferBuilder var1 = new ByteBufferBuilder(DefaultVertexFormat.POSITION_TEX.getVertexSize() * 4)) {
         BufferBuilder var2 = new BufferBuilder(var1, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         var2.addVertex(-1.0F, -1.0F, 1.0F).setUv(0.0F, 0.0F);
         var2.addVertex(-1.0F, 1.0F, 1.0F).setUv(0.0F, 1.0F);
         var2.addVertex(1.0F, 1.0F, 1.0F).setUv(1.0F, 1.0F);
         var2.addVertex(1.0F, -1.0F, 1.0F).setUv(1.0F, 0.0F);
         var2.addVertex(1.0F, -1.0F, 1.0F).setUv(0.0F, 0.0F);
         var2.addVertex(1.0F, 1.0F, 1.0F).setUv(0.0F, 1.0F);
         var2.addVertex(1.0F, 1.0F, -1.0F).setUv(1.0F, 1.0F);
         var2.addVertex(1.0F, -1.0F, -1.0F).setUv(1.0F, 0.0F);
         var2.addVertex(1.0F, -1.0F, -1.0F).setUv(0.0F, 0.0F);
         var2.addVertex(1.0F, 1.0F, -1.0F).setUv(0.0F, 1.0F);
         var2.addVertex(-1.0F, 1.0F, -1.0F).setUv(1.0F, 1.0F);
         var2.addVertex(-1.0F, -1.0F, -1.0F).setUv(1.0F, 0.0F);
         var2.addVertex(-1.0F, -1.0F, -1.0F).setUv(0.0F, 0.0F);
         var2.addVertex(-1.0F, 1.0F, -1.0F).setUv(0.0F, 1.0F);
         var2.addVertex(-1.0F, 1.0F, 1.0F).setUv(1.0F, 1.0F);
         var2.addVertex(-1.0F, -1.0F, 1.0F).setUv(1.0F, 0.0F);
         var2.addVertex(-1.0F, -1.0F, -1.0F).setUv(0.0F, 0.0F);
         var2.addVertex(-1.0F, -1.0F, 1.0F).setUv(0.0F, 1.0F);
         var2.addVertex(1.0F, -1.0F, 1.0F).setUv(1.0F, 1.0F);
         var2.addVertex(1.0F, -1.0F, -1.0F).setUv(1.0F, 0.0F);
         var2.addVertex(-1.0F, 1.0F, 1.0F).setUv(0.0F, 0.0F);
         var2.addVertex(-1.0F, 1.0F, -1.0F).setUv(0.0F, 1.0F);
         var2.addVertex(1.0F, 1.0F, -1.0F).setUv(1.0F, 1.0F);
         var2.addVertex(1.0F, 1.0F, 1.0F).setUv(1.0F, 0.0F);

         try (MeshData var3 = var2.buildOrThrow()) {
            this.cubeMapBuffer = RenderSystem.getDevice().createBuffer(() -> "Cube map vertex buffer", 32, var3.vertexBuffer());
         }
      }

   }

   public void registerTextures(TextureManager var1) {
      for(ResourceLocation var3 : this.sides) {
         var1.registerForNextReload(var3);
      }

   }
}
