package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class CubeMap {
   private final ResourceLocation[] images = new ResourceLocation[6];

   public CubeMap(ResourceLocation var1) {
      super();

      for(int var2 = 0; var2 < 6; ++var2) {
         this.images[var2] = new ResourceLocation(var1.getNamespace(), var1.getPath() + '_' + var2 + ".png");
      }

   }

   public void render(Minecraft var1, float var2, float var3, float var4) {
      Tesselator var5 = Tesselator.getInstance();
      BufferBuilder var6 = var5.getBuilder();
      RenderSystem.matrixMode(5889);
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      RenderSystem.multMatrix(Matrix4f.perspective(85.0D, (float)var1.getWindow().getWidth() / (float)var1.getWindow().getHeight(), 0.05F, 10.0F));
      RenderSystem.matrixMode(5888);
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      RenderSystem.enableBlend();
      RenderSystem.disableAlphaTest();
      RenderSystem.disableCull();
      RenderSystem.depthMask(false);
      RenderSystem.defaultBlendFunc();
      boolean var7 = true;

      for(int var8 = 0; var8 < 4; ++var8) {
         RenderSystem.pushMatrix();
         float var9 = ((float)(var8 % 2) / 2.0F - 0.5F) / 256.0F;
         float var10 = ((float)(var8 / 2) / 2.0F - 0.5F) / 256.0F;
         float var11 = 0.0F;
         RenderSystem.translatef(var9, var10, 0.0F);
         RenderSystem.rotatef(var2, 1.0F, 0.0F, 0.0F);
         RenderSystem.rotatef(var3, 0.0F, 1.0F, 0.0F);

         for(int var12 = 0; var12 < 6; ++var12) {
            var1.getTextureManager().bind(this.images[var12]);
            var6.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            int var13 = Math.round(255.0F * var4) / (var8 + 1);
            if (var12 == 0) {
               var6.vertex(-1.0D, -1.0D, 1.0D).uv(0.0F, 0.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, 1.0D, 1.0D).uv(0.0F, 1.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, 1.0D, 1.0D).uv(1.0F, 1.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, -1.0D, 1.0D).uv(1.0F, 0.0F).color(255, 255, 255, var13).endVertex();
            }

            if (var12 == 1) {
               var6.vertex(1.0D, -1.0D, 1.0D).uv(0.0F, 0.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, 1.0D, 1.0D).uv(0.0F, 1.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, 1.0D, -1.0D).uv(1.0F, 1.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, -1.0D, -1.0D).uv(1.0F, 0.0F).color(255, 255, 255, var13).endVertex();
            }

            if (var12 == 2) {
               var6.vertex(1.0D, -1.0D, -1.0D).uv(0.0F, 0.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, 1.0D, -1.0D).uv(0.0F, 1.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, 1.0D, -1.0D).uv(1.0F, 1.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, -1.0D, -1.0D).uv(1.0F, 0.0F).color(255, 255, 255, var13).endVertex();
            }

            if (var12 == 3) {
               var6.vertex(-1.0D, -1.0D, -1.0D).uv(0.0F, 0.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, 1.0D, -1.0D).uv(0.0F, 1.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, 1.0D, 1.0D).uv(1.0F, 1.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, -1.0D, 1.0D).uv(1.0F, 0.0F).color(255, 255, 255, var13).endVertex();
            }

            if (var12 == 4) {
               var6.vertex(-1.0D, -1.0D, -1.0D).uv(0.0F, 0.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, -1.0D, 1.0D).uv(0.0F, 1.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, -1.0D, 1.0D).uv(1.0F, 1.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, -1.0D, -1.0D).uv(1.0F, 0.0F).color(255, 255, 255, var13).endVertex();
            }

            if (var12 == 5) {
               var6.vertex(-1.0D, 1.0D, 1.0D).uv(0.0F, 0.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, 1.0D, -1.0D).uv(0.0F, 1.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, 1.0D, -1.0D).uv(1.0F, 1.0F).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, 1.0D, 1.0D).uv(1.0F, 0.0F).color(255, 255, 255, var13).endVertex();
            }

            var5.end();
         }

         RenderSystem.popMatrix();
         RenderSystem.colorMask(true, true, true, false);
      }

      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.matrixMode(5889);
      RenderSystem.popMatrix();
      RenderSystem.matrixMode(5888);
      RenderSystem.popMatrix();
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
