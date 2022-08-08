package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

public class ChunkBorderRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private static final int CELL_BORDER = FastColor.ARGB32.color(255, 0, 155, 155);
   private static final int YELLOW = FastColor.ARGB32.color(255, 255, 255, 0);

   public ChunkBorderRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      RenderSystem.enableDepthTest();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
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
      var11.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

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
         var22 = var21 % 4 == 0 ? CELL_BORDER : YELLOW;
         var11.vertex(var17 + (double)var21, var12, var19).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var17 + (double)var21, var12, var19).color(var22).endVertex();
         var11.vertex(var17 + (double)var21, var14, var19).color(var22).endVertex();
         var11.vertex(var17 + (double)var21, var14, var19).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var17 + (double)var21, var12, var19 + 16.0).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var17 + (double)var21, var12, var19 + 16.0).color(var22).endVertex();
         var11.vertex(var17 + (double)var21, var14, var19 + 16.0).color(var22).endVertex();
         var11.vertex(var17 + (double)var21, var14, var19 + 16.0).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      for(var21 = 2; var21 < 16; var21 += 2) {
         var22 = var21 % 4 == 0 ? CELL_BORDER : YELLOW;
         var11.vertex(var17, var12, var19 + (double)var21).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var17, var12, var19 + (double)var21).color(var22).endVertex();
         var11.vertex(var17, var14, var19 + (double)var21).color(var22).endVertex();
         var11.vertex(var17, var14, var19 + (double)var21).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var17 + 16.0, var12, var19 + (double)var21).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var17 + 16.0, var12, var19 + (double)var21).color(var22).endVertex();
         var11.vertex(var17 + 16.0, var14, var19 + (double)var21).color(var22).endVertex();
         var11.vertex(var17 + 16.0, var14, var19 + (double)var21).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      double var25;
      for(var21 = this.minecraft.level.getMinBuildHeight(); var21 <= this.minecraft.level.getMaxBuildHeight(); var21 += 2) {
         var25 = (double)var21 - var5;
         int var24 = var21 % 8 == 0 ? CELL_BORDER : YELLOW;
         var11.vertex(var17, var25, var19).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var11.vertex(var17, var25, var19).color(var24).endVertex();
         var11.vertex(var17, var25, var19 + 16.0).color(var24).endVertex();
         var11.vertex(var17 + 16.0, var25, var19 + 16.0).color(var24).endVertex();
         var11.vertex(var17 + 16.0, var25, var19).color(var24).endVertex();
         var11.vertex(var17, var25, var19).color(var24).endVertex();
         var11.vertex(var17, var25, var19).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      var10.end();
      RenderSystem.lineWidth(2.0F);
      var11.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

      for(var21 = 0; var21 <= 16; var21 += 16) {
         for(var22 = 0; var22 <= 16; var22 += 16) {
            var11.vertex(var17 + (double)var21, var12, var19 + (double)var22).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
            var11.vertex(var17 + (double)var21, var12, var19 + (double)var22).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            var11.vertex(var17 + (double)var21, var14, var19 + (double)var22).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            var11.vertex(var17 + (double)var21, var14, var19 + (double)var22).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
         }
      }

      for(var21 = this.minecraft.level.getMinBuildHeight(); var21 <= this.minecraft.level.getMaxBuildHeight(); var21 += 16) {
         var25 = (double)var21 - var5;
         var11.vertex(var17, var25, var19).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
         var11.vertex(var17, var25, var19).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var17, var25, var19 + 16.0).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var17 + 16.0, var25, var19 + 16.0).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var17 + 16.0, var25, var19).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var17, var25, var19).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var11.vertex(var17, var25, var19).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
      }

      var10.end();
      RenderSystem.lineWidth(1.0F);
      RenderSystem.enableBlend();
      RenderSystem.enableTexture();
   }
}
