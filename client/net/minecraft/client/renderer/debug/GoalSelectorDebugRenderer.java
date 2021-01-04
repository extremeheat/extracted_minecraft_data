package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class GoalSelectorDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map<Integer, List<GoalSelectorDebugRenderer.DebugGoal>> goalSelectors = Maps.newHashMap();

   public void clear() {
      this.goalSelectors.clear();
   }

   public void addGoalSelector(int var1, List<GoalSelectorDebugRenderer.DebugGoal> var2) {
      this.goalSelectors.put(var1, var2);
   }

   public GoalSelectorDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(long var1) {
      Camera var3 = this.minecraft.gameRenderer.getMainCamera();
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture();
      BlockPos var4 = new BlockPos(var3.getPosition().x, 0.0D, var3.getPosition().z);
      this.goalSelectors.forEach((var1x, var2) -> {
         for(int var3 = 0; var3 < var2.size(); ++var3) {
            GoalSelectorDebugRenderer.DebugGoal var4x = (GoalSelectorDebugRenderer.DebugGoal)var2.get(var3);
            if (var4.closerThan(var4x.pos, 160.0D)) {
               double var5 = (double)var4x.pos.getX() + 0.5D;
               double var7 = (double)var4x.pos.getY() + 2.0D + (double)var3 * 0.25D;
               double var9 = (double)var4x.pos.getZ() + 0.5D;
               int var11 = var4x.isRunning ? -16711936 : -3355444;
               DebugRenderer.renderFloatingText(var4x.name, var5, var7, var9, var11);
            }
         }

      });
      GlStateManager.enableDepthTest();
      GlStateManager.enableTexture();
      GlStateManager.popMatrix();
   }

   public static class DebugGoal {
      public final BlockPos pos;
      public final int priority;
      public final String name;
      public final boolean isRunning;

      public DebugGoal(BlockPos var1, int var2, String var3, boolean var4) {
         super();
         this.pos = var1;
         this.priority = var2;
         this.name = var3;
         this.isRunning = var4;
      }
   }
}
