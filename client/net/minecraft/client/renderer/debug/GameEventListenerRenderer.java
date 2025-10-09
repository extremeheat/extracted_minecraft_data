package net.minecraft.client.renderer.debug;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GameEventListenerRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final float BOX_HEIGHT = 1.0F;

   public GameEventListenerRenderer() {
      super();
   }

   private void forEachListener(DebugValueAccess var1, ListenerVisitor var2) {
      var1.forEachBlock(DebugSubscriptions.GAME_EVENT_LISTENERS, (var1x, var2x) -> var2.accept(var1x.getCenter(), var2x.listenerRadius()));
      var1.forEachEntity(DebugSubscriptions.GAME_EVENT_LISTENERS, (var1x, var2x) -> var2.accept(var1x.position(), var2x.listenerRadius()));
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      this.forEachListener(var7, (var0, var1x) -> {
         double var2 = (double)var1x * 2.0;
         Gizmos.cuboid(AABB.ofSize(var0, var2, var2, var2), GizmoStyle.fill(ARGB.colorFromFloat(0.35F, 1.0F, 1.0F, 0.0F)));
      });
      this.forEachListener(var7, (var0, var1x) -> Gizmos.cuboid(AABB.ofSize(var0, 0.5, 1.0, 0.5).move(0.0, 0.5, 0.0), GizmoStyle.fill(ARGB.colorFromFloat(0.35F, 1.0F, 1.0F, 0.0F))));
      this.forEachListener(var7, (var0, var1x) -> {
         Gizmos.billboardText("Listener Origin", var0.add(0.0, 1.8, 0.0), TextGizmo.Style.whiteAndCentered().withScale(0.4F));
         Gizmos.billboardText(BlockPos.containing(var0).toString(), var0.add(0.0, 1.5, 0.0), TextGizmo.Style.forColorAndCentered(-6959665).withScale(0.4F));
      });
      var7.forEachEvent(DebugSubscriptions.GAME_EVENTS, (var0, var1x, var2) -> {
         Vec3 var3 = var0.pos();
         double var4 = 0.4;
         AABB var6 = AABB.ofSize(var3.add(0.0, 0.5, 0.0), 0.4, 0.9, 0.4);
         Gizmos.cuboid(var6, GizmoStyle.fill(ARGB.colorFromFloat(0.2F, 1.0F, 1.0F, 1.0F)));
         Gizmos.billboardText(var0.event().getRegisteredName(), var3.add(0.0, 0.85, 0.0), TextGizmo.Style.forColorAndCentered(-7564911).withScale(0.12F));
      });
   }

   @FunctionalInterface
   interface ListenerVisitor {
      void accept(Vec3 var1, int var2);
   }
}
