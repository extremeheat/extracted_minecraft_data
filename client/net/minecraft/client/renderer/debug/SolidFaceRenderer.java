package net.minecraft.client.renderer.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SolidFaceRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public SolidFaceRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      Level var10 = this.minecraft.player.level();
      BlockPos var11 = BlockPos.containing(var1, var3, var5);

      for(BlockPos var13 : BlockPos.betweenClosed(var11.offset(-6, -6, -6), var11.offset(6, 6, 6))) {
         BlockState var14 = var10.getBlockState(var13);
         if (!var14.is(Blocks.AIR)) {
            VoxelShape var15 = var14.getShape(var10, var13);

            for(AABB var17 : var15.toAabbs()) {
               AABB var18 = var17.move(var13).inflate(0.002);
               int var19 = -2130771968;
               Vec3 var20 = var18.getMinPosition();
               Vec3 var21 = var18.getMaxPosition();
               addFaceIfSturdy(var13, var14, var10, Direction.WEST, var20, var21, -2130771968);
               addFaceIfSturdy(var13, var14, var10, Direction.SOUTH, var20, var21, -2130771968);
               addFaceIfSturdy(var13, var14, var10, Direction.EAST, var20, var21, -2130771968);
               addFaceIfSturdy(var13, var14, var10, Direction.NORTH, var20, var21, -2130771968);
               addFaceIfSturdy(var13, var14, var10, Direction.DOWN, var20, var21, -2130771968);
               addFaceIfSturdy(var13, var14, var10, Direction.UP, var20, var21, -2130771968);
            }
         }
      }

   }

   private static void addFaceIfSturdy(BlockPos var0, BlockState var1, BlockGetter var2, Direction var3, Vec3 var4, Vec3 var5, int var6) {
      if (var1.isFaceSturdy(var2, var0, var3)) {
         Gizmos.rect(var4, var5, var3, GizmoStyle.fill(var6));
      }

   }
}
