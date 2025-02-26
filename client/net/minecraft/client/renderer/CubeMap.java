package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
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

public class CubeMap {
   private static final int SIDES = 6;
   @Nullable
   private GpuBuffer cubeMapBuffer = null;
   private final List<ResourceLocation> sides;

   public CubeMap(ResourceLocation var1) {
      super();
      this.sides = IntStream.range(0, 6).mapToObj((var1x) -> {
         String var10001 = var1.getPath();
         return var1.withPath(var10001 + "_" + var1x + ".png");
      }).toList();
   }

   public void render(Minecraft var1, float var2, float var3, float var4) {
      if (this.cubeMapBuffer == null) {
         this.initializeVertices();
      }

      Matrix4f var5 = (new Matrix4f()).setPerspective(1.4835298F, (float)var1.getWindow().getWidth() / (float)var1.getWindow().getHeight(), 0.05F, 10.0F);
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(var5, ProjectionType.PERSPECTIVE);
      Matrix4fStack var6 = RenderSystem.getModelViewStack();
      var6.pushMatrix();
      var6.rotationX(3.1415927F);
      boolean var7 = true;
      RenderPipeline var8 = RenderPipelines.PANORAMA;
      RenderTarget var9 = Minecraft.getInstance().getMainRenderTarget();
      GpuTexture var10 = var9.getColorTexture();
      GpuTexture var11 = var9.getDepthTexture();

      try (RenderPass var12 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var10, OptionalInt.empty(), var11, OptionalDouble.empty())) {
         RenderSystem.AutoStorageIndexBuffer var13 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
         var12.setPipeline(var8);
         var12.setVertexBuffer(0, this.cubeMapBuffer);
         var12.setIndexBuffer(var13.getBuffer(6), var13.type());

         for(int var14 = 0; var14 < 4; ++var14) {
            var6.pushMatrix();
            float var15 = ((float)(var14 % 2) / 2.0F - 0.5F) / 256.0F;
            float var16 = ((float)(var14 / 2) / 2.0F - 0.5F) / 256.0F;
            float var17 = 0.0F;
            var6.translate(var15, var16, 0.0F);
            var6.rotateX(var2 * 0.017453292F);
            var6.rotateY(var3 * 0.017453292F);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, var4 / (float)(var14 + 1));

            for(int var18 = 0; var18 < 6; ++var18) {
               var12.bindSampler("Sampler0", var1.getTextureManager().getTexture((ResourceLocation)this.sides.get(var18)).getTexture());
               var12.drawIndexed(6 * var18, 6);
            }

            var6.popMatrix();
         }
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.restoreProjectionMatrix();
      var6.popMatrix();
   }

   private void initializeVertices() {
      this.cubeMapBuffer = RenderSystem.getDevice().createBuffer(() -> "Cube map vertex buffer", BufferType.VERTICES, BufferUsage.DYNAMIC_WRITE, 24 * DefaultVertexFormat.POSITION_TEX.getVertexSize());

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
            CommandEncoder var4 = RenderSystem.getDevice().createCommandEncoder();
            var4.writeToBuffer(this.cubeMapBuffer, var3.vertexBuffer(), 0);
         }
      }

   }

   public void registerTextures(TextureManager var1) {
      for(ResourceLocation var3 : this.sides) {
         var1.registerForNextReload(var3);
      }

   }
}
