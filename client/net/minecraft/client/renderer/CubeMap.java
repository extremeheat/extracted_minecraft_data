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
   public static final int SIDES = 6;
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
         this.cubeMapBuffer = initializeVertices();
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
      RenderSystem.AutoStorageIndexBuffer var12 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer var13 = var12.getBuffer(36);

      try (RenderPass var14 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var10, OptionalInt.empty(), var11, OptionalDouble.empty())) {
         var14.setPipeline(var8);
         var14.setVertexBuffer(0, this.cubeMapBuffer);
         var14.setIndexBuffer(var13, var12.type());

         for(int var15 = 0; var15 < 4; ++var15) {
            var6.pushMatrix();
            float var16 = ((float)(var15 % 2) / 2.0F - 0.5F) / 256.0F;
            float var17 = ((float)(var15 / 2) / 2.0F - 0.5F) / 256.0F;
            float var18 = 0.0F;
            var6.translate(var16, var17, 0.0F);
            var6.rotateX(var2 * 0.017453292F);
            var6.rotateY(var3 * 0.017453292F);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, var4 / (float)(var15 + 1));

            for(int var19 = 0; var19 < 6; ++var19) {
               var14.bindSampler("Sampler0", var1.getTextureManager().getTexture((ResourceLocation)this.sides.get(var19)).getTexture());
               var14.drawIndexed(6 * var19, 6);
            }

            var6.popMatrix();
         }
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.restoreProjectionMatrix();
      var6.popMatrix();
   }

   public static GpuBuffer initializeVertices() {
      GpuBuffer var0 = RenderSystem.getDevice().createBuffer(() -> "Cube map vertex buffer", BufferType.VERTICES, BufferUsage.DYNAMIC_WRITE, 24 * DefaultVertexFormat.POSITION_TEX.getVertexSize());

      GpuBuffer var10;
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
            var4.writeToBuffer(var0, var3.vertexBuffer(), 0);
         }

         var10 = var0;
      }

      return var10;
   }

   public void registerTextures(TextureManager var1) {
      for(ResourceLocation var3 : this.sides) {
         var1.registerForNextReload(var3);
      }

   }
}
