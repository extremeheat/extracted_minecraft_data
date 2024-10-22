package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class ChunkCullingDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   public static final Direction[] DIRECTIONS = Direction.values();
   private final Minecraft minecraft;

   public ChunkCullingDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   @Override
   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      LevelRenderer var9 = this.minecraft.levelRenderer;
      if (this.minecraft.sectionPath || this.minecraft.sectionVisibility) {
         SectionOcclusionGraph var10 = var9.getSectionOcclusionGraph();
         ObjectListIterator var11 = var9.getVisibleSections().iterator();

         while (var11.hasNext()) {
            SectionRenderDispatcher.RenderSection var12 = (SectionRenderDispatcher.RenderSection)var11.next();
            SectionOcclusionGraph.Node var13 = var10.getNode(var12);
            if (var13 != null) {
               BlockPos var14 = var12.getOrigin();
               var1.pushPose();
               var1.translate((double)var14.getX() - var3, (double)var14.getY() - var5, (double)var14.getZ() - var7);
               Matrix4f var15 = var1.last().pose();
               if (this.minecraft.sectionPath) {
                  VertexConsumer var16 = var2.getBuffer(RenderType.lines());
                  int var17 = var13.step == 0 ? 0 : Mth.hsvToRgb((float)var13.step / 50.0F, 0.9F, 0.9F);
                  int var18 = var17 >> 16 & 0xFF;
                  int var19 = var17 >> 8 & 0xFF;
                  int var20 = var17 & 0xFF;

                  for (int var21 = 0; var21 < DIRECTIONS.length; var21++) {
                     if (var13.hasSourceDirection(var21)) {
                        Direction var22 = DIRECTIONS[var21];
                        var16.addVertex(var15, 8.0F, 8.0F, 8.0F)
                           .setColor(var18, var19, var20, 255)
                           .setNormal((float)var22.getStepX(), (float)var22.getStepY(), (float)var22.getStepZ());
                        var16.addVertex(var15, (float)(8 - 16 * var22.getStepX()), (float)(8 - 16 * var22.getStepY()), (float)(8 - 16 * var22.getStepZ()))
                           .setColor(var18, var19, var20, 255)
                           .setNormal((float)var22.getStepX(), (float)var22.getStepY(), (float)var22.getStepZ());
                     }
                  }
               }

               if (this.minecraft.sectionVisibility && var12.getCompiled().hasRenderableLayers()) {
                  VertexConsumer var32 = var2.getBuffer(RenderType.lines());
                  int var33 = 0;

                  for (Direction var40 : DIRECTIONS) {
                     for (Direction var25 : DIRECTIONS) {
                        boolean var26 = var12.getCompiled().facesCanSeeEachother(var40, var25);
                        if (!var26) {
                           var33++;
                           var32.addVertex(var15, (float)(8 + 8 * var40.getStepX()), (float)(8 + 8 * var40.getStepY()), (float)(8 + 8 * var40.getStepZ()))
                              .setColor(255, 0, 0, 255)
                              .setNormal((float)var40.getStepX(), (float)var40.getStepY(), (float)var40.getStepZ());
                           var32.addVertex(var15, (float)(8 + 8 * var25.getStepX()), (float)(8 + 8 * var25.getStepY()), (float)(8 + 8 * var25.getStepZ()))
                              .setColor(255, 0, 0, 255)
                              .setNormal((float)var25.getStepX(), (float)var25.getStepY(), (float)var25.getStepZ());
                        }
                     }
                  }

                  if (var33 > 0) {
                     VertexConsumer var35 = var2.getBuffer(RenderType.debugQuads());
                     float var37 = 0.5F;
                     float var39 = 0.2F;
                     var35.addVertex(var15, 0.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 15.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 15.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 0.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 0.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 15.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 15.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 0.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 0.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 0.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 0.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 0.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 15.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 15.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 15.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 15.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 0.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 15.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 15.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 0.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 0.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 15.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 15.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var35.addVertex(var15, 0.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                  }
               }

               var1.popPose();
            }
         }
      }

      Frustum var27 = var9.getCapturedFrustum();
      if (var27 != null) {
         var1.pushPose();
         var1.translate((float)(var27.getCamX() - var3), (float)(var27.getCamY() - var5), (float)(var27.getCamZ() - var7));
         Matrix4f var28 = var1.last().pose();
         Vector4f[] var29 = var27.getFrustumPoints();
         VertexConsumer var30 = var2.getBuffer(RenderType.debugQuads());
         this.addFrustumQuad(var30, var28, var29, 0, 1, 2, 3, 0, 1, 1);
         this.addFrustumQuad(var30, var28, var29, 4, 5, 6, 7, 1, 0, 0);
         this.addFrustumQuad(var30, var28, var29, 0, 1, 5, 4, 1, 1, 0);
         this.addFrustumQuad(var30, var28, var29, 2, 3, 7, 6, 0, 0, 1);
         this.addFrustumQuad(var30, var28, var29, 0, 4, 7, 3, 0, 1, 0);
         this.addFrustumQuad(var30, var28, var29, 1, 5, 6, 2, 1, 0, 1);
         VertexConsumer var31 = var2.getBuffer(RenderType.lines());
         this.addFrustumVertex(var31, var28, var29[0]);
         this.addFrustumVertex(var31, var28, var29[1]);
         this.addFrustumVertex(var31, var28, var29[1]);
         this.addFrustumVertex(var31, var28, var29[2]);
         this.addFrustumVertex(var31, var28, var29[2]);
         this.addFrustumVertex(var31, var28, var29[3]);
         this.addFrustumVertex(var31, var28, var29[3]);
         this.addFrustumVertex(var31, var28, var29[0]);
         this.addFrustumVertex(var31, var28, var29[4]);
         this.addFrustumVertex(var31, var28, var29[5]);
         this.addFrustumVertex(var31, var28, var29[5]);
         this.addFrustumVertex(var31, var28, var29[6]);
         this.addFrustumVertex(var31, var28, var29[6]);
         this.addFrustumVertex(var31, var28, var29[7]);
         this.addFrustumVertex(var31, var28, var29[7]);
         this.addFrustumVertex(var31, var28, var29[4]);
         this.addFrustumVertex(var31, var28, var29[0]);
         this.addFrustumVertex(var31, var28, var29[4]);
         this.addFrustumVertex(var31, var28, var29[1]);
         this.addFrustumVertex(var31, var28, var29[5]);
         this.addFrustumVertex(var31, var28, var29[2]);
         this.addFrustumVertex(var31, var28, var29[6]);
         this.addFrustumVertex(var31, var28, var29[3]);
         this.addFrustumVertex(var31, var28, var29[7]);
         var1.popPose();
      }
   }

   private void addFrustumVertex(VertexConsumer var1, Matrix4f var2, Vector4f var3) {
      var1.addVertex(var2, var3.x(), var3.y(), var3.z()).setColor(-16777216).setNormal(0.0F, 0.0F, -1.0F);
   }

   private void addFrustumQuad(VertexConsumer var1, Matrix4f var2, Vector4f[] var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      float var11 = 0.25F;
      var1.addVertex(var2, var3[var4].x(), var3[var4].y(), var3[var4].z()).setColor((float)var8, (float)var9, (float)var10, 0.25F);
      var1.addVertex(var2, var3[var5].x(), var3[var5].y(), var3[var5].z()).setColor((float)var8, (float)var9, (float)var10, 0.25F);
      var1.addVertex(var2, var3[var6].x(), var3[var6].y(), var3[var6].z()).setColor((float)var8, (float)var9, (float)var10, 0.25F);
      var1.addVertex(var2, var3[var7].x(), var3[var7].y(), var3[var7].z()).setColor((float)var8, (float)var9, (float)var10, 0.25F);
   }
}
