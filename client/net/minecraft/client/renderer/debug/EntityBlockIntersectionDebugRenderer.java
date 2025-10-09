package net.minecraft.client.renderer.debug;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;

public class EntityBlockIntersectionDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final float PADDING = 0.02F;

   public EntityBlockIntersectionDebugRenderer() {
      super();
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      var7.forEachBlock(DebugSubscriptions.ENTITY_BLOCK_INTERSECTIONS, (var0, var1x) -> Gizmos.cuboid(var0, 0.02F, GizmoStyle.fill(var1x.color())));
   }
}
