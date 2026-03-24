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

   public GoalSelectorDebugRenderer(final Minecraft minecraft) {
      super();
      this.minecraft = minecraft;
   }

   public void emitGizmos(final double camX, final double camY, final double camZ, final DebugValueAccess debugValues, final Frustum frustum, final float partialTicks) {
      Camera camera = this.minecraft.gameRenderer.getMainCamera();
      BlockPos playerPos = BlockPos.containing(camera.position().x, 0.0, camera.position().z);
      debugValues.forEachEntity(DebugSubscriptions.GOAL_SELECTORS, (entity, goalInfo) -> {
         if (playerPos.closerThan(entity.blockPosition(), 160.0)) {
            for(int i = 0; i < goalInfo.goals().size(); ++i) {
               DebugGoalInfo.DebugGoal goal = (DebugGoalInfo.DebugGoal)goalInfo.goals().get(i);
               double x = (double)entity.getBlockX() + 0.5;
               double y = entity.getY() + 2.0 + (double)i * 0.25;
               double z = (double)entity.getBlockZ() + 0.5;
               int color = goal.isRunning() ? -16711936 : -3355444;
               Gizmos.billboardText(goal.name(), new Vec3(x, y, z), TextGizmo.Style.forColorAndCentered(color));
            }
         }

      });
   }
}
