package net.minecraft.client.renderer.debug;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NeighborsUpdateRenderer implements DebugRenderer.SimpleDebugRenderer {
   public NeighborsUpdateRenderer() {
      super();
   }

   public void emitGizmos(final double camX, final double camY, final double camZ, final DebugValueAccess debugValues, final Frustum frustum, final float partialTicks) {
      int shrinkTime = DebugSubscriptions.NEIGHBOR_UPDATES.expireAfterTicks();
      double shrinkSpeed = 1.0 / (double)(shrinkTime * 2);
      Map<BlockPos, LastUpdate> lastUpdates = new HashMap();
      debugValues.forEachEvent(DebugSubscriptions.NEIGHBOR_UPDATES, (blockPos, remainingTicks, totalLifetime) -> {
         long age = (long)(totalLifetime - remainingTicks);
         LastUpdate lastUpdate = (LastUpdate)lastUpdates.getOrDefault(blockPos, NeighborsUpdateRenderer.LastUpdate.NONE);
         lastUpdates.put(blockPos, lastUpdate.tryCount((int)age));
      });

      for(Map.Entry<BlockPos, LastUpdate> entry : lastUpdates.entrySet()) {
         BlockPos pos = (BlockPos)entry.getKey();
         LastUpdate lastUpdate = (LastUpdate)entry.getValue();
         AABB aabb = (new AABB(pos)).inflate(0.002).deflate(shrinkSpeed * (double)lastUpdate.age);
         Gizmos.cuboid(aabb, GizmoStyle.stroke(-1));
      }

      for(Map.Entry<BlockPos, LastUpdate> entry : lastUpdates.entrySet()) {
         BlockPos pos = (BlockPos)entry.getKey();
         LastUpdate lastUpdate = (LastUpdate)entry.getValue();
         Gizmos.billboardText(String.valueOf(lastUpdate.count), Vec3.atCenterOf(pos), TextGizmo.Style.whiteAndCentered());
      }

   }

   private static record LastUpdate(int count, int age) {
      private static final LastUpdate NONE = new LastUpdate(0, 2147483647);

      private LastUpdate {
         super();
      }

      public LastUpdate tryCount(final int age) {
         if (age == this.age) {
            return new LastUpdate(this.count + 1, age);
         } else {
            return age < this.age ? new LastUpdate(1, age) : this;
         }
      }
   }
}
