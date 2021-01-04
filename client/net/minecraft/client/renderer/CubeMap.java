package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
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
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.multMatrix(Matrix4f.perspective(85.0D, (float)var1.window.getWidth() / (float)var1.window.getHeight(), 0.05F, 10.0F));
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.enableBlend();
      GlStateManager.disableAlphaTest();
      GlStateManager.disableCull();
      GlStateManager.depthMask(false);
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      boolean var7 = true;

      for(int var8 = 0; var8 < 4; ++var8) {
         GlStateManager.pushMatrix();
         float var9 = ((float)(var8 % 2) / 2.0F - 0.5F) / 256.0F;
         float var10 = ((float)(var8 / 2) / 2.0F - 0.5F) / 256.0F;
         float var11 = 0.0F;
         GlStateManager.translatef(var9, var10, 0.0F);
         GlStateManager.rotatef(var2, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(var3, 0.0F, 1.0F, 0.0F);

         for(int var12 = 0; var12 < 6; ++var12) {
            var1.getTextureManager().bind(this.images[var12]);
            var6.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            int var13 = Math.round(255.0F * var4) / (var8 + 1);
            if (var12 == 0) {
               var6.vertex(-1.0D, -1.0D, 1.0D).uv(0.0D, 0.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, 1.0D, 1.0D).uv(0.0D, 1.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, 1.0D, 1.0D).uv(1.0D, 1.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, -1.0D, 1.0D).uv(1.0D, 0.0D).color(255, 255, 255, var13).endVertex();
            }

            if (var12 == 1) {
               var6.vertex(1.0D, -1.0D, 1.0D).uv(0.0D, 0.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, 1.0D, 1.0D).uv(0.0D, 1.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, 1.0D, -1.0D).uv(1.0D, 1.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, -1.0D, -1.0D).uv(1.0D, 0.0D).color(255, 255, 255, var13).endVertex();
            }

            if (var12 == 2) {
               var6.vertex(1.0D, -1.0D, -1.0D).uv(0.0D, 0.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, 1.0D, -1.0D).uv(0.0D, 1.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, 1.0D, -1.0D).uv(1.0D, 1.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, -1.0D, -1.0D).uv(1.0D, 0.0D).color(255, 255, 255, var13).endVertex();
            }

            if (var12 == 3) {
               var6.vertex(-1.0D, -1.0D, -1.0D).uv(0.0D, 0.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, 1.0D, -1.0D).uv(0.0D, 1.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, 1.0D, 1.0D).uv(1.0D, 1.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, -1.0D, 1.0D).uv(1.0D, 0.0D).color(255, 255, 255, var13).endVertex();
            }

            if (var12 == 4) {
               var6.vertex(-1.0D, -1.0D, -1.0D).uv(0.0D, 0.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, -1.0D, 1.0D).uv(0.0D, 1.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, -1.0D, 1.0D).uv(1.0D, 1.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, -1.0D, -1.0D).uv(1.0D, 0.0D).color(255, 255, 255, var13).endVertex();
            }

            if (var12 == 5) {
               var6.vertex(-1.0D, 1.0D, 1.0D).uv(0.0D, 0.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(-1.0D, 1.0D, -1.0D).uv(0.0D, 1.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, 1.0D, -1.0D).uv(1.0D, 1.0D).color(255, 255, 255, var13).endVertex();
               var6.vertex(1.0D, 1.0D, 1.0D).uv(1.0D, 0.0D).color(255, 255, 255, var13).endVertex();
            }

            var5.end();
         }

         GlStateManager.popMatrix();
         GlStateManager.colorMask(true, true, true, false);
      }

      var6.offset(0.0D, 0.0D, 0.0D);
      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.matrixMode(5889);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.enableDepthTest();
   }

   public CompletableFuture<Void> preload(TextureManager var1, Executor var2) {
      CompletableFuture[] var3 = new CompletableFuture[6];

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var3[var4] = var1.preload(this.images[var4], var2);
      }

      return CompletableFuture.allOf(var3);
   }
}
