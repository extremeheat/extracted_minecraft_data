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
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class ChunkCullingDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   public static final Direction[] DIRECTIONS = Direction.values();
   private final Minecraft minecraft;

   public ChunkCullingDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      LevelRenderer var9 = this.minecraft.levelRenderer;
      boolean var10 = this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.CHUNK_SECTION_PATHS);
      boolean var11 = this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.CHUNK_SECTION_VISIBILITY);
      if (var10 || var11) {
         SectionOcclusionGraph var12 = var9.getSectionOcclusionGraph();
         ObjectListIterator var13 = var9.getVisibleSections().iterator();

         while(var13.hasNext()) {
            SectionRenderDispatcher.RenderSection var14 = (SectionRenderDispatcher.RenderSection)var13.next();
            SectionOcclusionGraph.Node var15 = var12.getNode(var14);
            if (var15 != null) {
               BlockPos var16 = var14.getRenderOrigin();
               var1.pushPose();
               var1.translate((double)var16.getX() - var3, (double)var16.getY() - var5, (double)var16.getZ() - var7);
               Matrix4f var17 = var1.last().pose();
               if (var10) {
                  VertexConsumer var18 = var2.getBuffer(RenderType.lines());
                  int var19 = var15.step == 0 ? 0 : Mth.hsvToRgb((float)var15.step / 50.0F, 0.9F, 0.9F);
                  int var20 = var19 >> 16 & 255;
                  int var21 = var19 >> 8 & 255;
                  int var22 = var19 & 255;

                  for(int var23 = 0; var23 < DIRECTIONS.length; ++var23) {
                     if (var15.hasSourceDirection(var23)) {
                        Direction var24 = DIRECTIONS[var23];
                        var18.addVertex(var17, 8.0F, 8.0F, 8.0F).setColor(var20, var21, var22, 255).setNormal((float)var24.getStepX(), (float)var24.getStepY(), (float)var24.getStepZ());
                        var18.addVertex(var17, (float)(8 - 16 * var24.getStepX()), (float)(8 - 16 * var24.getStepY()), (float)(8 - 16 * var24.getStepZ())).setColor(var20, var21, var22, 255).setNormal((float)var24.getStepX(), (float)var24.getStepY(), (float)var24.getStepZ());
                     }
                  }
               }

               if (var11 && var14.getSectionMesh().hasRenderableLayers()) {
                  VertexConsumer var34 = var2.getBuffer(RenderType.lines());
                  int var35 = 0;

                  for(Direction var42 : DIRECTIONS) {
                     for(Direction var27 : DIRECTIONS) {
                        boolean var28 = var14.getSectionMesh().facesCanSeeEachother(var42, var27);
                        if (!var28) {
                           ++var35;
                           var34.addVertex(var17, (float)(8 + 8 * var42.getStepX()), (float)(8 + 8 * var42.getStepY()), (float)(8 + 8 * var42.getStepZ())).setColor(255, 0, 0, 255).setNormal((float)var42.getStepX(), (float)var42.getStepY(), (float)var42.getStepZ());
                           var34.addVertex(var17, (float)(8 + 8 * var27.getStepX()), (float)(8 + 8 * var27.getStepY()), (float)(8 + 8 * var27.getStepZ())).setColor(255, 0, 0, 255).setNormal((float)var27.getStepX(), (float)var27.getStepY(), (float)var27.getStepZ());
                        }
                     }
                  }

                  if (var35 > 0) {
                     VertexConsumer var37 = var2.getBuffer(RenderType.debugQuads());
                     float var39 = 0.5F;
                     float var41 = 0.2F;
                     var37.addVertex(var17, 0.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 15.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 15.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 0.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 0.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 15.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 15.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 0.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 0.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 0.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 0.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 0.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 15.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 15.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 15.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 15.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 0.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 15.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 15.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 0.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 0.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 15.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 15.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     var37.addVertex(var17, 0.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                  }
               }

               var1.popPose();
            }
         }
      }

      Frustum var29 = var9.getCapturedFrustum();
      if (var29 != null) {
         var1.pushPose();
         var1.translate((float)(var29.getCamX() - var3), (float)(var29.getCamY() - var5), (float)(var29.getCamZ() - var7));
         Matrix4f var30 = var1.last().pose();
         Vector4f[] var31 = var29.getFrustumPoints();
         VertexConsumer var32 = var2.getBuffer(RenderType.debugQuads());
         this.addFrustumQuad(var32, var30, var31, 0, 1, 2, 3, 0, 1, 1);
         this.addFrustumQuad(var32, var30, var31, 4, 5, 6, 7, 1, 0, 0);
         this.addFrustumQuad(var32, var30, var31, 0, 1, 5, 4, 1, 1, 0);
         this.addFrustumQuad(var32, var30, var31, 2, 3, 7, 6, 0, 0, 1);
         this.addFrustumQuad(var32, var30, var31, 0, 4, 7, 3, 0, 1, 0);
         this.addFrustumQuad(var32, var30, var31, 1, 5, 6, 2, 1, 0, 1);
         VertexConsumer var33 = var2.getBuffer(RenderType.lines());
         this.addFrustumVertex(var33, var30, var31[0]);
         this.addFrustumVertex(var33, var30, var31[1]);
         this.addFrustumVertex(var33, var30, var31[1]);
         this.addFrustumVertex(var33, var30, var31[2]);
         this.addFrustumVertex(var33, var30, var31[2]);
         this.addFrustumVertex(var33, var30, var31[3]);
         this.addFrustumVertex(var33, var30, var31[3]);
         this.addFrustumVertex(var33, var30, var31[0]);
         this.addFrustumVertex(var33, var30, var31[4]);
         this.addFrustumVertex(var33, var30, var31[5]);
         this.addFrustumVertex(var33, var30, var31[5]);
         this.addFrustumVertex(var33, var30, var31[6]);
         this.addFrustumVertex(var33, var30, var31[6]);
         this.addFrustumVertex(var33, var30, var31[7]);
         this.addFrustumVertex(var33, var30, var31[7]);
         this.addFrustumVertex(var33, var30, var31[4]);
         this.addFrustumVertex(var33, var30, var31[0]);
         this.addFrustumVertex(var33, var30, var31[4]);
         this.addFrustumVertex(var33, var30, var31[1]);
         this.addFrustumVertex(var33, var30, var31[5]);
         this.addFrustumVertex(var33, var30, var31[2]);
         this.addFrustumVertex(var33, var30, var31[6]);
         this.addFrustumVertex(var33, var30, var31[3]);
         this.addFrustumVertex(var33, var30, var31[7]);
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
