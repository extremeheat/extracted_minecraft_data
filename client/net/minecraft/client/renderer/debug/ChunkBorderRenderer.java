package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
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

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9, Frustum var10) {
      Entity var11 = this.minecraft.gameRenderer.getMainCamera().getEntity();
      float var12 = (float)((double)this.minecraft.level.getMinY() - var5);
      float var13 = (float)((double)(this.minecraft.level.getMaxY() + 1) - var5);
      ChunkPos var14 = var11.chunkPosition();
      float var15 = (float)((double)var14.getMinBlockX() - var3);
      float var16 = (float)((double)var14.getMinBlockZ() - var7);
      VertexConsumer var17 = var2.getBuffer(RenderType.debugLineStrip(1.0));
      Matrix4f var18 = var1.last().pose();

      for(int var19 = -16; var19 <= 32; var19 += 16) {
         for(int var20 = -16; var20 <= 32; var20 += 16) {
            var17.addVertex(var18, var15 + (float)var19, var12, var16 + (float)var20).setColor(1.0F, 0.0F, 0.0F, 0.0F);
            var17.addVertex(var18, var15 + (float)var19, var12, var16 + (float)var20).setColor(1.0F, 0.0F, 0.0F, 0.5F);
            var17.addVertex(var18, var15 + (float)var19, var13, var16 + (float)var20).setColor(1.0F, 0.0F, 0.0F, 0.5F);
            var17.addVertex(var18, var15 + (float)var19, var13, var16 + (float)var20).setColor(1.0F, 0.0F, 0.0F, 0.0F);
         }
      }

      for(int var23 = 2; var23 < 16; var23 += 2) {
         int var28 = var23 % 4 == 0 ? CELL_BORDER : YELLOW;
         var17.addVertex(var18, var15 + (float)var23, var12, var16).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var17.addVertex(var18, var15 + (float)var23, var12, var16).setColor(var28);
         var17.addVertex(var18, var15 + (float)var23, var13, var16).setColor(var28);
         var17.addVertex(var18, var15 + (float)var23, var13, var16).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var17.addVertex(var18, var15 + (float)var23, var12, var16 + 16.0F).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var17.addVertex(var18, var15 + (float)var23, var12, var16 + 16.0F).setColor(var28);
         var17.addVertex(var18, var15 + (float)var23, var13, var16 + 16.0F).setColor(var28);
         var17.addVertex(var18, var15 + (float)var23, var13, var16 + 16.0F).setColor(1.0F, 1.0F, 0.0F, 0.0F);
      }

      for(int var24 = 2; var24 < 16; var24 += 2) {
         int var29 = var24 % 4 == 0 ? CELL_BORDER : YELLOW;
         var17.addVertex(var18, var15, var12, var16 + (float)var24).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var17.addVertex(var18, var15, var12, var16 + (float)var24).setColor(var29);
         var17.addVertex(var18, var15, var13, var16 + (float)var24).setColor(var29);
         var17.addVertex(var18, var15, var13, var16 + (float)var24).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var17.addVertex(var18, var15 + 16.0F, var12, var16 + (float)var24).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var17.addVertex(var18, var15 + 16.0F, var12, var16 + (float)var24).setColor(var29);
         var17.addVertex(var18, var15 + 16.0F, var13, var16 + (float)var24).setColor(var29);
         var17.addVertex(var18, var15 + 16.0F, var13, var16 + (float)var24).setColor(1.0F, 1.0F, 0.0F, 0.0F);
      }

      for(int var25 = this.minecraft.level.getMinY(); var25 <= this.minecraft.level.getMaxY() + 1; var25 += 2) {
         float var30 = (float)((double)var25 - var5);
         int var21 = var25 % 8 == 0 ? CELL_BORDER : YELLOW;
         var17.addVertex(var18, var15, var30, var16).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var17.addVertex(var18, var15, var30, var16).setColor(var21);
         var17.addVertex(var18, var15, var30, var16 + 16.0F).setColor(var21);
         var17.addVertex(var18, var15 + 16.0F, var30, var16 + 16.0F).setColor(var21);
         var17.addVertex(var18, var15 + 16.0F, var30, var16).setColor(var21);
         var17.addVertex(var18, var15, var30, var16).setColor(var21);
         var17.addVertex(var18, var15, var30, var16).setColor(1.0F, 1.0F, 0.0F, 0.0F);
      }

      var17 = var2.getBuffer(RenderType.debugLineStrip(2.0));

      for(int var26 = 0; var26 <= 16; var26 += 16) {
         for(int var31 = 0; var31 <= 16; var31 += 16) {
            var17.addVertex(var18, var15 + (float)var26, var12, var16 + (float)var31).setColor(0.25F, 0.25F, 1.0F, 0.0F);
            var17.addVertex(var18, var15 + (float)var26, var12, var16 + (float)var31).setColor(0.25F, 0.25F, 1.0F, 1.0F);
            var17.addVertex(var18, var15 + (float)var26, var13, var16 + (float)var31).setColor(0.25F, 0.25F, 1.0F, 1.0F);
            var17.addVertex(var18, var15 + (float)var26, var13, var16 + (float)var31).setColor(0.25F, 0.25F, 1.0F, 0.0F);
         }
      }

      for(int var27 = this.minecraft.level.getMinY(); var27 <= this.minecraft.level.getMaxY() + 1; var27 += 16) {
         float var32 = (float)((double)var27 - var5);
         var17.addVertex(var18, var15, var32, var16).setColor(0.25F, 0.25F, 1.0F, 0.0F);
         var17.addVertex(var18, var15, var32, var16).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var17.addVertex(var18, var15, var32, var16 + 16.0F).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var17.addVertex(var18, var15 + 16.0F, var32, var16 + 16.0F).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var17.addVertex(var18, var15 + 16.0F, var32, var16).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var17.addVertex(var18, var15, var32, var16).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var17.addVertex(var18, var15, var32, var16).setColor(0.25F, 0.25F, 1.0F, 0.0F);
      }

   }
}
