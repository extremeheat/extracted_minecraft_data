package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.world.phys.Vec3;

public class GameTestBlockHighlightRenderer {
   private static final int SHOW_POS_DURATION_MS = 10000;
   private static final float PADDING = 0.02F;
   private final Map<BlockPos, Marker> markers = Maps.newHashMap();

   public GameTestBlockHighlightRenderer() {
      super();
   }

   public void highlightPos(BlockPos var1, BlockPos var2) {
      String var3 = var2.toShortString();
      this.markers.put(var1, new Marker(1610678016, var3, Util.getMillis() + 10000L));
   }

   public void clear() {
      this.markers.clear();
   }

   public void emitGizmos() {
      long var1 = Util.getMillis();
      this.markers.entrySet().removeIf((var2) -> var1 > ((Marker)var2.getValue()).removeAtTime);
      this.markers.forEach((var1x, var2) -> this.renderMarker(var1x, var2));
   }

   private void renderMarker(BlockPos var1, Marker var2) {
      Gizmos.cuboid(var1, 0.02F, GizmoStyle.fill(var2.color()));
      if (!var2.text.isEmpty()) {
         Gizmos.billboardText(var2.text, Vec3.atLowerCornerWithOffset(var1, 0.5, 1.2, 0.5), TextGizmo.Style.whiteAndCentered().withScale(0.16F)).setAlwaysOnTop();
      }

   }

   static record Marker(int color, String text, long removeAtTime) {
      final String text;
      final long removeAtTime;

      Marker(int var1, String var2, long var3) {
         super();
         this.color = var1;
         this.text = var2;
         this.removeAtTime = var3;
      }
   }
}
