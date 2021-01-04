package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

public class ChunkBorderRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public ChunkBorderRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(long var1) {
      Camera var3 = this.minecraft.gameRenderer.getMainCamera();
      Tesselator var4 = Tesselator.getInstance();
      BufferBuilder var5 = var4.getBuilder();
      double var6 = var3.getPosition().x;
      double var8 = var3.getPosition().y;
      double var10 = var3.getPosition().z;
      double var12 = 0.0D - var8;
      double var14 = 256.0D - var8;
      GlStateManager.disableTexture();
      GlStateManager.disableBlend();
      double var16 = (double)(var3.getEntity().xChunk << 4) - var6;
      double var18 = (double)(var3.getEntity().zChunk << 4) - var10;
      GlStateManager.lineWidth(1.0F);
      var5.begin(3, DefaultVertexFormat.POSITION_COLOR);

      int var20;
      int var21;
      for(var20 = -16; var20 <= 32; var20 += 16) {
         for(var21 = -16; var21 <= 32; var21 += 16) {
            var5.vertex(var16 + (double)var20, var12, var18 + (double)var21).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
            var5.vertex(var16 + (double)var20, var12, var18 + (double)var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
            var5.vertex(var16 + (double)var20, var14, var18 + (double)var21).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
            var5.vertex(var16 + (double)var20, var14, var18 + (double)var21).color(1.0F, 0.0F, 0.0F, 0.0F).endVertex();
         }
      }

      for(var20 = 2; var20 < 16; var20 += 2) {
         var5.vertex(var16 + (double)var20, var12, var18).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var5.vertex(var16 + (double)var20, var12, var18).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var5.vertex(var16 + (double)var20, var14, var18).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var5.vertex(var16 + (double)var20, var14, var18).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var5.vertex(var16 + (double)var20, var12, var18 + 16.0D).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var5.vertex(var16 + (double)var20, var12, var18 + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var5.vertex(var16 + (double)var20, var14, var18 + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var5.vertex(var16 + (double)var20, var14, var18 + 16.0D).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      for(var20 = 2; var20 < 16; var20 += 2) {
         var5.vertex(var16, var12, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var5.vertex(var16, var12, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var5.vertex(var16, var14, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var5.vertex(var16, var14, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var5.vertex(var16 + 16.0D, var12, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var5.vertex(var16 + 16.0D, var12, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var5.vertex(var16 + 16.0D, var14, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var5.vertex(var16 + 16.0D, var14, var18 + (double)var20).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      double var23;
      for(var20 = 0; var20 <= 256; var20 += 2) {
         var23 = (double)var20 - var8;
         var5.vertex(var16, var23, var18).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
         var5.vertex(var16, var23, var18).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var5.vertex(var16, var23, var18 + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var5.vertex(var16 + 16.0D, var23, var18 + 16.0D).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var5.vertex(var16 + 16.0D, var23, var18).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var5.vertex(var16, var23, var18).color(1.0F, 1.0F, 0.0F, 1.0F).endVertex();
         var5.vertex(var16, var23, var18).color(1.0F, 1.0F, 0.0F, 0.0F).endVertex();
      }

      var4.end();
      GlStateManager.lineWidth(2.0F);
      var5.begin(3, DefaultVertexFormat.POSITION_COLOR);

      for(var20 = 0; var20 <= 16; var20 += 16) {
         for(var21 = 0; var21 <= 16; var21 += 16) {
            var5.vertex(var16 + (double)var20, var12, var18 + (double)var21).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
            var5.vertex(var16 + (double)var20, var12, var18 + (double)var21).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            var5.vertex(var16 + (double)var20, var14, var18 + (double)var21).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
            var5.vertex(var16 + (double)var20, var14, var18 + (double)var21).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
         }
      }

      for(var20 = 0; var20 <= 256; var20 += 16) {
         var23 = (double)var20 - var8;
         var5.vertex(var16, var23, var18).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
         var5.vertex(var16, var23, var18).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var5.vertex(var16, var23, var18 + 16.0D).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var5.vertex(var16 + 16.0D, var23, var18 + 16.0D).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var5.vertex(var16 + 16.0D, var23, var18).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var5.vertex(var16, var23, var18).color(0.25F, 0.25F, 1.0F, 1.0F).endVertex();
         var5.vertex(var16, var23, var18).color(0.25F, 0.25F, 1.0F, 0.0F).endVertex();
      }

      var4.end();
      GlStateManager.lineWidth(1.0F);
      GlStateManager.enableBlend();
      GlStateManager.enableTexture();
   }
}
