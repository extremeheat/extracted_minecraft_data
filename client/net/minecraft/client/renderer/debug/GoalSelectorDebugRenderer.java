package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.debug.DebugGoalInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;

public class GoalSelectorDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int MAX_RENDER_DIST = 160;
   private final Minecraft minecraft;

   public GoalSelectorDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9, Frustum var10) {
      Camera var11 = this.minecraft.gameRenderer.getMainCamera();
      BlockPos var12 = BlockPos.containing(var11.getPosition().x, 0.0, var11.getPosition().z);
      var9.forEachEntity(DebugSubscriptions.GOAL_SELECTORS, (var3x, var4) -> {
         if (var12.closerThan(var3x.blockPosition(), 160.0)) {
            for(int var5 = 0; var5 < var4.goals().size(); ++var5) {
               DebugGoalInfo.DebugGoal var6 = (DebugGoalInfo.DebugGoal)var4.goals().get(var5);
               double var7 = (double)var3x.getBlockX() + 0.5;
               double var9 = var3x.getY() + 2.0 + (double)var5 * 0.25;
               double var11 = (double)var3x.getBlockZ() + 0.5;
               int var13 = var6.isRunning() ? -16711936 : -3355444;
               DebugRenderer.renderFloatingText(var1, var2, var6.name(), var7, var9, var11, var13);
            }
         }

      });
   }
}
