package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.joml.Matrix4f;

public class ChunkBorderRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private static final int CELL_BORDER = FastColor.ARGB32.color(255, 0, 155, 155);
   private static final int YELLOW = FastColor.ARGB32.color(255, 255, 255, 0);

   public ChunkBorderRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      Entity var9 = this.minecraft.gameRenderer.getMainCamera().getEntity();
      float var10 = (float)((double)this.minecraft.level.getMinBuildHeight() - var5);
      float var11 = (float)((double)this.minecraft.level.getMaxBuildHeight() - var5);
      ChunkPos var12 = var9.chunkPosition();
      float var13 = (float)((double)var12.getMinBlockX() - var3);
      float var14 = (float)((double)var12.getMinBlockZ() - var7);
      VertexConsumer var15 = var2.getBuffer(RenderType.debugLineStrip(1.0));
      Matrix4f var16 = var1.last().pose();

      int var17;
      int var18;
      for(var17 = -16; var17 <= 32; var17 += 16) {
         for(var18 = -16; var18 <= 32; var18 += 16) {
            var15.addVertex(var16, var13 + (float)var17, var10, var14 + (float)var18).setColor(1.0F, 0.0F, 0.0F, 0.0F);
            var15.addVertex(var16, var13 + (float)var17, var10, var14 + (float)var18).setColor(1.0F, 0.0F, 0.0F, 0.5F);
            var15.addVertex(var16, var13 + (float)var17, var11, var14 + (float)var18).setColor(1.0F, 0.0F, 0.0F, 0.5F);
            var15.addVertex(var16, var13 + (float)var17, var11, var14 + (float)var18).setColor(1.0F, 0.0F, 0.0F, 0.0F);
         }
      }

      for(var17 = 2; var17 < 16; var17 += 2) {
         var18 = var17 % 4 == 0 ? CELL_BORDER : YELLOW;
         var15.addVertex(var16, var13 + (float)var17, var10, var14).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var15.addVertex(var16, var13 + (float)var17, var10, var14).setColor(var18);
         var15.addVertex(var16, var13 + (float)var17, var11, var14).setColor(var18);
         var15.addVertex(var16, var13 + (float)var17, var11, var14).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var15.addVertex(var16, var13 + (float)var17, var10, var14 + 16.0F).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var15.addVertex(var16, var13 + (float)var17, var10, var14 + 16.0F).setColor(var18);
         var15.addVertex(var16, var13 + (float)var17, var11, var14 + 16.0F).setColor(var18);
         var15.addVertex(var16, var13 + (float)var17, var11, var14 + 16.0F).setColor(1.0F, 1.0F, 0.0F, 0.0F);
      }

      for(var17 = 2; var17 < 16; var17 += 2) {
         var18 = var17 % 4 == 0 ? CELL_BORDER : YELLOW;
         var15.addVertex(var16, var13, var10, var14 + (float)var17).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var15.addVertex(var16, var13, var10, var14 + (float)var17).setColor(var18);
         var15.addVertex(var16, var13, var11, var14 + (float)var17).setColor(var18);
         var15.addVertex(var16, var13, var11, var14 + (float)var17).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var15.addVertex(var16, var13 + 16.0F, var10, var14 + (float)var17).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var15.addVertex(var16, var13 + 16.0F, var10, var14 + (float)var17).setColor(var18);
         var15.addVertex(var16, var13 + 16.0F, var11, var14 + (float)var17).setColor(var18);
         var15.addVertex(var16, var13 + 16.0F, var11, var14 + (float)var17).setColor(1.0F, 1.0F, 0.0F, 0.0F);
      }

      float var20;
      for(var17 = this.minecraft.level.getMinBuildHeight(); var17 <= this.minecraft.level.getMaxBuildHeight(); var17 += 2) {
         var20 = (float)((double)var17 - var5);
         int var19 = var17 % 8 == 0 ? CELL_BORDER : YELLOW;
         var15.addVertex(var16, var13, var20, var14).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var15.addVertex(var16, var13, var20, var14).setColor(var19);
         var15.addVertex(var16, var13, var20, var14 + 16.0F).setColor(var19);
         var15.addVertex(var16, var13 + 16.0F, var20, var14 + 16.0F).setColor(var19);
         var15.addVertex(var16, var13 + 16.0F, var20, var14).setColor(var19);
         var15.addVertex(var16, var13, var20, var14).setColor(var19);
         var15.addVertex(var16, var13, var20, var14).setColor(1.0F, 1.0F, 0.0F, 0.0F);
      }

      var15 = var2.getBuffer(RenderType.debugLineStrip(2.0));

      for(var17 = 0; var17 <= 16; var17 += 16) {
         for(var18 = 0; var18 <= 16; var18 += 16) {
            var15.addVertex(var16, var13 + (float)var17, var10, var14 + (float)var18).setColor(0.25F, 0.25F, 1.0F, 0.0F);
            var15.addVertex(var16, var13 + (float)var17, var10, var14 + (float)var18).setColor(0.25F, 0.25F, 1.0F, 1.0F);
            var15.addVertex(var16, var13 + (float)var17, var11, var14 + (float)var18).setColor(0.25F, 0.25F, 1.0F, 1.0F);
            var15.addVertex(var16, var13 + (float)var17, var11, var14 + (float)var18).setColor(0.25F, 0.25F, 1.0F, 0.0F);
         }
      }

      for(var17 = this.minecraft.level.getMinBuildHeight(); var17 <= this.minecraft.level.getMaxBuildHeight(); var17 += 16) {
         var20 = (float)((double)var17 - var5);
         var15.addVertex(var16, var13, var20, var14).setColor(0.25F, 0.25F, 1.0F, 0.0F);
         var15.addVertex(var16, var13, var20, var14).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var15.addVertex(var16, var13, var20, var14 + 16.0F).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var15.addVertex(var16, var13 + 16.0F, var20, var14 + 16.0F).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var15.addVertex(var16, var13 + 16.0F, var20, var14).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var15.addVertex(var16, var13, var20, var14).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var15.addVertex(var16, var13, var20, var14).setColor(0.25F, 0.25F, 1.0F, 0.0F);
      }

   }
}
