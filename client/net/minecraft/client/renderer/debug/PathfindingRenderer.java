package net.minecraft.client.renderer.debug;

import java.util.Locale;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugPathInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PathfindingRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final float MAX_RENDER_DIST = 80.0F;
   private static final int MAX_TARGETING_DIST = 8;
   private static final boolean SHOW_ONLY_SELECTED = false;
   private static final boolean SHOW_OPEN_CLOSED = true;
   private static final boolean SHOW_OPEN_CLOSED_COST_MALUS = false;
   private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_TEXT = false;
   private static final boolean SHOW_OPEN_CLOSED_NODE_TYPE_WITH_BOX = true;
   private static final boolean SHOW_GROUND_LABELS = true;
   private static final float TEXT_SCALE = 0.32F;

   public PathfindingRenderer() {
      super();
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      var7.forEachEntity(DebugSubscriptions.ENTITY_PATHS, (var6, var7x) -> renderPath(var1, var3, var5, var7x.path(), var7x.maxNodeDistance()));
   }

   private static void renderPath(double var0, double var2, double var4, Path var6, float var7) {
      renderPath(var6, var7, true, true, var0, var2, var4);
   }

   public static void renderPath(Path var0, float var1, boolean var2, boolean var3, double var4, double var6, double var8) {
      renderPathLine(var0, var4, var6, var8);
      BlockPos var10 = var0.getTarget();
      if (distanceToCamera(var10, var4, var6, var8) <= 80.0F) {
         Gizmos.cuboid(new AABB((double)((float)var10.getX() + 0.25F), (double)((float)var10.getY() + 0.25F), (double)var10.getZ() + 0.25, (double)((float)var10.getX() + 0.75F), (double)((float)var10.getY() + 0.75F), (double)((float)var10.getZ() + 0.75F)), GizmoStyle.fill(ARGB.colorFromFloat(0.5F, 0.0F, 1.0F, 0.0F)));

         for(int var11 = 0; var11 < var0.getNodeCount(); ++var11) {
            Node var12 = var0.getNode(var11);
            if (distanceToCamera(var12.asBlockPos(), var4, var6, var8) <= 80.0F) {
               float var13 = var11 == var0.getNextNodeIndex() ? 1.0F : 0.0F;
               float var14 = var11 == var0.getNextNodeIndex() ? 0.0F : 1.0F;
               AABB var15 = new AABB((double)((float)var12.x + 0.5F - var1), (double)((float)var12.y + 0.01F * (float)var11), (double)((float)var12.z + 0.5F - var1), (double)((float)var12.x + 0.5F + var1), (double)((float)var12.y + 0.25F + 0.01F * (float)var11), (double)((float)var12.z + 0.5F + var1));
               Gizmos.cuboid(var15, GizmoStyle.fill(ARGB.colorFromFloat(0.5F, var13, 0.0F, var14)));
            }
         }
      }

      Path.DebugData var16 = var0.debugData();
      if (var2 && var16 != null) {
         for(Node var25 : var16.closedSet()) {
            if (distanceToCamera(var25.asBlockPos(), var4, var6, var8) <= 80.0F) {
               Gizmos.cuboid(new AABB((double)((float)var25.x + 0.5F - var1 / 2.0F), (double)((float)var25.y + 0.01F), (double)((float)var25.z + 0.5F - var1 / 2.0F), (double)((float)var25.x + 0.5F + var1 / 2.0F), (double)var25.y + 0.1, (double)((float)var25.z + 0.5F + var1 / 2.0F)), GizmoStyle.fill(ARGB.colorFromFloat(0.5F, 1.0F, 0.8F, 0.8F)));
            }
         }

         for(Node var26 : var16.openSet()) {
            if (distanceToCamera(var26.asBlockPos(), var4, var6, var8) <= 80.0F) {
               Gizmos.cuboid(new AABB((double)((float)var26.x + 0.5F - var1 / 2.0F), (double)((float)var26.y + 0.01F), (double)((float)var26.z + 0.5F - var1 / 2.0F), (double)((float)var26.x + 0.5F + var1 / 2.0F), (double)var26.y + 0.1, (double)((float)var26.z + 0.5F + var1 / 2.0F)), GizmoStyle.fill(ARGB.colorFromFloat(0.5F, 0.8F, 1.0F, 1.0F)));
            }
         }
      }

      if (var3) {
         for(int var19 = 0; var19 < var0.getNodeCount(); ++var19) {
            Node var22 = var0.getNode(var19);
            if (distanceToCamera(var22.asBlockPos(), var4, var6, var8) <= 80.0F) {
               Gizmos.billboardText(String.valueOf(var22.type), new Vec3((double)var22.x + 0.5, (double)var22.y + 0.75, (double)var22.z + 0.5), TextGizmo.Style.whiteAndCentered().withScale(0.32F)).setAlwaysOnTop();
               Gizmos.billboardText(String.format(Locale.ROOT, "%.2f", var22.costMalus), new Vec3((double)var22.x + 0.5, (double)var22.y + 0.25, (double)var22.z + 0.5), TextGizmo.Style.whiteAndCentered().withScale(0.32F)).setAlwaysOnTop();
            }
         }
      }

   }

   public static void renderPathLine(Path var0, double var1, double var3, double var5) {
      if (var0.getNodeCount() >= 2) {
         Vec3 var7 = var0.getNode(0).asVec3();

         for(int var8 = 1; var8 < var0.getNodeCount(); ++var8) {
            Node var9 = var0.getNode(var8);
            if (distanceToCamera(var9.asBlockPos(), var1, var3, var5) > 80.0F) {
               var7 = var9.asVec3();
            } else {
               float var10 = (float)var8 / (float)var0.getNodeCount() * 0.33F;
               int var11 = ARGB.opaque(Mth.hsvToRgb(var10, 0.9F, 0.9F));
               Gizmos.arrow(var7.add(0.5, 0.5, 0.5), var9.asVec3().add(0.5, 0.5, 0.5), var11);
               var7 = var9.asVec3();
            }
         }

      }
   }

   private static float distanceToCamera(BlockPos var0, double var1, double var3, double var5) {
      return (float)(Math.abs((double)var0.getX() - var1) + Math.abs((double)var0.getY() - var3) + Math.abs((double)var0.getZ() - var5));
   }

   // $FF: synthetic method
   private static void lambda$emitGizmos$0(DebugValueAccess var0, double var1, double var3, double var5, Entity var7) {
      DebugPathInfo var8 = (DebugPathInfo)var0.getEntityValue(DebugSubscriptions.ENTITY_PATHS, var7);
      if (var8 != null) {
         renderPath(var1, var3, var5, var8.path(), var8.maxNodeDistance());
      }

   }
}
