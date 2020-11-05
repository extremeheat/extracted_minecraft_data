package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

public class ChunkBorderRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public ChunkBorderRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      RenderSystem.enableDepthTest();
      RenderSystem.shadeModel(7425);
      RenderSystem.enableAlphaTest();
      RenderSystem.defaultAlphaFunc();
      Entity var9 = this.minecraft.gameRenderer.getMainCamera().getEntity();
      Tesselator var10 = Tesselator.getInstance();
      BufferBuilder var11 = var10.getBuilder();
      double var12 = 0.0D - var5;
      double var14 = 256.0D - var5;
      RenderSystem.disableTexture();
      RenderSystem.disableBlend();
      double var16 = (double)(var9.xChunk << 4) - var3;
      double var18 = (double)(var9.zChunk << 4) - var7;
      RenderSystem.lineWidth(1.0F);
      var11.begin(3, DefaultVertexFormat.POSITION_COLOR);

      int var20;
      int var21;
      for(var20 = -16; var20 <= 32; var20 += 16) {
         for(var21 = -16; var21 <= 32; var21 += 16) {
            var11.vertex(var16 + (double)var20, var12, var18 + (double)var21).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
            var11.vertex(var16 + (double)var20, var12, var18 + (double)var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
            var11.vertex(var16 + (double)var20, var14, var18 + (double)var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
            var11.vertex(var16 + (double)var20, var14, var18 + (double)var21).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
         }
      }

      for(var20 = 2; var20 < 16; var20 += 2) {
         var11.vertex(var16 + (double)var20, var12, var18).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var16 + (double)var20, var12, var18).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var16 + (double)var20, var14, var18).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var16 + (double)var20, var14, var18).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var16 + (double)var20, var12, var18 + 16.0D).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var16 + (double)var20, var12, var18 + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var16 + (double)var20, var14, var18 + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var16 + (double)var20, var14, var18 + 16.0D).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      for(var20 = 2; var20 < 16; var20 += 2) {
         var11.vertex(var16, var12, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var16, var12, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var16, var14, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var16, var14, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var16 + 16.0D, var12, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var16 + 16.0D, var12, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var16 + 16.0D, var14, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var16 + 16.0D, var14, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      double var23;
      for(var20 = 0; var20 <= 256; var20 += 2) {
         var23 = (double)var20 - var5;
         var11.vertex(var16, var23, var18).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var16, var23, var18).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var16, var23, var18 + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var16 + 16.0D, var23, var18 + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var16 + 16.0D, var23, var18).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var16, var23, var18).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var16, var23, var18).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      var10.end();
      RenderSystem.lineWidth(2.0F);
      var11.begin(3, DefaultVertexFormat.POSITION_COLOR);

      for(var20 = 0; var20 <= 16; var20 += 16) {
         for(var21 = 0; var21 <= 16; var21 += 16) {
            var11.vertex(var16 + (double)var20, var12, var18 + (double)var21).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
            var11.vertex(var16 + (double)var20, var12, var18 + (double)var21).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            var11.vertex(var16 + (double)var20, var14, var18 + (double)var21).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            var11.vertex(var16 + (double)var20, var14, var18 + (double)var21).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
         }
      }

      for(var20 = 0; var20 <= 256; var20 += 16) {
         var23 = (double)var20 - var5;
         var11.vertex(var16, var23, var18).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
         var11.vertex(var16, var23, var18).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var16, var23, var18 + 16.0D).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var16 + 16.0D, var23, var18 + 16.0D).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var16 + 16.0D, var23, var18).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var16, var23, var18).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var16, var23, var18).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
      }

      var10.end();
      RenderSystem.lineWidth(1.0F);
      RenderSystem.enableBlend();
      RenderSystem.enableTexture();
      RenderSystem.shadeModel(7424);
   }
}
