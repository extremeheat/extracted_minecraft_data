package net.minecraft.client.renderer.debug;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;

public class VillageSectionsDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   public VillageSectionsDebugRenderer() {
      super();
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      var7.forEachBlock(DebugSubscriptions.VILLAGE_SECTIONS, (var0, var1x) -> {
         SectionPos var2 = SectionPos.of(var0);
         Gizmos.cuboid(var2.center(), GizmoStyle.fill(ARGB.colorFromFloat(0.15F, 0.2F, 1.0F, 0.2F)));
      });
   }
}
