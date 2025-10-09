package net.minecraft.client.renderer.debug;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugPoiInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;

public class PoiDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int MAX_RENDER_DIST_FOR_POI_INFO = 30;
   private static final float TEXT_SCALE = 0.32F;
   private static final int ORANGE = -23296;
   private final BrainDebugRenderer brainRenderer;

   public PoiDebugRenderer(BrainDebugRenderer var1) {
      super();
      this.brainRenderer = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      BlockPos var10 = BlockPos.containing(var1, var3, var5);
      var7.forEachBlock(DebugSubscriptions.POIS, (var3x, var4) -> {
         if (var10.closerThan(var3x, 30.0)) {
            highlightPoi(var3x);
            this.renderPoiInfo(var4, var7);
         }

      });
      this.brainRenderer.getGhostPois(var7).forEach((var3x, var4) -> {
         if (var7.getBlockValue(DebugSubscriptions.POIS, var3x) == null) {
            if (var10.closerThan(var3x, 30.0)) {
               this.renderGhostPoi(var3x, var4);
            }

         }
      });
   }

   private static void highlightPoi(BlockPos var0) {
      float var1 = 0.05F;
      Gizmos.cuboid(var0, 0.05F, GizmoStyle.fill(ARGB.colorFromFloat(0.3F, 0.2F, 0.2F, 1.0F)));
   }

   private void renderGhostPoi(BlockPos var1, List<String> var2) {
      float var3 = 0.05F;
      Gizmos.cuboid(var1, 0.05F, GizmoStyle.fill(ARGB.colorFromFloat(0.3F, 0.2F, 0.2F, 1.0F)));
      Gizmos.billboardTextOverBlock(var2.toString(), var1, 0, -256, 0.32F);
      Gizmos.billboardTextOverBlock("Ghost POI", var1, 1, -65536, 0.32F);
   }

   private void renderPoiInfo(DebugPoiInfo var1, DebugValueAccess var2) {
      int var3 = 0;
      if (SharedConstants.DEBUG_BRAIN) {
         List var4 = this.getTicketHolderNames(var1, false, var2);
         if (var4.size() < 4) {
            renderTextOverPoi("Owners: " + String.valueOf(var4), var1, var3, -256);
         } else {
            renderTextOverPoi(var4.size() + " ticket holders", var1, var3, -256);
         }

         ++var3;
         List var5 = this.getTicketHolderNames(var1, true, var2);
         if (var5.size() < 4) {
            renderTextOverPoi("Candidates: " + String.valueOf(var5), var1, var3, -23296);
         } else {
            renderTextOverPoi(var5.size() + " potential owners", var1, var3, -23296);
         }

         ++var3;
      }

      renderTextOverPoi("Free tickets: " + var1.freeTicketCount(), var1, var3, -256);
      ++var3;
      renderTextOverPoi(var1.poiType().getRegisteredName(), var1, var3, -1);
   }

   private static void renderTextOverPoi(String var0, DebugPoiInfo var1, int var2, int var3) {
      Gizmos.billboardTextOverBlock(var0, var1.pos(), var2, var3, 0.32F);
   }

   private List<String> getTicketHolderNames(DebugPoiInfo var1, boolean var2, DebugValueAccess var3) {
      ArrayList var4 = new ArrayList();
      var3.forEachEntity(DebugSubscriptions.BRAINS, (var3x, var4x) -> {
         boolean var5 = var2 ? var4x.hasPotentialPoi(var1.pos()) : var4x.hasPoi(var1.pos());
         if (var5) {
            var4.add(DebugEntityNameGenerator.getEntityName(var3x.getUUID()));
         }

      });
      return var4;
   }
}
