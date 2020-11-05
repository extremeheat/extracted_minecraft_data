package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

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
      double var12 = (double)this.minecraft.level.getMinBuildHeight() - var5;
      double var14 = (double)this.minecraft.level.getMaxBuildHeight() - var5;
      RenderSystem.disableTexture();
      RenderSystem.disableBlend();
      ChunkPos var16 = var9.chunkPosition();
      double var17 = (double)var16.getMinBlockX() - var3;
      double var19 = (double)var16.getMinBlockZ() - var7;
      RenderSystem.lineWidth(1.0F);
      var11.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

      int var21;
      int var22;
      for(var21 = -16; var21 <= 32; var21 += 16) {
         for(var22 = -16; var22 <= 32; var22 += 16) {
            var11.vertex(var17 + (double)var21, var12, var19 + (double)var22).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
            var11.vertex(var17 + (double)var21, var12, var19 + (double)var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
            var11.vertex(var17 + (double)var21, var14, var19 + (double)var22).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
            var11.vertex(var17 + (double)var21, var14, var19 + (double)var22).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
         }
      }

      for(var21 = 2; var21 < 16; var21 += 2) {
         var11.vertex(var17 + (double)var21, var12, var19).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var17 + (double)var21, var12, var19).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var17 + (double)var21, var14, var19).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var17 + (double)var21, var14, var19).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var17 + (double)var21, var12, var19 + 16.0D).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var17 + (double)var21, var12, var19 + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var17 + (double)var21, var14, var19 + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var17 + (double)var21, var14, var19 + 16.0D).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      for(var21 = 2; var21 < 16; var21 += 2) {
         var11.vertex(var17, var12, var19 + (double)var21).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var17, var12, var19 + (double)var21).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var17, var14, var19 + (double)var21).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var17, var14, var19 + (double)var21).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var17 + 16.0D, var12, var19 + (double)var21).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var17 + 16.0D, var12, var19 + (double)var21).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var17 + 16.0D, var14, var19 + (double)var21).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var17 + 16.0D, var14, var19 + (double)var21).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      double var24;
      for(var21 = this.minecraft.level.getMinBuildHeight(); var21 <= this.minecraft.level.getMaxBuildHeight(); var21 += 2) {
         var24 = (double)var21 - var5;
         var11.vertex(var17, var24, var19).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var17, var24, var19).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var17, var24, var19 + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var17 + 16.0D, var24, var19 + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var17 + 16.0D, var24, var19).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var17, var24, var19).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var11.vertex(var17, var24, var19).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      var10.end();
      RenderSystem.lineWidth(2.0F);
      var11.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

      for(var21 = 0; var21 <= 16; var21 += 16) {
         for(var22 = 0; var22 <= 16; var22 += 16) {
            var11.vertex(var17 + (double)var21, var12, var19 + (double)var22).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
            var11.vertex(var17 + (double)var21, var12, var19 + (double)var22).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            var11.vertex(var17 + (double)var21, var14, var19 + (double)var22).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            var11.vertex(var17 + (double)var21, var14, var19 + (double)var22).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
         }
      }

      for(var21 = this.minecraft.level.getMinBuildHeight(); var21 <= this.minecraft.level.getMaxBuildHeight(); var21 += 16) {
         var24 = (double)var21 - var5;
         var11.vertex(var17, var24, var19).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
         var11.vertex(var17, var24, var19).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var17, var24, var19 + 16.0D).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var17 + 16.0D, var24, var19 + 16.0D).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var17 + 16.0D, var24, var19).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var17, var24, var19).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var17, var24, var19).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
      }

      var10.end();
      RenderSystem.lineWidth(1.0F);
      RenderSystem.enableBlend();
      RenderSystem.enableTexture();
      RenderSystem.shadeModel(7424);
   }
}
