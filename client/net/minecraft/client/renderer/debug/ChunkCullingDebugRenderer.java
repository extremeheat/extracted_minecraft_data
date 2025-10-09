package net.minecraft.client.renderer.debug;

import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class ChunkCullingDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   public static final Direction[] DIRECTIONS = Direction.values();
   private final Minecraft minecraft;

   public ChunkCullingDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
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
               if (var11) {
                  int var18 = var16.step == 0 ? 0 : Mth.hsvToRgb((float)var16.step / 50.0F, 0.9F, 0.9F);

                  for(int var19 = 0; var19 < DIRECTIONS.length; ++var19) {
                     if (var16.hasSourceDirection(var19)) {
                        Direction var20 = DIRECTIONS[var19];
                        Gizmos.line(Vec3.atLowerCornerWithOffset(var17, 8.0, 8.0, 8.0), Vec3.atLowerCornerWithOffset(var17, (double)(8 - 16 * var20.getStepX()), (double)(8 - 16 * var20.getStepY()), (double)(8 - 16 * var20.getStepZ())), ARGB.opaque(var18));
                     }
                  }
               }

               if (var12 && var15.getSectionMesh().hasRenderableLayers()) {
                  int var31 = 0;

                  for(Direction var22 : DIRECTIONS) {
                     for(Direction var26 : DIRECTIONS) {
                        boolean var27 = var15.getSectionMesh().facesCanSeeEachother(var22, var26);
                        if (!var27) {
                           ++var31;
                           Gizmos.line(Vec3.atLowerCornerWithOffset(var17, (double)(8 + 8 * var22.getStepX()), (double)(8 + 8 * var22.getStepY()), (double)(8 + 8 * var22.getStepZ())), Vec3.atLowerCornerWithOffset(var17, (double)(8 + 8 * var26.getStepX()), (double)(8 + 8 * var26.getStepY()), (double)(8 + 8 * var26.getStepZ())), ARGB.color(255, 255, 0, 0));
                        }
                     }
                  }

                  if (var31 > 0) {
                     float var33 = 0.5F;
                     float var35 = 0.2F;
                     Gizmos.cuboid(var15.getBoundingBox().deflate(0.5), GizmoStyle.fill(ARGB.colorFromFloat(0.2F, 0.9F, 0.9F, 0.0F)));
                  }
               }
            }
         }
      }

      Frustum var28 = var10.getCapturedFrustum();
      if (var28 != null) {
         Vec3 var29 = new Vec3(var28.getCamX(), var28.getCamY(), var28.getCamZ());
         Vector4f[] var30 = var28.getFrustumPoints();
         this.addFrustumQuad(var29, var30, 0, 1, 2, 3, 0, 1, 1);
         this.addFrustumQuad(var29, var30, 4, 5, 6, 7, 1, 0, 0);
         this.addFrustumQuad(var29, var30, 0, 1, 5, 4, 1, 1, 0);
         this.addFrustumQuad(var29, var30, 2, 3, 7, 6, 0, 0, 1);
         this.addFrustumQuad(var29, var30, 0, 4, 7, 3, 0, 1, 0);
         this.addFrustumQuad(var29, var30, 1, 5, 6, 2, 1, 0, 1);
         this.addFrustumLine(var29, var30[0], var30[1]);
         this.addFrustumLine(var29, var30[1], var30[2]);
         this.addFrustumLine(var29, var30[2], var30[3]);
         this.addFrustumLine(var29, var30[3], var30[0]);
         this.addFrustumLine(var29, var30[4], var30[5]);
         this.addFrustumLine(var29, var30[5], var30[6]);
         this.addFrustumLine(var29, var30[6], var30[7]);
         this.addFrustumLine(var29, var30[7], var30[4]);
         this.addFrustumLine(var29, var30[0], var30[4]);
         this.addFrustumLine(var29, var30[1], var30[5]);
         this.addFrustumLine(var29, var30[2], var30[6]);
         this.addFrustumLine(var29, var30[3], var30[7]);
      }

   }

   private void addFrustumLine(Vec3 var1, Vector4f var2, Vector4f var3) {
      Gizmos.line(new Vec3(var1.x + (double)var2.x, var1.y + (double)var2.y, var1.z + (double)var2.z), new Vec3(var1.x + (double)var3.x, var1.y + (double)var3.y, var1.z + (double)var3.z), -16777216);
   }

   private void addFrustumQuad(Vec3 var1, Vector4f[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      float var10 = 0.25F;
      Gizmos.rect((new Vec3((double)var2[var3].x(), (double)var2[var3].y(), (double)var2[var3].z())).add(var1), (new Vec3((double)var2[var4].x(), (double)var2[var4].y(), (double)var2[var4].z())).add(var1), (new Vec3((double)var2[var5].x(), (double)var2[var5].y(), (double)var2[var5].z())).add(var1), (new Vec3((double)var2[var6].x(), (double)var2[var6].y(), (double)var2[var6].z())).add(var1), GizmoStyle.fill(ARGB.colorFromFloat(0.25F, (float)var7, (float)var8, (float)var9)));
   }
}
