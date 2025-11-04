package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.CubeMapTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CubeMap implements AutoCloseable {
   private static final int SIDES = 6;
   private final GpuBuffer vertexBuffer;
   private final CachedPerspectiveProjectionMatrixBuffer projectionMatrixUbo;
   private final Identifier location;

   public CubeMap(Identifier var1) {
      super();
      this.location = var1;
      this.projectionMatrixUbo = new CachedPerspectiveProjectionMatrixBuffer("cubemap", 0.05F, 10.0F);
      this.vertexBuffer = initializeVertices();
   }

   public void render(Minecraft var1, float var2, float var3) {
      RenderSystem.setProjectionMatrix(this.projectionMatrixUbo.getBuffer(var1.getWindow().getWidth(), var1.getWindow().getHeight(), 85.0F), ProjectionType.PERSPECTIVE);
      RenderPipeline var4 = RenderPipelines.PANORAMA;
      RenderTarget var5 = Minecraft.getInstance().getMainRenderTarget();
      GpuTextureView var6 = var5.getColorTextureView();
      GpuTextureView var7 = var5.getDepthTextureView();
      RenderSystem.AutoStorageIndexBuffer var8 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer var9 = var8.getBuffer(36);
      Matrix4fStack var10 = RenderSystem.getModelViewStack();
      var10.pushMatrix();
      var10.rotationX(3.1415927F);
      var10.rotateX(var2 * 0.017453292F);
      var10.rotateY(var3 * 0.017453292F);
      GpuBufferSlice var11 = RenderSystem.getDynamicUniforms().writeTransform(new Matrix4f(var10), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f());
      var10.popMatrix();

      try (RenderPass var12 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Cubemap", var6, OptionalInt.empty(), var7, OptionalDouble.empty())) {
         var12.setPipeline(var4);
         RenderSystem.bindDefaultUniforms(var12);
         var12.setVertexBuffer(0, this.vertexBuffer);
         var12.setIndexBuffer(var9, var8.type());
         var12.setUniform("DynamicTransforms", var11);
         AbstractTexture var13 = var1.getTextureManager().getTexture(this.location);
         var12.bindTexture("Sampler0", var13.getTextureView(), var13.getSampler());
         var12.drawIndexed(0, 0, 36, 1);
      }

   }

   private static GpuBuffer initializeVertices() {
      GpuBuffer var3;
      try (ByteBufferBuilder var0 = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION.getVertexSize() * 4 * 6)) {
         BufferBuilder var1 = new BufferBuilder(var0, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
         var1.addVertex(-1.0F, -1.0F, 1.0F);
         var1.addVertex(-1.0F, 1.0F, 1.0F);
         var1.addVertex(1.0F, 1.0F, 1.0F);
         var1.addVertex(1.0F, -1.0F, 1.0F);
         var1.addVertex(1.0F, -1.0F, 1.0F);
         var1.addVertex(1.0F, 1.0F, 1.0F);
         var1.addVertex(1.0F, 1.0F, -1.0F);
         var1.addVertex(1.0F, -1.0F, -1.0F);
         var1.addVertex(1.0F, -1.0F, -1.0F);
         var1.addVertex(1.0F, 1.0F, -1.0F);
         var1.addVertex(-1.0F, 1.0F, -1.0F);
         var1.addVertex(-1.0F, -1.0F, -1.0F);
         var1.addVertex(-1.0F, -1.0F, -1.0F);
         var1.addVertex(-1.0F, 1.0F, -1.0F);
         var1.addVertex(-1.0F, 1.0F, 1.0F);
         var1.addVertex(-1.0F, -1.0F, 1.0F);
         var1.addVertex(-1.0F, -1.0F, -1.0F);
         var1.addVertex(-1.0F, -1.0F, 1.0F);
         var1.addVertex(1.0F, -1.0F, 1.0F);
         var1.addVertex(1.0F, -1.0F, -1.0F);
         var1.addVertex(-1.0F, 1.0F, 1.0F);
         var1.addVertex(-1.0F, 1.0F, -1.0F);
         var1.addVertex(1.0F, 1.0F, -1.0F);
         var1.addVertex(1.0F, 1.0F, 1.0F);

         try (MeshData var2 = var1.buildOrThrow()) {
            var3 = RenderSystem.getDevice().createBuffer(() -> "Cube map vertex buffer", 32, var2.vertexBuffer());
         }
      }

      return var3;
   }

   public void registerTextures(TextureManager var1) {
      var1.register(this.location, new CubeMapTexture(this.location));
   }

   public void close() {
      this.vertexBuffer.close();
      this.projectionMatrixUbo.close();
   }
}
