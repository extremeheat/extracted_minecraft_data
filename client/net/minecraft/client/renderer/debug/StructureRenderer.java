package net.minecraft.client.renderer.debug;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugStructureInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;

public class StructureRenderer implements DebugRenderer.SimpleDebugRenderer {
   public StructureRenderer() {
      super();
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      var7.forEachChunk(DebugSubscriptions.STRUCTURES, (var0, var1x) -> {
         for(DebugStructureInfo var3 : var1x) {
            Gizmos.cuboid(AABB.of(var3.boundingBox()), GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 1.0F, 1.0F, 1.0F)));

            for(DebugStructureInfo.Piece var5 : var3.pieces()) {
               if (var5.isStart()) {
                  Gizmos.cuboid(AABB.of(var5.boundingBox()), GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 0.0F, 1.0F, 0.0F)));
               } else {
                  Gizmos.cuboid(AABB.of(var5.boundingBox()), GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 0.0F, 0.0F, 1.0F)));
               }
            }
         }

      });
   }
}
