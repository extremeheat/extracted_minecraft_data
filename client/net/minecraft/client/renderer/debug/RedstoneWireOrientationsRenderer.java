package net.minecraft.client.renderer.debug;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.Vec3;

public class RedstoneWireOrientationsRenderer implements DebugRenderer.SimpleDebugRenderer {
   public RedstoneWireOrientationsRenderer() {
      super();
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      var7.forEachBlock(DebugSubscriptions.REDSTONE_WIRE_ORIENTATIONS, (var0, var1x) -> {
         Vec3 var2 = var0.getBottomCenter().subtract(0.0, 0.1, 0.0);
         Gizmos.arrow(var2, var2.add(var1x.getFront().getUnitVec3().scale(0.5)), -16776961);
         Gizmos.arrow(var2, var2.add(var1x.getUp().getUnitVec3().scale(0.4)), -65536);
         Gizmos.arrow(var2, var2.add(var1x.getSide().getUnitVec3().scale(0.3)), -256);
      });
   }
}
