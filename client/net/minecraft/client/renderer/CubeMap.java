package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public class CubeMap {
   private static final int SIDES = 6;
   private final ResourceLocation[] images = new ResourceLocation[6];

   public CubeMap(ResourceLocation var1) {
      super();

      for(int var2 = 0; var2 < 6; ++var2) {
         ResourceLocation[] var10000 = this.images;
         String var10003 = var1.getPath();
         var10000[var2] = var1.withPath(var10003 + "_" + var2 + ".png");
      }

   }

   public void render(Minecraft var1, float var2, float var3, float var4) {
      Tesselator var5 = Tesselator.getInstance();
      Matrix4f var6 = (new Matrix4f()).setPerspective(1.4835298F, (float)var1.getWindow().getWidth() / (float)var1.getWindow().getHeight(), 0.05F, 10.0F);
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(var6, VertexSorting.DISTANCE_TO_ORIGIN);
      Matrix4fStack var7 = RenderSystem.getModelViewStack();
      var7.pushMatrix();
      var7.rotationX(3.1415927F);
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.enableBlend();
      RenderSystem.disableCull();
      RenderSystem.depthMask(false);
      boolean var8 = true;

      for(int var9 = 0; var9 < 4; ++var9) {
         var7.pushMatrix();
         float var10 = ((float)(var9 % 2) / 2.0F - 0.5F) / 256.0F;
         float var11 = ((float)(var9 / 2) / 2.0F - 0.5F) / 256.0F;
         float var12 = 0.0F;
         var7.translate(var10, var11, 0.0F);
         var7.rotateX(var2 * 0.017453292F);
         var7.rotateY(var3 * 0.017453292F);
         RenderSystem.applyModelViewMatrix();

         for(int var13 = 0; var13 < 6; ++var13) {
            RenderSystem.setShaderTexture(0, this.images[var13]);
            BufferBuilder var14 = var5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            int var15 = Math.round(255.0F * var4) / (var9 + 1);
            if (var13 == 0) {
               var14.addVertex(-1.0F, -1.0F, 1.0F).setUv(0.0F, 0.0F).setWhiteAlpha(var15);
               var14.addVertex(-1.0F, 1.0F, 1.0F).setUv(0.0F, 1.0F).setWhiteAlpha(var15);
               var14.addVertex(1.0F, 1.0F, 1.0F).setUv(1.0F, 1.0F).setWhiteAlpha(var15);
               var14.addVertex(1.0F, -1.0F, 1.0F).setUv(1.0F, 0.0F).setWhiteAlpha(var15);
            }

            if (var13 == 1) {
               var14.addVertex(1.0F, -1.0F, 1.0F).setUv(0.0F, 0.0F).setWhiteAlpha(var15);
               var14.addVertex(1.0F, 1.0F, 1.0F).setUv(0.0F, 1.0F).setWhiteAlpha(var15);
               var14.addVertex(1.0F, 1.0F, -1.0F).setUv(1.0F, 1.0F).setWhiteAlpha(var15);
               var14.addVertex(1.0F, -1.0F, -1.0F).setUv(1.0F, 0.0F).setWhiteAlpha(var15);
            }

            if (var13 == 2) {
               var14.addVertex(1.0F, -1.0F, -1.0F).setUv(0.0F, 0.0F).setWhiteAlpha(var15);
               var14.addVertex(1.0F, 1.0F, -1.0F).setUv(0.0F, 1.0F).setWhiteAlpha(var15);
               var14.addVertex(-1.0F, 1.0F, -1.0F).setUv(1.0F, 1.0F).setWhiteAlpha(var15);
               var14.addVertex(-1.0F, -1.0F, -1.0F).setUv(1.0F, 0.0F).setWhiteAlpha(var15);
            }

            if (var13 == 3) {
               var14.addVertex(-1.0F, -1.0F, -1.0F).setUv(0.0F, 0.0F).setWhiteAlpha(var15);
               var14.addVertex(-1.0F, 1.0F, -1.0F).setUv(0.0F, 1.0F).setWhiteAlpha(var15);
               var14.addVertex(-1.0F, 1.0F, 1.0F).setUv(1.0F, 1.0F).setWhiteAlpha(var15);
               var14.addVertex(-1.0F, -1.0F, 1.0F).setUv(1.0F, 0.0F).setWhiteAlpha(var15);
            }

            if (var13 == 4) {
               var14.addVertex(-1.0F, -1.0F, -1.0F).setUv(0.0F, 0.0F).setWhiteAlpha(var15);
               var14.addVertex(-1.0F, -1.0F, 1.0F).setUv(0.0F, 1.0F).setWhiteAlpha(var15);
               var14.addVertex(1.0F, -1.0F, 1.0F).setUv(1.0F, 1.0F).setWhiteAlpha(var15);
               var14.addVertex(1.0F, -1.0F, -1.0F).setUv(1.0F, 0.0F).setWhiteAlpha(var15);
            }

            if (var13 == 5) {
               var14.addVertex(-1.0F, 1.0F, 1.0F).setUv(0.0F, 0.0F).setWhiteAlpha(var15);
               var14.addVertex(-1.0F, 1.0F, -1.0F).setUv(0.0F, 1.0F).setWhiteAlpha(var15);
               var14.addVertex(1.0F, 1.0F, -1.0F).setUv(1.0F, 1.0F).setWhiteAlpha(var15);
               var14.addVertex(1.0F, 1.0F, 1.0F).setUv(1.0F, 0.0F).setWhiteAlpha(var15);
            }

            BufferUploader.drawWithShader(var14.buildOrThrow());
         }

         var7.popMatrix();
         RenderSystem.colorMask(true, true, true, false);
      }

      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.restoreProjectionMatrix();
      var7.popMatrix();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
      RenderSystem.enableDepthTest();
   }

   public CompletableFuture<Void> preload(TextureManager var1, Executor var2) {
      CompletableFuture[] var3 = new CompletableFuture[6];

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var3[var4] = var1.preload(this.images[var4], var2);
      }

      return CompletableFuture.allOf(var3);
   }
}
