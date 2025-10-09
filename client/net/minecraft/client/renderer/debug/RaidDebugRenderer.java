package net.minecraft.client.renderer.debug;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.Vec3;

public class RaidDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int MAX_RENDER_DIST = 160;
   private static final float TEXT_SCALE = 0.64F;
   private final Minecraft minecraft;

   public RaidDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      BlockPos var10 = this.getCamera().blockPosition();
      var7.forEachChunk(DebugSubscriptions.RAIDS, (var1x, var2) -> {
         for(BlockPos var4 : var2) {
            if (var10.closerThan(var4, 160.0)) {
               highlightRaidCenter(var4);
            }
         }

      });
   }

   private static void highlightRaidCenter(BlockPos var0) {
      Gizmos.cuboid(var0, GizmoStyle.fill(ARGB.colorFromFloat(0.15F, 1.0F, 0.0F, 0.0F)));
      renderTextOverBlock("Raid center", var0, -65536);
   }

   private static void renderTextOverBlock(String var0, BlockPos var1, int var2) {
      Gizmos.billboardText(var0, Vec3.atLowerCornerWithOffset(var1, 0.5, 1.3, 0.5), TextGizmo.Style.forColor(var2).withScale(0.64F)).setAlwaysOnTop();
   }

   private Camera getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }
}
