package net.minecraft.client.renderer.debug;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BreezeDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int JUMP_TARGET_LINE_COLOR = ARGB.color(255, 255, 100, 255);
   private static final int TARGET_LINE_COLOR = ARGB.color(255, 100, 255, 255);
   private static final int INNER_CIRCLE_COLOR = ARGB.color(255, 0, 255, 0);
   private static final int MIDDLE_CIRCLE_COLOR = ARGB.color(255, 255, 165, 0);
   private static final int OUTER_CIRCLE_COLOR = ARGB.color(255, 255, 0, 0);
   private final Minecraft minecraft;

   public BreezeDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      ClientLevel var10 = this.minecraft.level;
      var7.forEachEntity(DebugSubscriptions.BREEZES, (var2, var3x) -> {
         Optional var10000 = var3x.attackTarget();
         Objects.requireNonNull(var10);
         var10000.map(var10::getEntity).map((var1) -> var1.getPosition(this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true))).ifPresent((var1) -> {
            Gizmos.arrow(var2.position(), var1, TARGET_LINE_COLOR);
            Vec3 var2x = var1.add(0.0, 0.009999999776482582, 0.0);
            Gizmos.circle(var2x, 4.0F, GizmoStyle.stroke(INNER_CIRCLE_COLOR));
            Gizmos.circle(var2x, 8.0F, GizmoStyle.stroke(MIDDLE_CIRCLE_COLOR));
            Gizmos.circle(var2x, 24.0F, GizmoStyle.stroke(OUTER_CIRCLE_COLOR));
         });
         var3x.jumpTarget().ifPresent((var1) -> {
            Gizmos.arrow(var2.position(), var1.getCenter(), JUMP_TARGET_LINE_COLOR);
            Gizmos.cuboid(AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(var1)), GizmoStyle.fill(ARGB.colorFromFloat(1.0F, 1.0F, 0.0F, 0.0F)));
         });
      });
   }
}
