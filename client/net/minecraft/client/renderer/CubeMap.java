package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
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
         this.images[var2] = var1.withPath(var1.getPath() + "_" + var2 + ".png");
      }
   }

   public void render(Minecraft var1, float var2, float var3, float var4) {
      Tesselator var5 = Tesselator.getInstance();
      BufferBuilder var6 = var5.getBuilder();
      Matrix4f var7 = new Matrix4f().setPerspective(1.4835298F, (float)var1.getWindow().getWidth() / (float)var1.getWindow().getHeight(), 0.05F, 10.0F);
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(var7, VertexSorting.DISTANCE_TO_ORIGIN);
      Matrix4fStack var8 = RenderSystem.getModelViewStack();
      var8.pushMatrix();
      var8.rotationX(3.1415927F);
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.enableBlend();
      RenderSystem.disableCull();
      RenderSystem.depthMask(false);
      boolean var9 = true;

      for(int var10 = 0; var10 < 4; ++var10) {
         var8.pushMatrix();
         float var11 = ((float)(var10 % 2) / 2.0F - 0.5F) / 256.0F;
         float var12 = ((float)(var10 / 2) / 2.0F - 0.5F) / 256.0F;
         float var13 = 0.0F;
         var8.translate(var11, var12, 0.0F);
         var8.rotateX(var2 * 0.017453292F);
         var8.rotateY(var3 * 0.017453292F);
         RenderSystem.applyModelViewMatrix();

         for(int var14 = 0; var14 < 6; ++var14) {
            RenderSystem.setShaderTexture(0, this.images[var14]);
            var6.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            int var15 = Math.round(255.0F * var4) / (var10 + 1);
            if (var14 == 0) {
               var6.vertex(-1.0, -1.0, 1.0).uv(0.0F, 0.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(-1.0, 1.0, 1.0).uv(0.0F, 1.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(1.0, 1.0, 1.0).uv(1.0F, 1.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(1.0, -1.0, 1.0).uv(1.0F, 0.0F).color(255, 255, 255, var15).endVertex();
            }

            if (var14 == 1) {
               var6.vertex(1.0, -1.0, 1.0).uv(0.0F, 0.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(1.0, 1.0, 1.0).uv(0.0F, 1.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(1.0, 1.0, -1.0).uv(1.0F, 1.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(1.0, -1.0, -1.0).uv(1.0F, 0.0F).color(255, 255, 255, var15).endVertex();
            }

            if (var14 == 2) {
               var6.vertex(1.0, -1.0, -1.0).uv(0.0F, 0.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(1.0, 1.0, -1.0).uv(0.0F, 1.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(-1.0, 1.0, -1.0).uv(1.0F, 1.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(-1.0, -1.0, -1.0).uv(1.0F, 0.0F).color(255, 255, 255, var15).endVertex();
            }

            if (var14 == 3) {
               var6.vertex(-1.0, -1.0, -1.0).uv(0.0F, 0.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(-1.0, 1.0, -1.0).uv(0.0F, 1.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(-1.0, 1.0, 1.0).uv(1.0F, 1.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(-1.0, -1.0, 1.0).uv(1.0F, 0.0F).color(255, 255, 255, var15).endVertex();
            }

            if (var14 == 4) {
               var6.vertex(-1.0, -1.0, -1.0).uv(0.0F, 0.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(-1.0, -1.0, 1.0).uv(0.0F, 1.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(1.0, -1.0, 1.0).uv(1.0F, 1.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(1.0, -1.0, -1.0).uv(1.0F, 0.0F).color(255, 255, 255, var15).endVertex();
            }

            if (var14 == 5) {
               var6.vertex(-1.0, 1.0, 1.0).uv(0.0F, 0.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(-1.0, 1.0, -1.0).uv(0.0F, 1.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(1.0, 1.0, -1.0).uv(1.0F, 1.0F).color(255, 255, 255, var15).endVertex();
               var6.vertex(1.0, 1.0, 1.0).uv(1.0F, 0.0F).color(255, 255, 255, var15).endVertex();
            }

            var5.end();
         }

         var8.popMatrix();
         RenderSystem.colorMask(true, true, true, false);
      }

      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.restoreProjectionMatrix();
      var8.popMatrix();
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
