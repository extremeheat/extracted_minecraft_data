package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.GoalDebugPayload;

public class GoalSelectorDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int MAX_RENDER_DIST = 160;
   private final Minecraft minecraft;
   private final Int2ObjectMap<EntityGoalInfo> goalSelectors = new Int2ObjectOpenHashMap();

   public void clear() {
      this.goalSelectors.clear();
   }

   public void addGoalSelector(int var1, BlockPos var2, List<GoalDebugPayload.DebugGoal> var3) {
      this.goalSelectors.put(var1, new EntityGoalInfo(var2, var3));
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
      BlockPos var10 = BlockPos.containing(var9.getPosition().x, 0.0, var9.getPosition().z);
      ObjectIterator var11 = this.goalSelectors.values().iterator();

      while(true) {
         EntityGoalInfo var12;
         BlockPos var13;
         do {
            if (!var11.hasNext()) {
               return;
            }

            var12 = (EntityGoalInfo)var11.next();
            var13 = var12.entityPos;
         } while(!var10.closerThan(var13, 160.0));

         for(int var14 = 0; var14 < var12.goals.size(); ++var14) {
            GoalDebugPayload.DebugGoal var15 = (GoalDebugPayload.DebugGoal)var12.goals.get(var14);
            double var16 = (double)var13.getX() + 0.5;
            double var18 = (double)var13.getY() + 2.0 + (double)var14 * 0.25;
            double var20 = (double)var13.getZ() + 0.5;
            int var22 = var15.isRunning() ? -16711936 : -3355444;
            DebugRenderer.renderFloatingText(var1, var2, var15.name(), var16, var18, var20, var22);
         }
      }
   }

   static record EntityGoalInfo(BlockPos entityPos, List<GoalDebugPayload.DebugGoal> goals) {
      final BlockPos entityPos;
      final List<GoalDebugPayload.DebugGoal> goals;

      EntityGoalInfo(BlockPos entityPos, List<GoalDebugPayload.DebugGoal> goals) {
         super();
         this.entityPos = entityPos;
         this.goals = goals;
      }

      public BlockPos entityPos() {
         return this.entityPos;
      }

      public List<GoalDebugPayload.DebugGoal> goals() {
         return this.goals;
      }
   }
}
