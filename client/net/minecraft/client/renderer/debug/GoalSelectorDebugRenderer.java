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

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      Camera var9 = this.minecraft.gameRenderer.getMainCamera();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      BlockPos var10 = new BlockPos(var9.getPosition().field_414, 0.0D, var9.getPosition().field_416);
      this.goalSelectors.forEach((var1x, var2x) -> {
         for(int var3 = 0; var3 < var2x.size(); ++var3) {
            GoalSelectorDebugRenderer.DebugGoal var4 = (GoalSelectorDebugRenderer.DebugGoal)var2x.get(var3);
            if (var10.closerThan(var4.pos, 160.0D)) {
               double var5 = (double)var4.pos.getX() + 0.5D;
               double var7 = (double)var4.pos.getY() + 2.0D + (double)var3 * 0.25D;
               double var9 = (double)var4.pos.getZ() + 0.5D;
               int var11 = var4.isRunning ? -16711936 : -3355444;
               DebugRenderer.renderFloatingText(var4.name, var5, var7, var9, var11);
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
