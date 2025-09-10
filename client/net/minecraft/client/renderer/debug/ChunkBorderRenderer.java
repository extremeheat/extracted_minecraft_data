package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.joml.Matrix4f;

public class ChunkBorderRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private static final int CELL_BORDER = ARGB.color(255, 0, 155, 155);
   private static final int YELLOW = ARGB.color(255, 255, 255, 0);

   public ChunkBorderRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9) {
      Entity var10 = this.minecraft.gameRenderer.getMainCamera().getEntity();
      float var11 = (float)((double)this.minecraft.level.getMinY() - var5);
      float var12 = (float)((double)(this.minecraft.level.getMaxY() + 1) - var5);
      ChunkPos var13 = var10.chunkPosition();
      float var14 = (float)((double)var13.getMinBlockX() - var3);
      float var15 = (float)((double)var13.getMinBlockZ() - var7);
      VertexConsumer var16 = var2.getBuffer(RenderType.debugLineStrip(1.0));
      Matrix4f var17 = var1.last().pose();

      for(int var18 = -16; var18 <= 32; var18 += 16) {
         for(int var19 = -16; var19 <= 32; var19 += 16) {
            var16.addVertex(var17, var14 + (float)var18, var11, var15 + (float)var19).setColor(1.0F, 0.0F, 0.0F, 0.0F);
            var16.addVertex(var17, var14 + (float)var18, var11, var15 + (float)var19).setColor(1.0F, 0.0F, 0.0F, 0.5F);
            var16.addVertex(var17, var14 + (float)var18, var12, var15 + (float)var19).setColor(1.0F, 0.0F, 0.0F, 0.5F);
            var16.addVertex(var17, var14 + (float)var18, var12, var15 + (float)var19).setColor(1.0F, 0.0F, 0.0F, 0.0F);
         }
      }

      for(int var22 = 2; var22 < 16; var22 += 2) {
         int var27 = var22 % 4 == 0 ? CELL_BORDER : YELLOW;
         var16.addVertex(var17, var14 + (float)var22, var11, var15).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var16.addVertex(var17, var14 + (float)var22, var11, var15).setColor(var27);
         var16.addVertex(var17, var14 + (float)var22, var12, var15).setColor(var27);
         var16.addVertex(var17, var14 + (float)var22, var12, var15).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var16.addVertex(var17, var14 + (float)var22, var11, var15 + 16.0F).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var16.addVertex(var17, var14 + (float)var22, var11, var15 + 16.0F).setColor(var27);
         var16.addVertex(var17, var14 + (float)var22, var12, var15 + 16.0F).setColor(var27);
         var16.addVertex(var17, var14 + (float)var22, var12, var15 + 16.0F).setColor(1.0F, 1.0F, 0.0F, 0.0F);
      }

      for(int var23 = 2; var23 < 16; var23 += 2) {
         int var28 = var23 % 4 == 0 ? CELL_BORDER : YELLOW;
         var16.addVertex(var17, var14, var11, var15 + (float)var23).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var16.addVertex(var17, var14, var11, var15 + (float)var23).setColor(var28);
         var16.addVertex(var17, var14, var12, var15 + (float)var23).setColor(var28);
         var16.addVertex(var17, var14, var12, var15 + (float)var23).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var16.addVertex(var17, var14 + 16.0F, var11, var15 + (float)var23).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var16.addVertex(var17, var14 + 16.0F, var11, var15 + (float)var23).setColor(var28);
         var16.addVertex(var17, var14 + 16.0F, var12, var15 + (float)var23).setColor(var28);
         var16.addVertex(var17, var14 + 16.0F, var12, var15 + (float)var23).setColor(1.0F, 1.0F, 0.0F, 0.0F);
      }

      for(int var24 = this.minecraft.level.getMinY(); var24 <= this.minecraft.level.getMaxY() + 1; var24 += 2) {
         float var29 = (float)((double)var24 - var5);
         int var20 = var24 % 8 == 0 ? CELL_BORDER : YELLOW;
         var16.addVertex(var17, var14, var29, var15).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var16.addVertex(var17, var14, var29, var15).setColor(var20);
         var16.addVertex(var17, var14, var29, var15 + 16.0F).setColor(var20);
         var16.addVertex(var17, var14 + 16.0F, var29, var15 + 16.0F).setColor(var20);
         var16.addVertex(var17, var14 + 16.0F, var29, var15).setColor(var20);
         var16.addVertex(var17, var14, var29, var15).setColor(var20);
         var16.addVertex(var17, var14, var29, var15).setColor(1.0F, 1.0F, 0.0F, 0.0F);
      }

      var16 = var2.getBuffer(RenderType.debugLineStrip(2.0));

      for(int var25 = 0; var25 <= 16; var25 += 16) {
         for(int var30 = 0; var30 <= 16; var30 += 16) {
            var16.addVertex(var17, var14 + (float)var25, var11, var15 + (float)var30).setColor(0.25F, 0.25F, 1.0F, 0.0F);
            var16.addVertex(var17, var14 + (float)var25, var11, var15 + (float)var30).setColor(0.25F, 0.25F, 1.0F, 1.0F);
            var16.addVertex(var17, var14 + (float)var25, var12, var15 + (float)var30).setColor(0.25F, 0.25F, 1.0F, 1.0F);
            var16.addVertex(var17, var14 + (float)var25, var12, var15 + (float)var30).setColor(0.25F, 0.25F, 1.0F, 0.0F);
         }
      }

      for(int var26 = this.minecraft.level.getMinY(); var26 <= this.minecraft.level.getMaxY() + 1; var26 += 16) {
         float var31 = (float)((double)var26 - var5);
         var16.addVertex(var17, var14, var31, var15).setColor(0.25F, 0.25F, 1.0F, 0.0F);
         var16.addVertex(var17, var14, var31, var15).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var16.addVertex(var17, var14, var31, var15 + 16.0F).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var16.addVertex(var17, var14 + 16.0F, var31, var15 + 16.0F).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var16.addVertex(var17, var14 + 16.0F, var31, var15).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var16.addVertex(var17, var14, var31, var15).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var16.addVertex(var17, var14, var31, var15).setColor(0.25F, 0.25F, 1.0F, 0.0F);
      }

   }
}
