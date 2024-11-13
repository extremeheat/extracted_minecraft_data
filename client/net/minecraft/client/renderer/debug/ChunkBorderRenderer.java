package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;
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

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      Entity var9 = this.minecraft.gameRenderer.getMainCamera().getEntity();
      float var10 = (float)((double)this.minecraft.level.getMinY() - var5);
      float var11 = (float)((double)(this.minecraft.level.getMaxY() + 1) - var5);
      ChunkPos var12 = var9.chunkPosition();
      float var13 = (float)((double)var12.getMinBlockX() - var3);
      float var14 = (float)((double)var12.getMinBlockZ() - var7);
      VertexConsumer var15 = var2.getBuffer(RenderType.debugLineStrip(1.0));
      Matrix4f var16 = var1.last().pose();

      for(int var17 = -16; var17 <= 32; var17 += 16) {
         for(int var18 = -16; var18 <= 32; var18 += 16) {
            var15.addVertex(var16, var13 + (float)var17, var10, var14 + (float)var18).setColor(1.0F, 0.0F, 0.0F, 0.0F);
            var15.addVertex(var16, var13 + (float)var17, var10, var14 + (float)var18).setColor(1.0F, 0.0F, 0.0F, 0.5F);
            var15.addVertex(var16, var13 + (float)var17, var11, var14 + (float)var18).setColor(1.0F, 0.0F, 0.0F, 0.5F);
            var15.addVertex(var16, var13 + (float)var17, var11, var14 + (float)var18).setColor(1.0F, 0.0F, 0.0F, 0.0F);
         }
      }

      for(int var21 = 2; var21 < 16; var21 += 2) {
         int var26 = var21 % 4 == 0 ? CELL_BORDER : YELLOW;
         var15.addVertex(var16, var13 + (float)var21, var10, var14).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var15.addVertex(var16, var13 + (float)var21, var10, var14).setColor(var26);
         var15.addVertex(var16, var13 + (float)var21, var11, var14).setColor(var26);
         var15.addVertex(var16, var13 + (float)var21, var11, var14).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var15.addVertex(var16, var13 + (float)var21, var10, var14 + 16.0F).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var15.addVertex(var16, var13 + (float)var21, var10, var14 + 16.0F).setColor(var26);
         var15.addVertex(var16, var13 + (float)var21, var11, var14 + 16.0F).setColor(var26);
         var15.addVertex(var16, var13 + (float)var21, var11, var14 + 16.0F).setColor(1.0F, 1.0F, 0.0F, 0.0F);
      }

      for(int var22 = 2; var22 < 16; var22 += 2) {
         int var27 = var22 % 4 == 0 ? CELL_BORDER : YELLOW;
         var15.addVertex(var16, var13, var10, var14 + (float)var22).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var15.addVertex(var16, var13, var10, var14 + (float)var22).setColor(var27);
         var15.addVertex(var16, var13, var11, var14 + (float)var22).setColor(var27);
         var15.addVertex(var16, var13, var11, var14 + (float)var22).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var15.addVertex(var16, var13 + 16.0F, var10, var14 + (float)var22).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var15.addVertex(var16, var13 + 16.0F, var10, var14 + (float)var22).setColor(var27);
         var15.addVertex(var16, var13 + 16.0F, var11, var14 + (float)var22).setColor(var27);
         var15.addVertex(var16, var13 + 16.0F, var11, var14 + (float)var22).setColor(1.0F, 1.0F, 0.0F, 0.0F);
      }

      for(int var23 = this.minecraft.level.getMinY(); var23 <= this.minecraft.level.getMaxY() + 1; var23 += 2) {
         float var28 = (float)((double)var23 - var5);
         int var19 = var23 % 8 == 0 ? CELL_BORDER : YELLOW;
         var15.addVertex(var16, var13, var28, var14).setColor(1.0F, 1.0F, 0.0F, 0.0F);
         var15.addVertex(var16, var13, var28, var14).setColor(var19);
         var15.addVertex(var16, var13, var28, var14 + 16.0F).setColor(var19);
         var15.addVertex(var16, var13 + 16.0F, var28, var14 + 16.0F).setColor(var19);
         var15.addVertex(var16, var13 + 16.0F, var28, var14).setColor(var19);
         var15.addVertex(var16, var13, var28, var14).setColor(var19);
         var15.addVertex(var16, var13, var28, var14).setColor(1.0F, 1.0F, 0.0F, 0.0F);
      }

      var15 = var2.getBuffer(RenderType.debugLineStrip(2.0));

      for(int var24 = 0; var24 <= 16; var24 += 16) {
         for(int var29 = 0; var29 <= 16; var29 += 16) {
            var15.addVertex(var16, var13 + (float)var24, var10, var14 + (float)var29).setColor(0.25F, 0.25F, 1.0F, 0.0F);
            var15.addVertex(var16, var13 + (float)var24, var10, var14 + (float)var29).setColor(0.25F, 0.25F, 1.0F, 1.0F);
            var15.addVertex(var16, var13 + (float)var24, var11, var14 + (float)var29).setColor(0.25F, 0.25F, 1.0F, 1.0F);
            var15.addVertex(var16, var13 + (float)var24, var11, var14 + (float)var29).setColor(0.25F, 0.25F, 1.0F, 0.0F);
         }
      }

      for(int var25 = this.minecraft.level.getMinY(); var25 <= this.minecraft.level.getMaxY() + 1; var25 += 16) {
         float var30 = (float)((double)var25 - var5);
         var15.addVertex(var16, var13, var30, var14).setColor(0.25F, 0.25F, 1.0F, 0.0F);
         var15.addVertex(var16, var13, var30, var14).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var15.addVertex(var16, var13, var30, var14 + 16.0F).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var15.addVertex(var16, var13 + 16.0F, var30, var14 + 16.0F).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var15.addVertex(var16, var13 + 16.0F, var30, var14).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var15.addVertex(var16, var13, var30, var14).setColor(0.25F, 0.25F, 1.0F, 1.0F);
         var15.addVertex(var16, var13, var30, var14).setColor(0.25F, 0.25F, 1.0F, 0.0F);
      }

   }
}
