package net.minecraft.client.renderer.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WaterDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public WaterDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      BlockPos var10 = this.minecraft.player.blockPosition();
      Level var11 = this.minecraft.player.level();

      for(BlockPos var13 : BlockPos.betweenClosed(var10.offset(-10, -10, -10), var10.offset(10, 10, 10))) {
         FluidState var14 = var11.getFluidState(var13);
         if (var14.is(FluidTags.WATER)) {
            double var15 = (double)((float)var13.getY() + var14.getHeight(var11, var13));
            Gizmos.cuboid(new AABB((double)((float)var13.getX() + 0.01F), (double)((float)var13.getY() + 0.01F), (double)((float)var13.getZ() + 0.01F), (double)((float)var13.getX() + 0.99F), var15, (double)((float)var13.getZ() + 0.99F)), GizmoStyle.fill(ARGB.colorFromFloat(0.15F, 0.0F, 1.0F, 0.0F)));
         }
      }

      for(BlockPos var18 : BlockPos.betweenClosed(var10.offset(-10, -10, -10), var10.offset(10, 10, 10))) {
         FluidState var19 = var11.getFluidState(var18);
         if (var19.is(FluidTags.WATER)) {
            Gizmos.billboardText(String.valueOf(var19.getAmount()), Vec3.atLowerCornerWithOffset(var18, 0.5, (double)var19.getHeight(var11, var18), 0.5), TextGizmo.Style.forColorAndCentered(-16777216));
         }
      }

   }
}
