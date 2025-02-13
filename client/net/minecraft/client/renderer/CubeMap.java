package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public class CubeMap {
   private static final int SIDES = 6;
   private final List<ResourceLocation> sides;
   private final VertexBuffer[] vertices = new VertexBuffer[6];

   public CubeMap(ResourceLocation var1) {
      super();
      this.sides = IntStream.range(0, 6).mapToObj((var1x) -> {
         String var10001 = var1.getPath();
         return var1.withPath(var10001 + "_" + var1x + ".png");
      }).toList();
   }

   public void render(Minecraft var1, float var2, float var3, float var4) {
      if (this.vertices[0] == null) {
         this.initializeVertices();
      }

      Matrix4f var5 = (new Matrix4f()).setPerspective(1.4835298F, (float)var1.getWindow().getWidth() / (float)var1.getWindow().getHeight(), 0.05F, 10.0F);
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(var5, ProjectionType.PERSPECTIVE);
      Matrix4fStack var6 = RenderSystem.getModelViewStack();
      var6.pushMatrix();
      var6.rotationX(3.1415927F);
      boolean var7 = true;

      for(int var8 = 0; var8 < 4; ++var8) {
         var6.pushMatrix();
         float var9 = ((float)(var8 % 2) / 2.0F - 0.5F) / 256.0F;
         float var10 = ((float)(var8 / 2) / 2.0F - 0.5F) / 256.0F;
         float var11 = 0.0F;
         var6.translate(var9, var10, 0.0F);
         var6.rotateX(var2 * 0.017453292F);
         var6.rotateY(var3 * 0.017453292F);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, var4 / (float)(var8 + 1));

         for(int var12 = 0; var12 < 6; ++var12) {
            this.vertices[var12].bind();
            this.vertices[var12].drawWithRenderType(RenderType.panorama((ResourceLocation)this.sides.get(var12)));
         }

         VertexBuffer.unbind();
         var6.popMatrix();
         RenderSystem.colorMask(true, true, true, false);
      }

      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.restoreProjectionMatrix();
      var6.popMatrix();
   }

   private void initializeVertices() {
      try (ByteBufferBuilder var1 = new ByteBufferBuilder(DefaultVertexFormat.POSITION_TEX.getVertexSize() * 4)) {
         for(int var2 = 0; var2 < 6; ++var2) {
            BufferBuilder var3 = new BufferBuilder(var1, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            if (var2 == 0) {
               var3.addVertex(-1.0F, -1.0F, 1.0F).setUv(0.0F, 0.0F);
               var3.addVertex(-1.0F, 1.0F, 1.0F).setUv(0.0F, 1.0F);
               var3.addVertex(1.0F, 1.0F, 1.0F).setUv(1.0F, 1.0F);
               var3.addVertex(1.0F, -1.0F, 1.0F).setUv(1.0F, 0.0F);
            }

            if (var2 == 1) {
               var3.addVertex(1.0F, -1.0F, 1.0F).setUv(0.0F, 0.0F);
               var3.addVertex(1.0F, 1.0F, 1.0F).setUv(0.0F, 1.0F);
               var3.addVertex(1.0F, 1.0F, -1.0F).setUv(1.0F, 1.0F);
               var3.addVertex(1.0F, -1.0F, -1.0F).setUv(1.0F, 0.0F);
            }

            if (var2 == 2) {
               var3.addVertex(1.0F, -1.0F, -1.0F).setUv(0.0F, 0.0F);
               var3.addVertex(1.0F, 1.0F, -1.0F).setUv(0.0F, 1.0F);
               var3.addVertex(-1.0F, 1.0F, -1.0F).setUv(1.0F, 1.0F);
               var3.addVertex(-1.0F, -1.0F, -1.0F).setUv(1.0F, 0.0F);
            }

            if (var2 == 3) {
               var3.addVertex(-1.0F, -1.0F, -1.0F).setUv(0.0F, 0.0F);
               var3.addVertex(-1.0F, 1.0F, -1.0F).setUv(0.0F, 1.0F);
               var3.addVertex(-1.0F, 1.0F, 1.0F).setUv(1.0F, 1.0F);
               var3.addVertex(-1.0F, -1.0F, 1.0F).setUv(1.0F, 0.0F);
            }

            if (var2 == 4) {
               var3.addVertex(-1.0F, -1.0F, -1.0F).setUv(0.0F, 0.0F);
               var3.addVertex(-1.0F, -1.0F, 1.0F).setUv(0.0F, 1.0F);
               var3.addVertex(1.0F, -1.0F, 1.0F).setUv(1.0F, 1.0F);
               var3.addVertex(1.0F, -1.0F, -1.0F).setUv(1.0F, 0.0F);
            }

            if (var2 == 5) {
               var3.addVertex(-1.0F, 1.0F, 1.0F).setUv(0.0F, 0.0F);
               var3.addVertex(-1.0F, 1.0F, -1.0F).setUv(0.0F, 1.0F);
               var3.addVertex(1.0F, 1.0F, -1.0F).setUv(1.0F, 1.0F);
               var3.addVertex(1.0F, 1.0F, 1.0F).setUv(1.0F, 0.0F);
            }

            this.vertices[var2] = new VertexBuffer(BufferUsage.STATIC_WRITE);
            this.vertices[var2].bind();
            this.vertices[var2].upload(var3.buildOrThrow());
            VertexBuffer.unbind();
         }
      }

   }

   public void registerTextures(TextureManager var1) {
      for(ResourceLocation var3 : this.sides) {
         var1.registerForNextReload(var3);
      }

   }
}
