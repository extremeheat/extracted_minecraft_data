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

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      int var10 = DebugSubscriptions.NEIGHBOR_UPDATES.expireAfterTicks();
      double var11 = 1.0 / (double)(var10 * 2);
      HashMap var13 = new HashMap();
      var7.forEachEvent(DebugSubscriptions.NEIGHBOR_UPDATES, (var1x, var2, var3x) -> {
         long var4 = (long)(var3x - var2);
         LastUpdate var6 = (LastUpdate)var13.getOrDefault(var1x, NeighborsUpdateRenderer.LastUpdate.NONE);
         var13.put(var1x, var6.tryCount((int)var4));
      });

      for(Map.Entry var15 : var13.entrySet()) {
         BlockPos var16 = (BlockPos)var15.getKey();
         LastUpdate var17 = (LastUpdate)var15.getValue();
         AABB var18 = (new AABB(var16)).inflate(0.002).deflate(var11 * (double)var17.age);
         Gizmos.cuboid(var18, GizmoStyle.stroke(-1));
      }

      for(Map.Entry var20 : var13.entrySet()) {
         BlockPos var21 = (BlockPos)var20.getKey();
         LastUpdate var22 = (LastUpdate)var20.getValue();
         Gizmos.billboardText(String.valueOf(var22.count), Vec3.atCenterOf(var21), TextGizmo.Style.whiteAndCentered());
      }

   }

   static record LastUpdate(int count, int age) {
      final int count;
      final int age;
      static final LastUpdate NONE = new LastUpdate(0, 2147483647);

      private LastUpdate(int var1, int var2) {
         super();
         this.count = var1;
         this.age = var2;
      }

      public LastUpdate tryCount(int var1) {
         if (var1 == this.age) {
            return new LastUpdate(this.count + 1, var1);
         } else {
            return var1 < this.age ? new LastUpdate(1, var1) : this;
         }
      }
   }
}
