package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;

public class GoalSelectorDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int MAX_RENDER_DIST = 160;
   private final Minecraft minecraft;
   private final Map<Integer, List<GoalSelectorDebugRenderer.DebugGoal>> goalSelectors = Maps.newHashMap();

   @Override
   public void clear() {
      this.goalSelectors.clear();
   }

   public void addGoalSelector(int var1, List<GoalSelectorDebugRenderer.DebugGoal> var2) {
      this.goalSelectors.put(var1, var2);
   }

   public void removeGoalSelector(int var1) {
      this.goalSelectors.remove(var1);
   }

   public GoalSelectorDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   @Override
   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      Camera var9 = this.minecraft.gameRenderer.getMainCamera();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos var10 = new BlockPos(var9.getPosition().x, 0.0, var9.getPosition().z);
      this.goalSelectors.forEach((var1x, var2x) -> {
         for(int var3x = 0; var3x < var2x.size(); ++var3x) {
            GoalSelectorDebugRenderer.DebugGoal var4 = var2x.get(var3x);
            if (var10.closerThan(var4.pos, 160.0)) {
               double var5x = (double)var4.pos.getX() + 0.5;
               double var7x = (double)var4.pos.getY() + 2.0 + (double)var3x * 0.25;
               double var9x = (double)var4.pos.getZ() + 0.5;
               int var11 = var4.isRunning ? -16711936 : -3355444;
               DebugRenderer.renderFloatingText(var4.name, var5x, var7x, var9x, var11);
            }
         }
      });
      RenderSystem.enableDepthTest();
      RenderSystem.enableTexture();
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
