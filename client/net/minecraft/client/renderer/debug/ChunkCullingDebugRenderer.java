package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugValueAccess;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class ChunkCullingDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   public static final Direction[] DIRECTIONS = Direction.values();
   private final Minecraft minecraft;

   public ChunkCullingDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9) {
      LevelRenderer var10 = this.minecraft.levelRenderer;
      boolean var11 = this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.CHUNK_SECTION_PATHS);
      boolean var12 = this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.CHUNK_SECTION_VISIBILITY);
      if (var11 || var12) {
         SectionOcclusionGraph var13 = var10.getSectionOcclusionGraph();
         ObjectListIterator var14 = var10.getVisibleSections().iterator();

         while(var14.hasNext()) {
            SectionRenderDispatcher.RenderSection var15 = (SectionRenderDispatcher.RenderSection)var14.next();
            SectionOcclusionGraph.Node var16 = var13.getNode(var15);
            if (var16 != null) {
               BlockPos var17 = var15.getRenderOrigin();
               var1.pushPose();
               var1.translate((double)var17.getX() - var3, (double)var17.getY() - var5, (double)var17.getZ() - var7);
               Matrix4f var18 = var1.last().pose();
               if (var11) {
                  VertexConsumer var19 = var2.getBuffer(RenderType.lines());
                  int var20 = var16.step == 0 ? 0 : Mth.hsvToRgb((float)var16.step / 50.0F, 0.9F, 0.9F);
                  int var21 = var20 >> 16 & 255;
                  int var22 = var20 >> 8 & 255;
                  int var23 = var20 & 255;

                  for(int var24 = 0; var24 < DIRECTIONS.length; ++var24) {
                     if (var16.hasSourceDirection(var24)) {
                        Direction var25 = DIRECTIONS[var24];
                        var19.addVertex(var18, 8.0F, 8.0F, 8.0F).setColor(var21, var22, var23, 255).setNormal((float)var25.getStepX(), (float)var25.getStepY(), (float)var25.getStepZ());
                        var19.addVertex(var18, (float)(8 - 16 * var25.getStepX()), (float)(8 - 16 * var25.getStepY()), (float)(8 - 16 * var25.getStepZ())).setColor(var21, var22, var23, 255).setNormal((float)var25.getStepX(), (float)var25.getStepY(), (float)var25.getStepZ());
                     }
                  }
               }

               if (var12 && var15.getSectionMesh().hasRenderableLayers()) {
                  VertexConsumer var35 = var2.getBuffer(RenderType.lines());
                  int var36 = 0;

                  for(Direction var43 : DIRECTIONS) {
                     for(Direction var28 : DIRECTIONS) {
                        boolean var29 = var15.getSectionMesh().facesCanSeeEachother(var43, var28);
                        if (!var29) {
                           ++var36;
                           var35.addVertex(var18, (float)(8 + 8 * var43.getStepX()), (float)(8 + 8 * var43.getStepY()), (float)(8 + 8 * var43.getStepZ())).setColor(255, 0, 0, 255).setNormal((float)var43.getStepX(), (float)var43.getStepY(), (float)var43.getStepZ());
                           var35.addVertex(var18, (float)(8 + 8 * var28.getStepX()), (float)(8 + 8 * var28.getStepY()), (float)(8 + 8 * var28.getStepZ())).setColor(255, 0, 0, 255).setNormal((float)var28.getStepX(), (float)var28.getStepY(), (float)var28.getStepZ());
                        }
                     }
                  }

                  if (var36 > 0) {
                     VertexConsumer var38 = var2.getBuffer(RenderType.debugQuads());
                     float var40 = 0.5F;
                     float var42 = 0.2F;
                     var38.addVertex(var18, 0.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 15.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 15.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 0.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 0.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 15.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 15.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 0.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 0.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 0.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 0.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 0.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 15.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 15.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 15.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 15.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 0.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 15.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 15.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 0.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 0.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 15.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 15.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var38.addVertex(var18, 0.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                  }
               }

               var1.popPose();
            }
         }
      }

      Frustum var30 = var10.getCapturedFrustum();
      if (var30 != null) {
         var1.pushPose();
         var1.translate((float)(var30.getCamX() - var3), (float)(var30.getCamY() - var5), (float)(var30.getCamZ() - var7));
         Matrix4f var31 = var1.last().pose();
         Vector4f[] var32 = var30.getFrustumPoints();
         VertexConsumer var33 = var2.getBuffer(RenderType.debugQuads());
         this.addFrustumQuad(var33, var31, var32, 0, 1, 2, 3, 0, 1, 1);
         this.addFrustumQuad(var33, var31, var32, 4, 5, 6, 7, 1, 0, 0);
         this.addFrustumQuad(var33, var31, var32, 0, 1, 5, 4, 1, 1, 0);
         this.addFrustumQuad(var33, var31, var32, 2, 3, 7, 6, 0, 0, 1);
         this.addFrustumQuad(var33, var31, var32, 0, 4, 7, 3, 0, 1, 0);
         this.addFrustumQuad(var33, var31, var32, 1, 5, 6, 2, 1, 0, 1);
         VertexConsumer var34 = var2.getBuffer(RenderType.lines());
         this.addFrustumVertex(var34, var31, var32[0]);
         this.addFrustumVertex(var34, var31, var32[1]);
         this.addFrustumVertex(var34, var31, var32[1]);
         this.addFrustumVertex(var34, var31, var32[2]);
         this.addFrustumVertex(var34, var31, var32[2]);
         this.addFrustumVertex(var34, var31, var32[3]);
         this.addFrustumVertex(var34, var31, var32[3]);
         this.addFrustumVertex(var34, var31, var32[0]);
         this.addFrustumVertex(var34, var31, var32[4]);
         this.addFrustumVertex(var34, var31, var32[5]);
         this.addFrustumVertex(var34, var31, var32[5]);
         this.addFrustumVertex(var34, var31, var32[6]);
         this.addFrustumVertex(var34, var31, var32[6]);
         this.addFrustumVertex(var34, var31, var32[7]);
         this.addFrustumVertex(var34, var31, var32[7]);
         this.addFrustumVertex(var34, var31, var32[4]);
         this.addFrustumVertex(var34, var31, var32[0]);
         this.addFrustumVertex(var34, var31, var32[4]);
         this.addFrustumVertex(var34, var31, var32[1]);
         this.addFrustumVertex(var34, var31, var32[5]);
         this.addFrustumVertex(var34, var31, var32[2]);
         this.addFrustumVertex(var34, var31, var32[6]);
         this.addFrustumVertex(var34, var31, var32[3]);
         this.addFrustumVertex(var34, var31, var32[7]);
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
