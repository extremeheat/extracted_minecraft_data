package net.minecraft.client.renderer.debug;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.debug.DebugGoalInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.Vec3;

public class GoalSelectorDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int MAX_RENDER_DIST = 160;
   private final Minecraft minecraft;

   public GoalSelectorDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      Camera var10 = this.minecraft.gameRenderer.getMainCamera();
      BlockPos var11 = BlockPos.containing(var10.position().x, 0.0, var10.position().z);
      var7.forEachEntity(DebugSubscriptions.GOAL_SELECTORS, (var1x, var2) -> {
         if (var11.closerThan(var1x.blockPosition(), 160.0)) {
            for(int var3 = 0; var3 < var2.goals().size(); ++var3) {
               DebugGoalInfo.DebugGoal var4 = (DebugGoalInfo.DebugGoal)var2.goals().get(var3);
               double var5 = (double)var1x.getBlockX() + 0.5;
               double var7 = var1x.getY() + 2.0 + (double)var3 * 0.25;
               double var9 = (double)var1x.getBlockZ() + 0.5;
               int var11x = var4.isRunning() ? -16711936 : -3355444;
               Gizmos.billboardText(var4.name(), new Vec3(var5, var7, var9), TextGizmo.Style.forColorAndCentered(var11x));
            }
         }

      });
   }
}
