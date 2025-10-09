package net.minecraft.client.renderer.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Octree;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.MutableInt;

public class OctreeDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public OctreeDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      Octree var10 = this.minecraft.levelRenderer.getSectionOcclusionGraph().getOctree();
      MutableInt var11 = new MutableInt(0);
      var10.visitNodes((var2, var3x, var4, var5x) -> this.renderNode(var2, var4, var3x, var11, var5x), var8, 32);
   }

   private void renderNode(Octree.Node var1, int var2, boolean var3, MutableInt var4, boolean var5) {
      AABB var6 = var1.getAABB();
      double var7 = var6.getXsize();
      long var9 = Math.round(var7 / 16.0);
      if (var9 == 1L) {
         var4.add(1);
         int var11 = var5 ? -16711936 : -1;
         Gizmos.billboardText(String.valueOf(var4.getValue()), var6.getCenter(), TextGizmo.Style.forColorAndCentered(var11).withScale(4.8F));
      }

      long var13 = var9 + 5L;
      Gizmos.cuboid(var6.deflate(0.1 * (double)var2), GizmoStyle.stroke(ARGB.colorFromFloat(var3 ? 0.4F : 1.0F, getColorComponent(var13, 0.3F), getColorComponent(var13, 0.8F), getColorComponent(var13, 0.5F))));
   }

   private static float getColorComponent(long var0, float var2) {
      float var3 = 0.1F;
      return Mth.frac(var2 * (float)var0) * 0.9F + 0.1F;
   }
}
